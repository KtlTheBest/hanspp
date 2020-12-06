
package semantics;

// this is where we check ASTs.

public class FunctionChecker
{
   cflat.Program prog;
   java.lang.String funcname;  
   VarStack localvars;

   public final static int impossible = 99999999;
   public final static int outputlevel = 0;
      // 0 is silent
      // 1 is moderate
      // 2 is much 
      // 3 is irritating.

   public FunctionChecker( cflat.Program prog, 
                           java.lang.String funcname, 
                           cflat.Program.FuncDef def ) 
   {
      this. prog = prog;
      this. funcname = funcname;

      localvars = new VarStack( ); 

      // Create a local variable called [retvar] for the return value: 

      localvars. push( "[retvar]", 1, def. returntype ); 
         // The 1 is the size of the return type, which is completely
         // irrelevant here, so we just put 1. 

      // Create parameters as local variables:

      int i = def. parameters. nrFields( ); 
      while( i -- != 0 )  
      {
         localvars. push( def. parameters. getName(i), 1, 
                          def. parameters. getType(i) );
      }
   }


   // This function reassigns expressions in stat, during type checking,
   // using the fact that Java has reference semantics. 

   public void 
   checkStatement( ast.Tree stat ) 
   throws Error
   {
      if( outputlevel >= 2 )
      {
         System.out.println( localvars ); 
         System.out.println( "checking " + stat );
      }

      if( stat instanceof ast.Apply )
      {
         ast.Apply appl = (ast.Apply) stat;

         if( appl. function. equals( "[compound]" ))
         {
            int s = localvars. nrVariables( ); 
            for( int i = 0; i != appl. sub. length; ++ i )
            {
               checkStatement( appl. sub[i] );
            }
            localvars. restore(s); 
            return;  
         }

         if( appl. function. equals( "[decl]" ) && 
             appl. sub. length == 1 )
         {
            // We check sanity of the type. If it passes,
            // we declare the variable. Lots of casts unfortunately. 

            SanityChecks.checkwellformed( prog. structdefs, 
                                          "function " + funcname, 
                                          appl. type );
            ast.Identifier id = (ast.Identifier) appl. sub[0]; 
            localvars. push( id. id, 1, appl. type ); 
            return; 
         }

         if( appl. function. equals( "[expr]" ) &&
             appl. sub. length == 1 )
         {
            appl. sub[0] = checkExpr( appl. sub[0] ); 
            return; 
         }

         if( appl. function. equals( "[while]" ) &&
             appl. sub. length == 2 ) 
         {
            ast.Tree cond = checkExpr( appl. sub[0] );
            cond = makeRValue( cond );
            if( penalty( cond.type, new type.Bool( )) >= impossible )
               throw new Error.checkExpr( "function " + funcname, appl, 
                                "while condition not convertible to Bool" );  
            cond = convert( cond, new type.Bool( )); 

            appl.sub[0] = cond;
            checkStatement( appl.sub[1] );
            return;
         }

         if( appl. function. equals( "[if]" ) &&
                ( appl. sub. length == 2 || appl. sub. length == 3 )) 
         {
            ast.Tree cond = checkExpr( appl. sub[0] );
            cond = makeRValue( cond );
            if( penalty( cond.type, new type.Bool( )) >= impossible )
               throw new Error.checkExpr( "function " + funcname, appl,
                                    "if condition not convertible to Bool" );
            cond = convert( cond, new type.Bool( ));

            appl.sub[0] = cond;
            checkStatement( appl.sub[1] );
            if( appl. sub. length == 3 )
               checkStatement( appl. sub[2] ); 
            return;  
         }

         if( appl. function. equals( "[return]" ) &&
             appl. sub. length == 0 )
         {
            // We know that the return value has index 0:

            if( !( localvars. getType(0) instanceof type.Void ))
            {
               throw new Error.checkStatement( "function " + funcname,
                     appl, "empty return not allowed in non-void function" );
            }
 
            return; 
         }
 
         if( appl. function. equals( "[return]" ) &&
             appl. sub. length == 1 )
         {
            ast.Tree sub = checkExpr( appl. sub[0] ); 

            type.Type neededtype = localvars. getType(0);
               // We know that the return value has index 0.

            if( penalty( sub. type, neededtype ) >= impossible )
            {
               throw new Error.checkStatement( "function " + funcname, 
                  appl, "returned type " + sub.type + 
                        " is not convertible to " +
                        neededtype );    
            }

            sub = makeRValue( sub );
            sub = convert( sub, neededtype );

            // We replace the statement by : 
            // begin 
            //    [retvar] = sub; 
            //    return 
            // end 

            appl. function = "[compound]"; 

            ast.Identifier retvar = new ast.Identifier( "[retvar]" );
            retvar. type = neededtype;
            retvar. lr = 'L';
           
            ast.Apply assign = new ast.Apply( "[assign]", retvar, sub );
            assign. type = neededtype;
            assign. lr = 'R'; 

            appl. sub = new ast.Tree[] 
               { new ast.Apply( "[expr]", assign ), 
                 new ast.Apply( "[return]" ) };

            return; 
         }

         if( appl. function. equals( "[print]" ) &&
             appl. sub. length == 1 )
         {
            // If the argument is a string, we just accept it,
            // otherwise, we check the argument with checkExpr( ). 

            if( appl.sub[0] instanceof ast.String )
               return;

            ast.Tree arg = checkExpr( appl. sub[0] );
            if( arg.type instanceof type.Bool ||
                arg.type instanceof type.Char ||
                arg.type instanceof type.Integer ||
                arg.type instanceof type.Double )
            {
               checkExpr( appl.sub[0] );
               arg = makeRValue( arg ); 
               appl.sub[0] = arg;
               return; 
            } 
         }

         if( appl. function. equals( "[newline]" ) &&
             appl. sub. length == 0 )
         {
            return;   // There is nothing to check. 
         }

         if( appl. function. equals( "[trace]" ) &&
             appl. sub. length == 0 )
         {
            return;   // There is nothing to check. 
         }
      }

      throw new Error.checkStatement( "function " + funcname,
                                      stat, "unknown statement type" ); 
   }



   
   ast.Tree checkUserFunc( ast.Tree appl, java.lang.String name,
                           ast.Tree[] arguments, 
                           cflat.Program.FuncDef definition )
   throws Error 
   {
      System.out.println( "Checking User function " + name );

      if( definition. parameters. nrFields( ) != arguments. length )
      {
         throw new Error.checkExpr( "function " + funcname, appl, 
             "wrong number of parameters, correct number is " + 
                 definition.parameters. nrFields( )); 
      }

      java.util.ArrayList< ast.Tree > checked = new java.util.ArrayList<> ( ); 
  
      for( int i = 0; i != arguments. length; ++ i )
      {
         ast.Tree arg = checkExpr( arguments[i] );
         type.Type needed = definition. parameters. getType(i);

         if( penalty( arg. type, needed ) >= impossible )
            throw new Error.checkExpr( "function " + funcname, appl,
               "type " + arg. type + 
               " of argument nr" + (i+1) + " not convertible to " + needed ); 

         arg = makeRValue( arg ); 
         arg = convert( arg, needed ); 
         checked. add( arg );
      }

      
      ast.Apply result = 
         new ast.Apply( name, checked.toArray( new ast.Tree[0] )); 

      result. type = definition. returntype; 
      result. lr = 'R';
      return result;
   }

   // The conversion sequence in the slides:
 
   public static int insequence( type.Type t )
   {
      if( t instanceof type.Bool ) return 1;
      if( t instanceof type.Char) return 2;
      if( t instanceof type.Integer ) return 3;
      if( t instanceof type.Double ) return 4;
      return -1;
   }

   // Almost the same as in the slides, except that we allow conversion
   // from pointer(void) to other pointers.

   public static int penalty( type.Type from, type.Type to )
   {
      if( from. equals( to ))
         return 0;
 
      int fromtype = insequence( from );
      int totype = insequence( to );

      if( fromtype >= 0 && totype >= 0 )
      {
         if( fromtype < totype ) return totype - fromtype;
         if( fromtype > totype ) return 10 * ( fromtype - totype );
      } 

      if( from instanceof type.Pointer && to instanceof type.Pointer )
      {
         // If from is Pointer(Void), we allow it with cost 1:

         if( ((type.Pointer) from ).tp instanceof type.Void )
         {
            return 1; 
         }
      }

      return impossible; 
   }


   // If t has type tp, we just return t.
   // Otherwise, return conv_{tp}(t). 

   public static ast.Tree convert( ast.Tree t, type.Type tp )  
   {
      if( t. type. equals( tp ))
         return t;

      ast.Tree res = new ast.Apply( "[conv]", t );
      res. type = tp;
      res. lr = 'R';
      return res;
   }

   public static ast.Tree makeRValue( ast.Tree t )
   {
      if( t. lr == 'L' )
      {
         ast.Tree res = new ast.Apply( "[load]", t );
         res. type = t. type;
         res. lr = 'R';
         return res;
      }
      else
         return t;
   }


   // If the type of t has form array(n,T)/lval, we construct
   // [conv](t) with type pntr(T)/rval:

   public static ast.Tree array2pointer( ast.Tree t )
   {
      if( t. lr == 'L' && t. type instanceof type.Array )
      {
         type.Type tp = ((type.Array) t. type ). tp; 
         ast.Apply res = new ast.Apply( "[conv]", t );

         res.type = new type.Pointer( tp ); 
         res.lr = 'R';
         return res; 
      }
      else
         return t;
   }


   // In the slides, we did it different, but I think that
   // this handles most cases in the best way:
   // We return the common type to which both tp1 and tp2 can be converted
   // at lowest common cost.
   // We need to return something, when there is no common type.
   // For this we use type.Void, since nothing is convertible to Void.

   public static type.Type bestcommontype( type.Type tp1, type.Type tp2 ) 
   {
      if( outputlevel >= 3 )  
      {
         System.out.println( "finding best common type between:\n" + tp1 + 
                             "and\n" + tp2 ); 
      }

      if( tp1. equals( tp2 ))
         return tp1;
      else
      {
         int c12 = penalty( tp1, tp2 );
         int c21 = penalty( tp2, tp1 );
         if( c12 < c21 && c12 < impossible )
            return tp2; 
         if( c21 < c12 && c21 < impossible )
            return tp1;

         return new type.Void( );
      }
   } 
}




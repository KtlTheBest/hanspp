
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

  public ast.Tree
    checkExpr( ast.Tree expr ) throws Error 
    {
      if( outputlevel >= 2 )
      {
        System. out. println( localvars );
        System. out. println( "checking Expr: " + expr );
      }

      if( expr instanceof ast.Identifier )
      {
        ast.Identifier id = (ast.Identifier) expr; 

        // We try to find as local variable:

        if( localvars. contains( id. id ))
        {
          // We don't change id. We make a clone and modify the clone. 

          ast.Identifier res = id. clone( ); 
          int offset = localvars. getIndex(id. id);
          res. type = localvars. getType( offset );
          res. lr = 'L';
          return array2pointer( res ); 
        }

        // We try to find id as constant:

        ast.Tree val = prog.constdefs.get( id. id );
        if( val != null )
        {
          return checkExpr( val ); 
          // Here is essential that checkExpr does not 
          // modify val. 
        }

        throw new Error.checkExpr( "function " + funcname, expr,
            "identifier " + id. id + 
            " is not a local variable or constant" );
      }

      // All possible constant types:

      if( expr instanceof ast.Bool )
      {
        ast.Tree res = expr. clone( ); 
        res.type = new type.Bool( );
        res.lr = 'R';
        return res; 
      }

      if( expr instanceof ast.Char )
      {
        ast.Tree res = expr. clone( );
        res.type = new type.Char( );
        res.lr = 'R';
        return res;
      }

      if( expr instanceof ast.Integer )
      {
        ast.Tree res = expr. clone( );
        res.type = new type.Integer( );
        res.lr = 'R';
        return res;  
      }

      if( expr instanceof ast.Double )
      {
        ast.Tree res = expr. clone( );
        res.type = new type.Double( );
        res.lr = 'R';
        return res; 
      }

      if( expr instanceof ast.Pointer )
      {
        ast.Tree res = expr. clone( );
        res.type = new type.Pointer( new type.Void( ));
        res.lr = 'R';
        return res;
      }

      if( expr instanceof ast.Apply )
      {
        ast.Apply appl = (ast.Apply) expr;  

        cflat.Program.FuncDef 
          definition = prog. funcdefs. get( appl. function );

        if( definition != null )
          return checkUserFunc( appl, appl.function, appl.sub, definition ); 

        if( appl. sub. length == 1 ) 
          return checkUnary( appl, appl. function, 
              checkExpr( appl.sub[0] ));

        if( appl. sub. length == 2 )
          return checkBinary( appl, appl. function, 
              checkExpr( appl.sub[0] ),
              checkExpr( appl.sub[1] ));

        if( appl. sub. length == 3 )
          return checkTernary( appl, appl. function,
              checkExpr( appl.sub[0] ),
              checkExpr( appl.sub[1] ),
              checkExpr( appl.sub[2] )); 
      }

      if( expr instanceof ast.Select )
      {
        java.lang.String field = ( (ast.Select) expr ). field; 
        ast.Tree sub = checkExpr( ( (ast.Select) expr ). sub ); 

        if( ! ( sub. type instanceof type.Struct ))
        {
          throw new Error.checkExpr( "function " + funcname,
              expr, 
              "type " + sub.type +
              " is not struct" ); 
        }

        java.lang.String structname = ( (type.Struct) sub.type ). name; 


        // This probably cannot happen:

        if( ! prog. structdefs. contains( structname ))
        {
          throw new Error.checkExpr( "function " + funcname,
              expr,
              "name " + structname + 
              " has no struct definition" );
        }

        if( ! prog. structdefs. hasfield( structname, field ))
        {
          prog. structdefs. printfields( structname );
          throw new Error.checkExpr( " function " + funcname,
              expr, 
              "struct " + structname + 
              " has no field " + field );
        } 

        int position = prog.structdefs.position( structname, field );
        type.Type tp = prog.structdefs.fieldtype( structname, position );

        ast.Select result = new ast.Select( field, sub );
        result. type = tp;
        result. lr = sub. lr; 
        result. index = position;
        return array2pointer( result );  
      }

      throw new Error.checkExpr( "function " + funcname, 
          expr, " unknown tree type" );  
    }
    
    
  private ast.Tree extractTypeFromPointer(ast.Tree sub){
    type.Type tp = sub.type;

    if(tp instanceof type.Pointer){
      type.Pointer pntr = (type.Pointer) tp;
      type.Type trueType = pntr.tp;

      ast.Apply res = new ast.Apply("[conv]", sub);
      res.type = trueType;
      if(trueType instanceof type.Pointer) res.lr = 'R';
      else res.lr = sub.lr;
      return res; 
    } else {  
      return sub;
    }
  }

  ast.Tree checkUnary( ast.Tree appl, java.lang.String unary,
      ast.Tree sub ) 
      throws Error 
    {
      if( outputlevel >= 1 )
      {
        System. out. println( "unary " + appl );
        System. out. println( "checked subtree:" );
        System.out. println( sub );
      }

      if( unary. equals( "[mmx]" ) || unary. equals( "[ppx]" ) ||
          unary. equals( "[xmm]" ) || unary. equals( "[xpp]" ))
      {
        type.Type tp = sub. type;  

        if( sub. lr != 'L' )
          throw new Error.checkExpr( "function " + funcname,
              appl, "subtree is not L-value" );

        SanityChecks.checkwellformed(
            prog. structdefs, 
            funcname, 
            sub. type);

        if(!(sub. type instanceof type.Double ||
              sub. type instanceof type.Integer ||
              sub. type instanceof type.Char ||
              sub. type instanceof type.Pointer)){
          throw new Error.checkExpr( "function " + funcname,
              appl, "Can't perform " + unary + " on type " + sub. type. toString());
              }

        ast. Tree res = makeRValue(sub);
        return res;
      }

      if(unary.equals("pntr")){
        type.Type tp = sub. type;
        sub = makeRValue(sub);

        SanityChecks.checkwellformed(
            prog. structdefs, 
            funcname, 
            sub. type);

        if( !( tp instanceof type.Pointer ) )
        {
          throw new Error.checkExpr( "function " + funcname,
              appl, "Can't perform " + unary + " on type " + sub. type. toString());
        }

        ast. Tree res = new ast.Apply( "[conv]", sub );
        res.type = ((type.Pointer) tp).tp;

        res.lr = 'L';
        return res;
      }

      if(unary.equals("amp")){

        type.Type tp = sub. type;

        SanityChecks.checkwellformed(
            prog. structdefs, 
            funcname, 
            sub. type);

        ast. Tree res = makeRValue(sub);        
        res = convert(sub, new type.Pointer(sub.type));

        return res;
      }
    
      if(unary.equals("neg")){

        type.Type tp = sub. type;

        SanityChecks.checkwellformed(
            prog. structdefs, 
            funcname, 
            sub. type);

        ast. Tree res = makeRValue(sub);
        return res;
      }

      throw new Error.checkExpr( "function " + funcname,
          appl, "unknown unary operator " + unary );

    }


  ast.Tree checkBinary( ast.Tree appl, java.lang.String binary,
      ast.Tree sub1, ast.Tree sub2 ) throws Error
  {

    System.out.println("Hello from Kurmankul: ");
    System.out.println(appl);

    if( outputlevel >= 2 )
    {
      System. out. println( "binary " + appl ); 
      System. out. println( "checked subtrees:" ); 
      System.out. println( sub1 );
      System.out. println( sub2 );
    }

    if( binary. equals( "=" )) 
    {
      // This is complete:

      if( sub1.lr != 'L' )
      {
        throw new Error.checkExpr( "function " + funcname, appl, 
            "first argument not Lvalue" );
      }

      int cost = penalty( sub2. type, sub1. type );
      if( cost >= impossible )
      {
        throw new Error.checkExpr( "function " + funcname,
            appl, "right type " + sub2.type + 
            "not convertible to left type " + 
            sub1.type ); 
      }

      sub2 = makeRValue( sub2 );
      sub2 = convert( sub2, sub1. type ); 

      ast.Tree res = new ast.Apply( "[assign]", sub1, sub2 );
      res. type = sub1. type;
      res. lr = 'R';
      return res;               
    }

    if( binary. equals( "eq" ) || binary. equals( "ne" ) ||
        binary. equals( "lt" ) || binary. equals( "gt" ) ||
        binary. equals( "le" ) || binary. equals( "ge" ))
    {
      sub1 = makeRValue( sub1 );
      sub2 = makeRValue( sub2 ); 

      int cost12 = penalty( sub1.type, sub2.type );
      int cost21 = penalty( sub2.type, sub1.type );

      if(cost12 < cost21 && cost12 < impossible){
        sub1 = convert( sub1, sub2.type );
      } else
        if(cost21 < cost12 && cost21 < impossible){
          sub2 = convert( sub2, sub1.type );
        }

      ast.Tree res = new ast.Apply(binary, sub1, sub2);
      res. type = new type.Bool();
      res. lr = 'R';

      return res;
    
    }

    if( binary. equals( "add" ) ||
        binary. equals( "sub" ) ||
        binary. equals( "mul" ) ||
        binary. equals( "truediv" ) ||
        binary. equals( "mod" ) )
    {
      System.out.println("Starting making RVal");
      if( ! (sub1.type instanceof type.Array) ) sub1 = makeRValue( sub1 );
      if( ! (sub2.type instanceof type.Array) ) sub2 = makeRValue( sub2 );
      System.out.println("Finished making RVal");

      System.out.println("Starting penalty");
      int cost12 = penalty( sub1.type, sub2.type );
      int cost21 = penalty( sub2.type, sub1.type );
      System.out.println("Finished penalty");

      if(sub1.type instanceof type.Array){
        sub1 = array2pointer( sub1 );
      }

      if(sub2.type instanceof type.Array){
        sub2 = array2pointer( sub2 );
      }

      type.Type trueType = sub1.type;
      System.out.println("Starting conversion checks");
      System.out.println("cost12: " + cost12);
      System.out.println("cost21: " + cost21);
      if(cost12 < cost21 && cost12 < impossible){
        sub1 = convert( sub1, sub2.type );
        trueType = sub2.type;
      } else
        if(cost21 < cost12 && cost21 < impossible){
          sub2 = convert( sub2, sub1.type );
          trueType = sub1.type;
        } else 
          if(cost12 == impossible && cost21 == impossible){
            if(sub1.type instanceof type.Pointer && sub2.type instanceof type.Integer){
              trueType = new type.Pointer( ((type.Pointer) sub1.type). tp );
            } else {
              System.out.println("Hello from Hans:");
              System.out.println("sub1:\n" + sub1);
              System.out.println("sub2:\n" + sub2);
              throw new Error.checkExpr("function " + funcname, appl,
                  "Incompatible types " + sub1.type.toString() + " and " + sub2.type.toString());
            }
          }
      System.out.println("Finished conversion checks");

      System.out.println("Starting constructing tree node");
      ast.Tree res = new ast.Apply(binary, sub1, sub2);
      res. type = trueType;
      res. lr = 'R';
      System.out.println("Finished constructing tree node");

      return res;
    } 

    throw new Error.checkExpr( "function " + funcname,
        appl, "unknown binary operator " + binary );
  }

  ast.Tree checkTernary( ast.Tree appl, java.lang.String ternary,
      ast.Tree sub1, ast.Tree sub2, ast.Tree sub3 )
      throws Error 
    {
      if( ternary. equals( "??" )) 
      {
        // It turns out that C++ accepts the following horror:

        // int p; int q;
        // bool b;

        // ( b ? p : q ) = 4;

        // Fortunately, C does not, so we convert our arguments into
        // Rvalues. 

        sub1 = makeRValue(sub1);
        sub2 = makeRValue(sub2);
        sub3 = makeRValue(sub3);

        int toBool = penalty(sub1. type, new type.Bool());

        if(toBool == impossible){
          throw new Error.checkExpr("function " + funcname, appl, "Cannot convert expression to bool");
        }

        sub1 = convert(sub1, new type.Bool());

        ast.Apply res = new ast.Apply("??", sub1, sub2, sub3);
        res.type = new type.Bool();
        res.lr = 'R';

        return res;
      } 
      throw new Error.checkExpr( "function " + funcname,
          appl, "unknown ternary operator " + ternary );

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
    System.out.println("Echo from Dnislam\n" + t);
    if( t. lr == 'L' && t. type instanceof type.Array )
    {
      System.out.println("Hello from Dnislam\n" + t);
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




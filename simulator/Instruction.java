
package simulator;

public abstract class Instruction
{

   public static final int BOOL = 1;
   public static final int CHAR = 2;
   public static final int INTEGER = 3;
   public static final int DOUBLE = 4;
   public static final int POINTER = 5;
   public static final int UNKNOWN = 6;
   public static final int UNDEFINED = 7;

   static int classify( ValType vt )
   {
      if( vt == null )
         return UNDEFINED;

      if( vt.type instanceof type.Bool )
         { return BOOL; }

      if( vt.type instanceof type.Char )  
         { return CHAR; }

      if( vt.type instanceof type.Integer )
         { return INTEGER; }

      if( vt.type instanceof type.Double )
         { return DOUBLE; }
      
      if( vt.type instanceof type.Pointer )
         { return POINTER; }

      return UNKNOWN;
   }


   // Check if op fits to the outcome of the comparison

   ValType cmp( java.lang.String op, int res )
   {
      System.out.println( "cmp " + op + " " + res );

      if( op == "ne" ) return 
         new ValType( new java.lang.Boolean( res != 0 ), 
                      new type.Bool( )); 

      return null;
   }

 
   //  Construct an error stating that we are not applicable: 

   Error notapplicable( State state, 
                        ValType ... args ) throws Error 
   {
      java.lang.String complaint = this. toString( );
      complaint += " is not applicable on types: "; 
      for( int i = 0; i != args. length; ++ i ) 
      {
         if( i != 0 ) complaint += ", ";
         if( args[i] != null )
            complaint += args[i]. type. toString( );
         else
            complaint += "(undefined)"; 
      }

      return new Error( state. position. funcname, complaint, 
                        state. position. blockname, state. position. i - 1 );
   }


   public static class Comment extends Instruction
   {
      java.lang.String comment; 
 
      public Comment( ) 
      {
         this. comment = "(none)";
      }

      public Comment( java.lang.String comment ) 
      {
         this. comment = comment;
      }

      void exec( type.StructStore structdefs, Program prog, 
                 Memory mem, java.util.ArrayDeque< State > statestack )
      {
      }

      public java.lang.String toString( java.lang.String prefix )
      {
         return prefix + "comment: " + comment; 
      }
   }

   public static class Trace extends Instruction
   {
      public Trace( )
      {
      }

      void exec( type.StructStore structdefs, Program prog,
                 Memory mem, java.util.ArrayDeque< State > statestack )
      {
         statestack. peek( ). tracing = true; 
      }

      public java.lang.String toString( java.lang.String prefix )
      {
         return prefix + "trace";
      }
   }

   public static class Dealloc extends Instruction
   {
      type.Type[] skipped;

      public Dealloc( type.Type ... skipped )
      {
         this. skipped = skipped;
      }
   
      void exec( type.StructStore structdefs, Program prog,
                 Memory mem, java.util.ArrayDeque< State > statestack ) 
      throws Error
      {
         int s = 0;
         for( type.Type t : skipped )
            s += t. sizeof( structdefs );

         System.out.println( "deallocate " + s ); 
         mem. deallocate(s); 
      }

      public java.lang.String toString( java.lang.String prefix )
      {
         java.lang.String res = prefix + "deallocate"; 
         for( int i = 0; i != skipped. length; ++ i )
         {
            res += " " + skipped[i];
         }
         return res;
      }
   }


   public static class Store extends Instruction
   {
      java.lang.String from;
      java.lang.String into;

      public Store( java.lang.String from,
                    java.lang.String into ) 
      {
         this. from = from; 
         this. into = into; 
      }

      void exec( type.StructStore structdefs, Program prog,
                 Memory mem, java.util.ArrayDeque< State > statestack ) 
      throws Error
      {
         ValType fromval = statestack. peek( ). lookup( from );
         ValType intoval = statestack. peek( ). lookup( into );
         if( fromval != null && intoval != null &&
             new type.Pointer( fromval.type ). equals( intoval.type ))
         {
            System.out.println( fromval );
            System.out.println( intoval );
            mem. store( intoval. getPointer( ), fromval.val ); 
         }
         else 
            throw notapplicable( statestack. peek( ), fromval, intoval ); 
      }

      public java.lang.String toString( java.lang.String prefix )
      {
         return prefix + "store " + from + " " + into;
      }
   }


   public static class Memcopy extends Instruction
   {
      java.lang.String from;
      java.lang.String into;

      public Memcopy( java.lang.String from, java.lang.String into )
      {
         this. from = from;
         this. into = into;
      }

      void exec( type.StructStore structdefs, Program prog,
                 Memory mem, java.util.ArrayDeque< State > statestack )
      throws Error
      {
         ValType fromval = statestack. peek( ). lookup( from );
         ValType intoval = statestack. peek( ). lookup( into );
         if( fromval != null && intoval != null &&
             fromval.type. equals( intoval.type ) &&
             fromval.type instanceof type.Pointer ) 
         {
            System.out.println( fromval );
            System.out.println( intoval );
            type.Type tp = ((type.Pointer) fromval.type ).tp;
            int s = tp. sizeof( structdefs );
            System.out. println( "size to be copied" + s ); 
            for( int i = 0; i != s; ++ i )
            {
               mem. store( intoval.getPointer( ). plus(i),  
                  mem.load( fromval.getPointer( ). plus(i) ));
            }
         }
         else
            throw notapplicable( statestack. peek( ), fromval, intoval );
      }

      public java.lang.String toString( java.lang.String prefix )
      {
         return prefix + "memcopy " + from + " " + into;
      }
   }


   public static class Goto extends Instruction
   {
      java.lang.String label;

      public Goto( java.lang.String label )
      {
         this. label = label;
      }

      void exec( type.StructStore structdefs, Program prog,
                 Memory mem, java.util.ArrayDeque< State > statestack ) 
      throws Error
      {
         Block target = 
            statestack. peek( ). position. func. blocks. get( label );

         if( target == null )
         {
            CurrentPosition pos = statestack. peek( ). position; 
            throw new Error( this. toString( ), 
                       "label " + label + " does not exist", 
                       pos. blockname, pos. i - 1 );
         } 

         statestack. peek( ). position. blockname = label;
         statestack. peek( ). position. block = target;
         statestack. peek( ). position. i = 0; 
      }

      public java.lang.String toString( java.lang.String prefix )
      {
         return prefix + "goto " + label;
      }
   }


   public static class Iffalse extends Instruction
   {
      java.lang.String reg; 
      java.lang.String label;

      public Iffalse( java.lang.String reg, java.lang.String label )
      {
         this. reg = reg; 
         this. label = label;
      }

      void exec( type.StructStore structdefs, Program prog,
                 Memory mem, java.util.ArrayDeque< State > statestack ) 
      throws Error
      {
         ValType val = statestack. peek( ). lookup( reg );
         if( classify( val ) != BOOL )
            throw notapplicable( statestack. peek( ), val );

         if( !val. getboolean( )) 
         { 
            Block target =
               statestack. peek( ). position. func. blocks. get( label );

            if( target == null )
            {
               CurrentPosition pos = statestack. peek( ). position; 
               throw new Error( this. toString( ),
                          "label " + label + " does not exist",
                          pos. blockname, pos. i - 1 );
            }

            statestack. peek( ). position. blockname = label;
            statestack. peek( ). position. block = target;
            statestack. peek( ). position. i = 0;
         }
      }

      public java.lang.String toString( java.lang.String prefix )
      {
         return prefix + "iffalse " + reg + " goto " + label;
      }
   }


   public static class Call extends Instruction
   {
      java.lang.String funcname;

      public Call( java.lang.String funcname )
      {
         this. funcname = funcname;
      }

      void exec( type.StructStore structdefs, Program prog,
                 Memory mem, java.util.ArrayDeque< State > statestack )
      throws Error
      {
         FunctionBody func = prog. funcdefs. get( funcname );
         if( func == null )
         {
            CurrentPosition pos = statestack. peek( ). position;
            throw new Error( this. toString( ),
                       "function " + funcname + " does not exist",
                       pos. blockname, pos. i - 1 );
         }

         Block entryblock = func. blocks. get( "entry" );
         if( entryblock == null )
         {
            CurrentPosition pos = statestack. peek( ). position;
            throw new Error( this. toString( ),
                       "function " + funcname + " has no entry block",
                       null, 0 );
         } 
         
         statestack. push( new State( 
            new CurrentPosition( funcname, func, "entry", entryblock, 0 )));
      }

      public java.lang.String toString( java.lang.String prefix )
      {
         return prefix + "call " + funcname;
      }
   }
       
   public static class Return extends Instruction
   {
      public Return( )
      {
      }

      void exec( type.StructStore structdefs, Program prog,
                 Memory mem, java.util.ArrayDeque< State > statestack ) 
      throws Error
      {
         statestack. pop( ); 
      }

      public java.lang.String toString( java.lang.String prefix )
      {
         return prefix + "return";
      }
   }


   // Instructions that put something in a register should inherit 
   // from Assignment. On the slides, they have form r : T <--    

   public static abstract class Assignment extends Instruction
   {
      java.lang.String resreg;
      type. Type restype;

      protected Assignment( java.lang.String resreg, type.Type restype )
      {
         this. resreg = resreg;
         this. restype = restype;
      }

      void exec( type.StructStore structdefs, Program prog,
                 Memory mem, java.util.ArrayDeque< State > statestack ) 
      throws Error
      {
         ValType res = eval( structdefs, mem, statestack. peek( )); 
         statestack. peek( ). remove( resreg );   
         if( !restype. equals( res. type ))
         {
            CurrentPosition pos = statestack. peek( ). position; 
            throw new Error( this. toString( ),
                             "declared type " + restype + 
                             " differs from real type " + res. type,  
                             pos. blockname, pos. i - 1 );

         }
         statestack. peek( ). assign( resreg, res );
      }

      public abstract ValType eval( type.StructStore structdefs, 
                                    Memory mem, State state ) throws Error; 
      
      public java.lang.String toString( java.lang.String prefix )
      {
         return prefix + resreg + " : " + restype + " <-- "; 
      }
   }


   // Different from the slides, we found it convenient
   // to store the allocated pointer in a register at once.
   // It may be possible to reuse this register instead of
   // using # .

   public static class Alloc extends Assignment
   {
      type.Type type;

      public Alloc( java.lang.String reg, type.Type type )
      {
         super( reg, new type.Pointer( type ));
         this. type = type;
      }

      public ValType eval( type.StructStore structdefs,
                           Memory mem, State state )
      {
         int s = type. sizeof( structdefs );
         Memory. Pointer res = mem. allocate(s);
         return new ValType( res, restype );
      }

      public java.lang.String toString( java.lang.String prefix )
      {
         return super.toString( prefix ) + "alloc " + type;  
      }
   }


   // This is written as '#' in the slides. Since we cannot
   // call a class '#", we call it 'Variable', because that is what it
   // creates. Different from the slides, we keep an array of types 
   // that we use to compute the position. 

   public static class Variable extends Assignment
   {
      type.Type[] skipped;

      public Variable( java.lang.String resreg, type.Type restype, 
                       type.Type ... skipped )
      {
         super( resreg, restype );   
         this. skipped = skipped; 
      }

      public ValType eval( type.StructStore structdefs, Memory mem, State state ) 
      {
         int s = 0;
         for( type.Type t : skipped ) 
            s += t. sizeof( structdefs );

         Memory. Pointer res = mem. getvariable(s);
         return new ValType( res, restype ); 
      }

      public java.lang.String toString( java.lang.String prefix )
      {
         java.lang.String res = super. toString( prefix ); 
         res += "#";
         for( int i = 0; i != skipped. length; ++ i )
         {
            res += " " + skipped[i];
         }
         return res;    
      }
   }

  
   public static class Constant extends Assignment
   {
      Object value; 

      public Constant( java.lang.String resreg, 
                       Object value, type.Type restype ) 
      {
         super( resreg, restype );  
         this. value = value; 
      }

      public ValType eval( type.StructStore structdefs,
                           Memory mem, State state ) throws Error 
      {
         ValType vt = new ValType( value, restype ); 
         if( !vt. seemswellformed( ))
         {
            CurrentPosition pos = state. position;
            throw new Error( pos. funcname, 
                       this. toString( ) + 
                       ", loaded constant " + vt + " is not well-formed", 
                       pos. blockname, pos.i - 1 );
         }
         return vt;
      }

      public java.lang.String toString( java.lang.String prefix )
      {
         return super. toString( prefix ) + value. toString( ); 
      }
   }


   public static class Load extends Assignment
   {
      java.lang.String fromreg;

      public Load( java.lang.String resreg, 
                   type.Type regtype,
                   java.lang.String fromreg )
      {
         super( resreg, regtype ); 
         this. fromreg = fromreg;
      }

      public ValType eval( type.StructStore structdefs,
                           Memory mem, State state ) throws Error 
      {
         ValType frompntr = state. lookup( fromreg );

         if( ! ( frompntr. type instanceof type.Pointer ))
            throw notapplicable( state, frompntr );

         type.Type tp = ((type.Pointer) frompntr.type ).tp;
            // Type that the result will have.
 
         ValType loaded = new ValType( mem. load( frompntr. getPointer( )),
                                       tp ); 

         if( !loaded. seemswellformed( ))
         {
            CurrentPosition pos = state. position;
            throw new Error( pos. funcname,
                       this. toString( ) +
                       ", loaded value " + loaded + " is not well-formed",
                       pos. blockname, pos.i - 1 );
         }

         return loaded;
      }

      public java.lang.String toString( java.lang.String prefix )
      {
         return super. toString( prefix ) + "load " + fromreg;
      }
   }


   public static class Phi extends Assignment
   {
      java.lang.String[] from; 

      public Phi( java.lang.String resreg,
                  type.Type regtype,
                  java.lang.String ... from )
      {
         super( resreg, regtype );
         this. from = from;
      }

      public ValType eval( type.StructStore structdefs,
                           Memory mem, State state ) throws Error
      {
         ValType fromval = state. lookup( from );
            // lookup picks the most recent among the defined.

         if( fromval == null )
            throw notapplicable( state, fromval );

         return fromval; 
      }

      public java.lang.String toString( java.lang.String prefix )
      {
         java.lang.String res = super. toString( prefix ) + "phi";
         for( int i = 0; i != from. length; ++ i )
            res += " " + from[i];  
         return res;  
      }
   }



   public static class Conv extends Assignment
   {
      java.lang.String arg;

      public Conv( java.lang.String resreg,
                   type.Type restype,
                   java.lang.String arg )
      {
         super( resreg, restype ); 
         this. arg = arg; 
      }


      public ValType eval( type.StructStore structdefs,
                           Memory mem, State state ) throws Error
      {
         Object result = null;

         ValType val = state. lookup( arg );
         if( val != null )
         {
            switch( classify( val ))
            {

            case POINTER:
               System.out.println( val ); 
               if( val. type. equals( new type.Pointer( new type.Void( ))) &&
                   restype instanceof type.Pointer ) 
               {
                  result = val.val;  
               }
               break;
 
            case INTEGER:
               int i = val. getint( );
               result = new java.lang.Double(i); 
               break;
            }
         } 
        
         if( result == null )
            throw notapplicable( state, val );
         
         return new ValType( result, restype );
      }

      public java.lang.String toString( java.lang.String prefix )
      {
         return super. toString( prefix ) + "conv " + arg;
      }

   }


   // All binary operators are identified by their Python names.
 
   public static class Binary extends Assignment
   {
      java.lang.String op;
         // We always use Python names for operators.

      java.lang.String arg1;
      java.lang.String arg2;

      public Binary( java.lang.String op, 
                     java.lang.String resreg, 
                     type.Type restype,
                     java.lang.String arg1,
                     java.lang.String arg2 )
      {
         super( resreg, restype ); 
         this. op = op;
         this. arg1 = arg1;
         this. arg2 = arg2; 
      }

      public ValType eval( type.StructStore structdefs,
                           Memory mem, State state ) throws Error
      {
         ValType val1 = state. lookup( arg1 );
         ValType val2 = state. lookup( arg2 ); 
         int tp1 = classify( val1 );
         int tp2 = classify( val2 );
 
         if( op == "eq" || op == "ne" || op == "lt" || 
             op == "gt" || op == "ge" )
         {
            if( tp1 == tp2 ) 
            {
               System.out.println( "comparison " + op ); 
               switch( tp1 )
               {

               case INTEGER: 
                  if( val1. getint( ) < val2. getint( ))
                     return cmp( op, -1 );  
                  if( val1. getint( ) > val2. getint( ))
                     return cmp( op, 1 );  
                  return cmp( op, 0 ); 
               case POINTER:
                  if( val1. type. equals( val2. type )) 
                  {
                     return cmp( op, val1. getPointer( ). 
                                 compareTo( val2. getPointer( )));
                  }
                  break;
               }
            } 
         } 

         if( op == "add" && tp1 == POINTER && tp2 == INTEGER )
         {
            int i2 = val2. getint( );

            if( val1.type.equals( restype ))
            {
               System.out.println( "iterator addition" );
               System.out.println( "unfortunately not implemented" );
            }
            else
            { 
               // Try field selection: 

               type.Type tp = ((type.Pointer) (val1.type) ).tp; 
                  // First argument points to type tp.
 
               if( tp instanceof type.Struct )
               {
                  java.lang.String name = ((type.Struct) tp ). name;
                  System.out.println( name );
                  int offset = structdefs. offset( name, i2 );
                  System.out.println( offset );  
                  type.Type restype = structdefs. fieldtype( name, i2 );
                  System.out.println( restype );  
                  return new ValType( val1.getPointer( ). plus( offset ),
                                      new type.Pointer( restype ));
               }
            }

         }

         if( op == "add" && tp1 == tp2 )
         {
            System.out.println( "considering add" );
            switch( tp1 )
            {

            case DOUBLE:
               return new ValType( val1. getdouble( ) + val2. getdouble( ),
                                   new type.Double( ));
            }


         }
         if( op == "sub" && tp1 == tp2 )
         {
            System.out.println( "considering sub" );
            switch( tp1 )
            {

            case INTEGER:
               return new ValType( val1. getint( ) - val2. getint( ),
                                   new type.Integer( )); 
            }
         }

         if( op == "mul" && tp1 == tp2 ) 
         {
            System.out.println( "considering mul" );
            switch( tp1 )
            {

            case DOUBLE:
               return new ValType(
                  new Double( val1.getdouble( ) * val2.getdouble( )),
                              new type.Double( )); 
            }
         }
 
         throw notapplicable( state, val1, val2 ); 
      }

      public java.lang.String toString( java.lang.String prefix )
      {
         return super. toString( prefix ) + 
                op + " " + arg1 + " " + arg2;
      }
   }


   // All unary operators are identified by their Python names.
 
   public static class Unary extends Assignment
   {
      java.lang.String op;
         // We always use Python names for operators.

      java.lang.String arg;

      public Unary( java.lang.String op, 
                    java.lang.String resreg, 
                    type.Type restype,
                    java.lang.String arg )
      {
         super( resreg, restype ); 
         this. op = op;
         this. arg = arg;
      }

      public ValType eval( type.StructStore structdefs,
                           Memory mem, State state ) throws Error
      {
         ValType val = state. lookup( arg );
         int tp = classify( val );
 
         if( op == "conv" )
         {
            switch( tp )
            {

            case INTEGER:
               if( restype instanceof type.Double )
               {
                  return new ValType( new java.lang.Double( val. getint( )),
                                      restype ); 
               }
               break;
            case DOUBLE:
            }


         }

         throw notapplicable( state, val ); 
      }

      public java.lang.String toString( java.lang.String prefix )
      {
         return super. toString( prefix ) + op + " " + arg;
      }
   }



   void exec( type.StructStore structdefs, Program prog, 
              Memory mem, java.util.ArrayDeque< State > statestack ) 
   throws Error
   {
      CurrentPosition pos = statestack. peek( ). position;
      throw new Error( pos. funcname, 
                       "instruction " + toString( "" ) + " has no exec method",
                       pos. blockname, pos.i - 1 );
   }

   public abstract java.lang.String toString( java.lang.String prefix );

   public java.lang.String toString( )
   {
      return toString( "" );
   }

}


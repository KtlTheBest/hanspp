
package simulator;

// We do much more checks than needed.
// In principle, one should do no checks at all. 
// Since is educational software, we do many checks.

public abstract class Instruction
{

   public static final int UNDEFINED =     0;
   public static final int VOID =          1;
   public static final int BOOL =          2;
   public static final int CHAR =          3;
   public static final int INTEGER =       4;
   public static final int DOUBLE =        5;
   public static final int POINTER =       6;
   public static final int STRUCT =        7;


   static int classify( ValType vt )
   {
      if( vt == null )
         return UNDEFINED; 

      if( vt.type instanceof type.Void )  
         return VOID;
 
      if( vt.type instanceof type.Bool )
         return BOOL; 

      if( vt.type instanceof type.Char )  
         return CHAR; 

      if( vt.type instanceof type.Integer )
         return INTEGER; 

      if( vt.type instanceof type.Double )
         return DOUBLE; 
      
      if( vt.type instanceof type.Pointer )
         return POINTER; 

      if( vt.type instanceof type.Struct ) 
         return STRUCT;
   
      throw new java.lang.Error( "classify: unknown type.Type" ); 
   }


   Error genericMisery( State state, java.lang.String cause )
   {
      CurrentPosition pos = state. position;
      return new Error( pos. funcname,
                        this. toString( ) + " : " + cause, 
                        pos. blockname, pos.i - 1 );
   }


 
   // Construct an error stating that we are not applicable on the
   // given types. This function also works when some of the arguments
   // are null.  

   Error wrongTypes( State state, ValType ... args ) 
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


   // Turn tracing on:

   public static class Trace extends Instruction
   {
      public Trace( ) { }

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
            s += t. memSize( structdefs );

         if( s > mem. size( ))
         {
            throw genericMisery( statestack. peek( ), 
                                "cannot deallocate " + s + " locations" );
         }

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
             intoval.type instanceof type.Pointer ) 
         {
             type.Type tp = ((type.Pointer) intoval.type ). tp;  
             if( fromval.type. equals( tp ))
             {
                mem. store( intoval. getPointer( ), fromval. val ); 
                return;
             }

             // We need to change the type Pointer(Void). 

             if( fromval.type. equals( new type.Pointer( new type.Void( ))))
             {
                fromval = new ValType( fromval.val, tp );
                mem. store( intoval. getPointer( ), fromval. val );
                return; 
             }
         }

         throw wrongTypes( statestack. peek( ), fromval, intoval ); 
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
            type.Type tp = ((type.Pointer) fromval.type ).tp;
            int s = tp. memSize( structdefs );
            for( int i = 0; i != s; ++ i )
            {
               mem. store( intoval.getPointer( ). plus(i),  
                  mem.load( fromval.getPointer( ). plus(i) ));
            }
         }
         else
            throw wrongTypes( statestack. peek( ), fromval, intoval );
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
            throw genericMisery( statestack. peek( ), 
                                 "label " + label + " does not exist" );
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
            throw wrongTypes( statestack. peek( ), val );

         if( !val. getboolean( )) 
         { 
            Block target =
               statestack. peek( ). position. func. blocks. get( label );

            if( target == null )
            {
               CurrentPosition pos = statestack. peek( ). position; 
               throw genericMisery( statestack. peek( ), 
                                    "label " + label + " does not exist" );
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
            throw genericMisery( statestack. peek( ), 
                                 "function " + funcname + " does not exist" );
         }

         Block entryblock = func. blocks. get( "entry" );
         if( entryblock == null )
         {
            CurrentPosition pos = statestack. peek( ). position;
            throw genericMisery( statestack. peek( ), 
                       "function " + funcname + " has no entry block" );
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
      public Return( ) { }

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

   public static class PrintString extends Instruction
   {
      java.lang.String string;

      public PrintString( java.lang.String string ) 
      {
         this. string = string;
      }

      void exec( type.StructStore structdefs, Program prog,
                 Memory mem, java.util.ArrayDeque< State > statestack )
      {
         System.out.print( string ); 
      }

      public java.lang.String toString( java.lang.String prefix )
      {
         return prefix + "printstring \"" + string + "\""; 
      }
   }

   public static class NewLine extends Instruction
   {
      public NewLine( ) { }

      void exec( type.StructStore structdefs, Program prog,
                 Memory mem, java.util.ArrayDeque< State > statestack )
      {
         System.out.println( "" );
      }

      public java.lang.String toString( java.lang.String prefix )
      {
         return prefix + "newline"; 
      }
   }

   public static class Print extends Instruction
   {
      java.lang.String reg; 

      public Print( java.lang.String reg )
      {
         this. reg = reg; 
      }

      void exec( type.StructStore structdefs, Program prog,
                 Memory mem, java.util.ArrayDeque< State > statestack )
      {
         ValType vt = statestack. peek( ). lookup( reg );
         if( vt != null ) 
            System.out.print( vt.val );
         else
            System.out.print( "(undefined)" ); 
      }

      public java.lang.String toString( java.lang.String prefix )
      {
         return prefix + "print " + reg; 
      }
   }


   // Instructions that put something in a register should inherit 
   // from Assignment. On the slides, they have form r : T <--    
   // Assignment is abstract.

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

         // Not of desired type? We throw error!

         if( !restype. equals( res. type ))
         {
            CurrentPosition pos = statestack. peek( ). position; 
            throw genericMisery( statestack. peek( ), 
                             "declared type " + restype + 
                             " differs from real type " + res. type );
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
         int s = type. memSize( structdefs );
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

      public ValType eval( type.StructStore structdefs, 
                           Memory mem, State state ) 
      throws Error 
      {
         int s = 0;
         for( type.Type t : skipped ) 
            s += t. memSize( structdefs );

         Memory. Pointer res = mem. getvariable(s);
         if( ! ( restype instanceof type.Pointer ))
            throw genericMisery( state, 
                                 "type " + restype + " is not pointer" );

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
            throw wrongTypes( state, vt );
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
            throw wrongTypes( state, frompntr );

         type.Type tp = ((type.Pointer) frompntr.type ).tp;
            // Type that the result will have.
 
         ValType loaded = new ValType( mem. load( frompntr. getPointer( )),
                                       tp ); 
 
         if( !loaded. seemswellformed( ))
         {
            throw genericMisery( state, "loaded value " + loaded + 
                                 "seems not well formed" );
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
         {
            throw genericMisery( state, 
                                 "none of the registers has a value" ); 
                                
         }

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
         int cls = classify( val );

         if( restype instanceof type.Bool )
         {
            if( cls == CHAR )
               return new ValType( val.getchar( ) == '\0' ? false : true );
            if( cls == INTEGER )
               return new ValType( val.getint( ) == 0 ? false : true );
            if( cls == DOUBLE )
               return new ValType( val.getdouble( ) == 0 ? false : true );
         }

         if( restype instanceof type.Char )
         {
            if( cls == INTEGER )
               return new ValType( (char) val.getint( ) );
         }

         if( restype instanceof type.Integer )
         {
            if( cls == BOOL )
               return new ValType( val.getboolean( ) ? 1 : 0 );
            if( cls == CHAR )
               return new ValType( (int) val.getchar( ));
            if( cls == DOUBLE )
               return new ValType( (int) val. getdouble( ));
         }

         if( restype instanceof type.Double )
         {
            if( cls == BOOL )
               return new ValType( val.getboolean( ) ? 1.0 : 0.0 );
            if( cls == CHAR )
               return new ValType( (double) val.getchar( )); 
            if( cls == INTEGER )
               return new ValType( (double) val.getint( ));
         } 
        
         throw wrongTypes( state, val );
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
         int cls1 = classify( val1 );
         int cls2 = classify( val2 );

         if( cls1 == BOOL && cls2 == BOOL )
         {
            boolean b1 = val1. getboolean( );
            boolean b2 = val2. getboolean( );

            if( op == "eq" ) return new ValType( b1 == b2 );
            if( op == "ne" ) return new ValType( b1 != b2 );
            if( op == "lt" ) return new ValType( !b1 && b2 );
            if( op == "gt" ) return new ValType( b1 && !b2 );
            if( op == "le" ) return new ValType( !b1 || b2 );
            if( op == "ge" ) return new ValType( b1 || !b2 );

            if( op == "mul" ) return new ValType( b1 && b2 );
         }

         if( cls1 == CHAR && cls2 == CHAR )
         {
            char c1 = val1. getchar( );
            char c2 = val2. getchar( );

            if( op == "eq" ) return new ValType( c1 == c2 );
            if( op == "ne" ) return new ValType( c1 != c2 );
            if( op == "lt" ) return new ValType( c1 < c2 );
            if( op == "gt" ) return new ValType( c1 > c2 );
            if( op == "le" ) return new ValType( c1 <= c2 );
            if( op == "ge" ) return new ValType( c1 >= c2 );

            if( op == "add" ) return new ValType( (char)(c1 + c2) );
            if( op == "sub" ) return new ValType( (char)(c1 - c2) );
            if( op == "mul" ) return new ValType( (char)(c1 * c2) );
            if( op == "truediv" ) return new ValType( (char)(c1 / c2) );
            if( op == "mod" ) return new ValType( (char)(c1 % c2) );
         }

         if( cls1 == INTEGER && cls2 == INTEGER )
         {
            int i1 = val1. getint( );
            int i2 = val2. getint( );

            if( op == "eq" ) return new ValType( i1 == i2 );
            if( op == "ne" ) return new ValType( i1 != i2 );
            if( op == "lt" ) return new ValType( i1 < i2 );
            if( op == "gt" ) return new ValType( i1 > i2 );
            if( op == "le" ) return new ValType( i1 <= i2 );
            if( op == "ge" ) return new ValType( i1 >= i2 );

            if( op == "add" ) return new ValType( i1 + i2 );
            if( op == "sub" ) return new ValType( i1 - i2 );
            if( op == "mul" ) return new ValType( i1 * i2 );
            if( op == "truediv" ) return new ValType( i1 / i2 );
            if( op == "mod" ) return new ValType( i1 % i2 );
         } 

         if( cls1 == DOUBLE && cls2 == DOUBLE )
         {
            double d1 = val1. getdouble( );
            double d2 = val2. getdouble( ); 

            if( op == "eq" ) return new ValType( d1 == d2 );
            if( op == "ne" ) return new ValType( d1 != d2 );
            if( op == "lt" ) return new ValType( d1 < d2 );
            if( op == "gt" ) return new ValType( d1 > d2 );
            if( op == "le" ) return new ValType( d1 <= d2 );
            if( op == "ge" ) return new ValType( d1 >= d2 );

            if( op == "add" ) return new ValType( d1 + d2 );
            if( op == "sub" ) return new ValType( d1 - d2 );
            if( op == "mul" ) return new ValType( d1 * d2 );
            if( op == "truediv" ) return new ValType( d1 / d2 );
            if( op == "mod" ) return new ValType( d1 % d2 );
         }

         if( cls1 == POINTER && cls2 == POINTER )
         {
            Memory.Pointer p1 = val1. getPointer( );
            Memory.Pointer p2 = val2. getPointer( );

            int i1 = p1. getInteger( );
            int i2 = p2. getInteger( );

            if( op == "eq" ) return new ValType( i1 == i2 );
            if( op == "ne" ) return new ValType( i1 != i2 );
            if( op == "lt" ) return new ValType( i1 < i2 );
            if( op == "gt" ) return new ValType( i1 > i2 );
            if( op == "le" ) return new ValType( i1 <= i2 );
            if( op == "ge" ) return new ValType( i1 >= i2 );

            if( op == "sub" && val1. type. equals( val2. type )) 
            {
               int s = ((type.Pointer) val1.type ).tp. memSize( structdefs );
               return new ValType( ( i1 - i2 ) / s );
            }
         }

          
         if( cls1 == POINTER && cls2 == INTEGER &&
             ( op == "add" || op == "sub" ))
         {
            type.Type tp = ((type.Pointer) (val1.type) ).tp;
               // That is the type to which val1 points.

            int i2 = val2. getint( );
               // The integer value.

            if( restype. equals( val1. type ))
            {
               // Type 1 in the slides:

               int s = tp. memSize( structdefs );
               if( op == "sub" )
                  i2 = -i2;

               return new ValType( val1.getPointer( ). plus( s * i2 ),
                                   restype ); 
            }
            else
            { 
               if( tp instanceof type.Struct &&
                   restype instanceof type.Pointer &&
                   op == "add" )
               {
                  if( i2 < 0 )
                     throw genericMisery( state, 
                                     "negative offset in field selection" ); 

                  java.lang.String name = ((type.Struct) tp ). name;
                  type.FieldArray def = structdefs. get( name );
                  if( def == null )
                     throw genericMisery( state, 
                                          "struct " + name + " is unknown" );

                  if( i2 >= def. nrFields( ))
                     throw genericMisery( state,
                               "offset " + i2 + " >= nrFields" );

                  int offset = def. offset( structdefs, i2 );

                  type.Type restype = def. getType( i2 );
                  return new ValType( val1.getPointer( ). plus( offset ),
                                      new type.Pointer( restype ));
               }

               if( tp instanceof type.Array &&
                   restype instanceof type.Pointer && op == "add" )
               {
                  if( i2 < 0 )
                     throw genericMisery( state,
                                     "negative offset in array entry" );
                  
                  int arraysize = ((type.Array) tp ). s;
                  type.Type arraytype = ((type.Array) tp ). tp;  
                
                  if( i2 >= arraysize )
                     throw genericMisery( state,
                             "offset " + i2 + " >= arraysize" );  

                  return new ValType( val1. getPointer( ). plus( 
                                i2 * arraytype.memSize( structdefs )), 
                                new type.Pointer( arraytype )); 
               }
            }
         }

         throw wrongTypes( state, val1, val2 ); 
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
         int cls = classify( val );

         if( op == "neg" ) 
         {
            if( cls == CHAR ) 
               return new ValType( (char) - val.getchar( )); 
            if( cls == INTEGER )
               return new ValType( - val.getint( ));  
            if( cls == DOUBLE )
               return new ValType( - val.getdouble( ));    
         }

         throw wrongTypes( state, val ); 
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
      throw genericMisery( statestack. peek( ), 
                           "instruction has no exec method" );
   }

   public abstract java.lang.String toString( java.lang.String prefix );

   public java.lang.String toString( )
   {
      return toString( "" );
   }

}


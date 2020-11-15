
package simulator;

public class Examples
{


   static public void addfact( Program prog )
   {
      FunctionBody fact = prog. create( "fact" );

      Block block = fact.create( "entry" );

      block. add(
         new Instruction.Trace( ),  
         new Instruction.Alloc( "R_", new type.Double( )),
         new Instruction.Variable( "R0", 
                         new type.Pointer( new type.Double( ))), 
         new Instruction.Constant( "R1", 
            new java.lang.Double(1.0), new type.Double( )), 
         new Instruction.Store( "R1", "R0" ), 
         new Instruction.Goto( "loop" ) 
      );

      block = fact. create( "loop" );
      block. add( 
         new Instruction.Variable( "R2",
                  new type.Pointer( new type.Integer( )), 
                  new type.Double( )), 
         new Instruction.Load( "R3",
                  new type.Integer( ), "R2" ), 
         new Instruction.Constant( "R4", 
                  new java.lang.Integer( 0 ),
                  new type.Integer( )), 
         new Instruction.Binary( "ne",  
                 "R5", new type.Bool( ), "R3", "R4" ), 
         new Instruction.Iffalse( "R5", "exit" ), 
         new Instruction.Goto( "body" )
      );

      block = fact. create( "body" );

      block. add( 
         new Instruction.Variable( "R6",
             new type.Pointer( new type.Double( ))), 
         new Instruction.Load( "R7", new type.Double( ), "R6" ),
         new Instruction.Variable( "R8", 
                  new type.Pointer( new type.Integer( )),
                  new type.Double( ) ), 
         new Instruction.Load( "R9", new type.Integer( ), "R8" ), 
         new Instruction.Conv( "R10", new type.Double( ), "R9" ), 
         new Instruction.Binary( "mul", 
                "R11", new type.Double( ), "R7", "R10" ), 
         new Instruction.Variable( "R12",
                  new type.Pointer( new type.Double( )) ),
         new Instruction.Store( "R11", "R12" )); 

      block. add( 
         new Instruction.Variable( "R13",
                  new type.Pointer( new type.Integer( )),
                  new type.Double( )),
         new Instruction.Load( "R14",
                  new type.Integer( ), "R13" ), 
         new Instruction.Constant( "R15",
                  new java.lang.Integer(1), new type.Integer( )),
         new Instruction.Binary( "sub",
                 "R16", new type.Integer( ), "R14", "R15" ),
         new Instruction.Variable( "R17",
                  new type.Pointer( new type.Integer( )),
                  new type.Double( ) ), 
         new Instruction.Store( "R16", "R17" ), 
         new Instruction.Goto( "loop" )
      );

      block = fact. create( "exit" );
 
      block. add( 
         new Instruction.Variable( "R18",
                new type.Pointer( new type.Double( ))), 
         new Instruction.Load( "R19",
                new type.Double( ), "R18" ), 
         new Instruction.Dealloc(
                new type.Double( ), new type.Integer( )), 

         new Instruction.Variable( "R20",
                new type.Pointer( new type.Double( ))), 
         new Instruction.Store( "R19", "R20" ), 
         new Instruction.Comment( "the end" ), 
         new Instruction.Return( )
      );
   }


   static public void addfactrec( Program prog )
   {
      FunctionBody fact = prog. create( "factrec" );

      Block block = fact.create( "entry" );
      block. add( 
         new Instruction.Variable( "R0",
                  new type.Pointer( new type.Integer( )) ), 
         new Instruction.Load( "R1",
                  new type.Integer( ), "R0" ), 
         new Instruction.Constant( "R2", 
                  new java.lang.Integer( 0 ),
                  new type.Integer( )), 
         new Instruction.Binary( "ne",  
                 "R3", new type.Bool( ), "R1", "R2" ), 
         new Instruction.Iffalse( "R3", "return1" ), 
         new Instruction.Goto( "rec" )
      );

      block = fact. create( "rec" );
      block. add( 
         new Instruction.Variable( "R4",
             new type.Pointer( new type.Integer( ))), 
         new Instruction.Load( "R5", new type.Integer( ), "R4" ), 
         new Instruction.Unary( "conv", "R6", new type.Double( ), "R5" ),

         new Instruction.Constant( "R7", new java.lang.Integer(1), 
               new type.Integer( )), 
         new Instruction.Variable( "R8",
             new type.Pointer( new type.Integer( ))),
         new Instruction.Load( "R9", new type.Integer( ), "R8" ),
         new Instruction.Binary( "sub", "R10", new type.Integer( ),
                     "R9", "R7" ), 
         new Instruction.Comment( "preparing for recursive call:" ), 
         new Instruction.Alloc( "X", new type.Double( )),
         new Instruction.Alloc( "R11", new type.Integer( )),
         new Instruction.Store( "R10", "R11" ),
         new Instruction.Call( "factrec" ),

         new Instruction.Variable( "R12", 
                  new type.Pointer( new type.Double( ))),
         new Instruction.Load( "R13", new type.Double( ), "R12" ), 
         new Instruction.Dealloc( new type.Double( )),  
         new Instruction.Binary( "mul", 
                "R14", new type.Double( ), "R6", "R13" ), 
         new Instruction.Dealloc( new type.Integer( )),
         new Instruction.Variable( "R15",
                  new type.Pointer( new type.Double( )) ),
         new Instruction.Store( "R14", "R15" ),
         new Instruction.Trace( ), 
         new Instruction.Comment( ),
         new Instruction.Return( )   
      );

      block = fact. create( "return1" );
  
      block. add(
         new Instruction.Trace( ), 
         new Instruction.Constant( "R4", new java.lang.Double( 1.0 ), 
                                         new type.Double( )),
         new Instruction.Dealloc( new type.Integer( )), 
         new Instruction.Variable( "R5",
             new type.Pointer( new type.Double( ))),
         new Instruction.Store( "R4", "R5" ),
         new Instruction.Return( )
      );
   }
   
   // This example is in SSA:

   static public void addlistsum( Program prog )
   {
      FunctionBody fact = prog. create( "listsum" );

      Block block = fact.create( "entry" );

      block. add(
         new Instruction.Variable( "R0",
                   new type.Pointer( new type.Pointer( 
                        new type.Struct( "list" )))),
         new Instruction.Load( "p1",
            new type.Pointer( new type.Struct( "list" )), "R0" ),
         new Instruction.Constant( "R1",
            new Memory.Pointer(0), 
               new type.Pointer( new type.Void( ))),
         new Instruction.Conv( "R2",
            new type.Pointer( new type.Struct( "list" )), "R1" ), 
         new Instruction.Constant( "sum1", 
            new java.lang.Double(0), new type.Double( )), 
         new Instruction.Goto( "loop" )
      );

      block = fact.create( "loop" );
      block. add( 
         new Instruction.Phi( "sum2", 
                 new type.Double( ), "sum1", "sum3" ), 
         new Instruction.Phi( "p2", 
                 new type.Pointer( new type.Struct( "list" )),
                 "p1", "p3" ),
         new Instruction.Binary( "ne",
                 "B1", new type.Bool( ), "p2", "R2" ),
         new Instruction.Iffalse( "B1", "exit" ),
         new Instruction.Constant( "R3",
            new java.lang.Integer(0), new type.Integer( )),
         new Instruction.Binary( "add", 
            "R4", new type.Pointer( new type.Double( )), "p2", "R3" ),
         new Instruction.Load( "R5", new type.Double( ), "R4" ),
         new Instruction.Binary( "add",
            "sum3", new type.Double( ), "sum2", "R5" ), 
         new Instruction.Constant( "R6",
            new java.lang.Integer(1), new type.Integer( )),
         new Instruction.Binary( "add",
            "R7", new type.Pointer( new type.Pointer( 
                      new type.Struct( "list" ))), "p2", "R6" ),
         new Instruction.Load( "p3", 
               new type.Pointer( new type.Struct( "list" )),
            "R7" ),
         new Instruction.Goto( "loop" ) 
      );

      block = fact.create( "exit" );
      block. add( 
         new Instruction.Dealloc( 
                 new type.Pointer( new type.Struct( "list" ))),
         new Instruction.Variable( "R8",
                   new type.Pointer( new type.Double( ))),
         new Instruction.Store( "sum2", "R8" ),
         new Instruction.Return( )
      ); 

   }

   static public void addcomplexsum( Program prog )
   {
      FunctionBody fact = prog. create( "complexsum" );

      Block block = fact.create( "entry" );

      block. add(
         new Instruction.Alloc( "R0", new type.Struct( "complex" )), 
         new Instruction.Variable( "R1",
                   new type.Pointer( new type. Struct( "complex" )),
                   new type.Struct( "complex" )),
         new Instruction.Variable( "R2",
                   new type.Pointer( new type.Struct( "complex" )),
                   new type.Struct( "complex" ),
                   new type.Struct( "complex" )),

         new Instruction.Comment( "Add the real components:" ),

         new Instruction.Constant( "R3",
                   new java.lang.Integer(0), 
                   new type.Integer( )),

         new Instruction.Binary( "add", "R4",
                   new type.Pointer( new type.Double( )),
                   "R1", "R3" ),
         new Instruction.Binary( "add", "R5",
                   new type.Pointer( new type.Double( )),
                   "R2", "R3" ),

         new Instruction.Load( "R6", new type.Double( ), "R4" ), 
         new Instruction.Load( "R7", new type.Double( ), "R5" ),
         new Instruction.Binary( "add", "R8", new type.Double( ), 
                                 "R6", "R7" ),
         new Instruction.Binary( "add", "R9", 
                    new type.Pointer( new type.Double( )), "R0", "R3" ), 
         new Instruction.Store( "R8", "R9" ), 

         new Instruction.Comment( "Add the imaginary components:" ), 

         new Instruction.Trace( ), 
         new Instruction.Constant( "R10",
                   new java.lang.Integer(1),
                   new type.Integer( )),

         new Instruction.Binary( "add", "R11",
                   new type.Pointer( new type.Double( )),
                   "R1", "R10" ),
         new Instruction.Binary( "add", "R12",
                   new type.Pointer( new type.Double( )),
                   "R2", "R10" ),

         new Instruction.Load( "R13", new type.Double( ), "R11" ),
         new Instruction.Load( "R14", new type.Double( ), "R12" ),
         new Instruction.Binary( "add", "R15", new type.Double( ),
                                 "R13", "R14" ),
         new Instruction.Binary( "add", "R16",
                    new type.Pointer( new type.Double( )), "R0", "R10" ),
         new Instruction.Store( "R15", "R16" ),

         new Instruction.Variable( "R17", 
                new type.Pointer( new type.Struct( "complex" )), 
                new type.Struct( "complex" ), 
                new type.Struct( "complex" ),
                new type.Struct( "complex" )),

         new Instruction.Memcopy( "R0", "R17" ), 
         new Instruction.Dealloc( 
                   new type.Struct( "complex" ), 
                   new type.Struct( "complex" ),
                   new type.Struct( "complex" )), 

         new Instruction.Return( )
      );
   }

}


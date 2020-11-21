
class Tests 
{

   public static void testast( ) 
   {
      ast.Pointer pnt = new ast.Pointer(0); 
      System.out. println( pnt ); 
      ast.Tree tr1 = new ast.Bool( true );
      tr1. type = new type.Bool( ); 
      tr1. lr = 'L';
     
      ast.Tree tr2 = new ast.Char( 'x' );

      ast.Tree tr3 = new ast.Double( 100 );
      System. out. println( tr2 );
      tr3. lr = 'L';
      tr3. type = new type. Double( );
    
      tr3 = new ast.Select( "f", tr3 ); 
      tr3. lr = 'R';
      tr3. type = new type. Pointer( new type. Double( ));
 
      ast.Tree tr4 = new ast.Apply( "<=", tr3, tr3 );
      tr4. lr = 'R';
      tr4. type = new type.Pointer( new type.Struct( "type6" ));

      tr4 = new ast.Apply( "function", tr4 );
      tr4. type = new type. Double( );
      tr4. lr = 'L';

      System.out.println( tr4 );

      System.out. println( tr4. trueType( )); 

      System.out.println( tr4. treesize( ));
   }


   public static void testtype( )
   {
      type.StructStore structs = new type.StructStore( );

      structs. insert( "struct1", new type.Field[] 
         { new type.Field( "f1", new type.Struct( "list" ) ),
           new type.Field( "f2", new type.Char( )),
           new type.Field( "f3", new type.Double( )),
           new type.Field( "f4", new type.Integer( )),
           new type.Field( "f5", new type.Double( )),
           new type.Field( "f6", new type.Pointer(
                       new type.Array( 20, new type.Struct( "x" )))) } );

      structs. insert( "list", 
         new type.Field[] { 
              new type.Field( "elem", new type.Double( )),
              new type.Field( "next",
                 new type.Pointer( new type.Struct( "list" ))) } );

      structs. insert( "complex",
         new type.Field[] {
              new type.Field( "re", new type.Double( )),
              new type.Field( "im", new type.Double( )) } ); 
         
      System. out. println( structs );
      System. out. println( structs. fieldtype( "struct1", 0 ));

      semantic.VarStore vars = new semantic.VarStore( ); 
      vars. push( "c1", structs, new type.Struct( "complex" ));
      vars. push( "d1", structs, new type.Double( ));
      vars. push( "l1", structs, new type.Struct( "list" ));
      vars. push( "ll", structs, new type.Array(10, new type.Struct( "list" )));       
      vars. push( "d1", structs, new type.Integer( )); 
      vars. restore(3); 
      System.out.println( vars );
      System.out.println( vars. contains( "d1" ));
      System.out.println( vars. currentoffset( "d1" ));
      System.out.println( vars. gettype( "d1" ));
   }

   
   public static void testsimulator( )
   {
      simulator.Memory mem = new simulator.Memory( ); 
      simulator.Program prog = new simulator.Program( );

      type.StructStore structs = new type.StructStore( );
      structs. insert( "list",
         new type.Field[] {
              new type.Field( "elem", new type.Double( )),
              new type.Field( "next",
                 new type.Pointer( new type.Struct( "list" ))) } );

      structs. insert( "complex",
         new type.Field[] {
              new type.Field( "re", new type.Double( )),
              new type.Field( "im", new type.Double( )) } );

      try 
      {
         // simulator.Examples.addfact( prog );
         simulator.Examples.addfactrec( prog ); 
         // simulator.Examples.addlistsum( prog );
         // simulator.Examples.addcomplexsum( prog );
         System.out.println( prog );

         if( true )
         { 
            Object[] data =
               new Object[] { new java.lang.Integer( 5 ) };

            type.Type types[] =
               new type.Type[] { new type.Integer( ) };

            prog. run( structs, mem, 
                       "factrec", new type.Double( ), data, types ); 
         }

         // mem. clear( );
     //     int ls = new type.Struct( "list" ). sizeof( structs );
       //   simulator.Memory.Pointer p =
         //    mem. allocate( 5 * ls );
//          for( int i = 0; i != 5; ++ i )
  //        {
    //         mem. store( p. plus( i * ls ), new java.lang.Double(i));
      //       mem. store( p. plus( i * ls + 1 ), p. plus( i * ls + 2 ));
//          }

  //        prog. run( structs, mem, "listsum", new type.Double( ), 
    //                 new Object[] { p }, 
      //               new type.Type[] { 
        //                new type.Pointer( new type.Struct( "list" )) } );

         if( false )
         {

            prog. run( structs, mem,
                    "complexsum", new type.Struct( "complex" ), 
                    new Object[] { 
                        new java.lang.Double(1), new java.lang.Double(10),
                        new java.lang.Double(2), new java.lang.Double(20) },
                    new type.Type[] {
                        new type.Struct( "complex" ), 
                        new type.Struct( "complex" ) } ); 
         }
  
         System.out.println( mem. toString( 50 ));
      }
      catch( simulator.Error err )
      {
         System.out.println( "Something went wrong: " );
         System.out.println( err );
         System.out.println( "\n" );
      }

   }

   public static void testtokenizer( java.io.InputStreamReader source ) 
   {
      System.out.println( "testing the tokenizer" );
      Lexer lex = new Lexer( source );

      try
      {
      java_cup.runtime.Symbol lookahead = lex. next_token( );

      while( lookahead.sym != sym. EOF ) 
      {
         System. out. println( "Read Symbol: " +
                               sym. terminalNames [ lookahead. sym ] +
                               " " + lookahead. left +
                               " " + lookahead. right +
                               " " + lookahead. value ); 

         lookahead = lex. next_token( );
      }
      System.out.println( "end of file at " + 
                             lookahead.left + " " + 
                             lookahead. right ); 

      }
      catch( java.io.IOException io )
      {
         System.out.println( "Error: " + io );
      }
      catch( ScanError err )
      {
         System.out.println( "ScanError: " + err );
      }
      catch( java.lang.NumberFormatException err )
      {
         System.out.println( "NumberFormatException: " + err );
         System.out.println( "this probably means that the integer is too big" );
      }
   }

   public static void testtokenizer( java.lang.String filename )
   {
      try
      {
         System.out.println( "Reading from file " + filename ); 
         testtokenizer( new java.io.FileReader( filename ));
      }
      catch( java.io.FileNotFoundException err )
      {
         System.out.println( "could not open file " + filename );
      }
   }
}




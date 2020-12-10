
class Main
{

  public static void main( java.lang.String[] args )
  {
    try 
    {
      Lexer lex = new Lexer( new java.io.InputStreamReader( System.in ));
      lex = new Lexer( new java.io.FileReader( "test4.cfl" )); 

      Parser cupparser = new Parser( lex );

      System.out.println("Starting the parsing");

      java_cup.runtime.Symbol parse_tree = cupparser. parse();

      System.out.println("Parsed the tree");

        System.out. println( cupparser. parsetree ); 

      System. out. println( "Starting Semantic Analysis" ); 

      for( java.util.Map.Entry< String, type.FieldArray > entry :
          cupparser. parsetree. structdefs. entrySet( ))
      {
        java.lang.String name = entry. getKey( );
        type.FieldArray fields = entry. getValue( );

        semantics.SanityChecks.checkwellformed( 
            cupparser. parsetree. structdefs, name, fields ); 
      }

      for( java.util.Map.Entry< String, type.FieldArray > entry :
          cupparser. parsetree. structdefs. entrySet( ))
      {
        java.lang.String name = entry. getKey( );
        type.FieldArray fields = entry. getValue( );

        semantics.SanityChecks.checknotcircular(
            cupparser. parsetree. structdefs, 
            new java.util.HashSet< java.lang.String > ( ),
            new java.util.ArrayDeque< java.lang.String > ( ),
            name, fields ); 
      }

      for( java.util.Map.Entry< String, cflat.Program.FuncDef > entry :
          cupparser. parsetree. funcdefs. entrySet( ))
      {
        java.lang.String name = entry. getKey( );
        cflat.Program.FuncDef def = entry. getValue( );

        semantics.SanityChecks.checkFunctionHeader( 
            cupparser. parsetree. structdefs, name, 
            def. parameters, def. returntype ); 
      }

      for( java.util.Map.Entry< String, cflat.Program.FuncDef > entry :
          cupparser. parsetree. funcdefs. entrySet( ))
      {
        java.lang.String name = entry. getKey( );
        cflat.Program.FuncDef def = entry. getValue( );  

        semantics.FunctionChecker checker = 
          new semantics.FunctionChecker( cupparser. parsetree, 
              name, def );

        checker. checkStatement( entry. getValue( ). body ); 
      } 

      System.out.println( "Result of Semantic Analysis" );
      System.out.println( cupparser. parsetree ); 

      System.out.println( "Starting Translation" );

      simulator.Program translation = new simulator.Program( );

      for( java.util.Map.Entry< String, cflat.Program.FuncDef > entry : 
          cupparser. parsetree. funcdefs. entrySet( )) 
      {
        java.lang.String name = entry.getKey( );
        cflat.Program.FuncDef def = entry. getValue( );

        simulator.FunctionBody transl = translation. create( name ); 
        // We don't fear redefinitions, because we checked
        // already that they don't exist. 

        translation.Translator trans =
          new translation.Translator( cupparser.parsetree, 
              name, def, transl );
        trans. translate( def ); 

        System.out.println( translation );

      }

      System.out.println( translation );  

      System.out.println( "Calling main:\n" );

      simulator.Memory mem = new simulator.Memory( );
      Object[] data = new Object[0];
      type.Type types[] = new type.Type[0];
      translation. run( cupparser.parsetree. structdefs, mem,
          "main", new type.Void( ), data, types );

      if( mem. size( ) != 0 )
      {
        System.out.println( 
            "The memory is not empty! " + 
            "This means that not all variables were deallocated" );
      }
      return; 
    } 
    catch( java.lang.Error e ) {
      System. out. println( "Error: " + e ); } 
    catch( java.lang.Exception e ) {
      System. out. println( "Exception: " + e ); } 
  }

}



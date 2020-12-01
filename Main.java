
class Main
{

   public static void main( java.lang.String[] args )
   {
      try 
      {
         Lexer lex = new Lexer( new java.io.InputStreamReader( System.in ));
         lex = new Lexer( new java.io.FileReader( "test.cfl" )); 

         Parser cupparser = new Parser( lex );

         java_cup.runtime.Symbol parse_tree = cupparser. parse();

         System.out. println( cupparser. parsetree ); 

         System. out. println( "Starting Semantic Analysis" ); 

         cupparser. parsetree. structdefs. forEach( 
            ( name, fields ) -> 
               { semantics.SanityChecks.checkwellformed( 
                    cupparser. parsetree. structdefs, name, fields ); } ); 

         cupparser. parsetree. structdefs. forEach(
            ( name, fields ) ->
               { semantics.SanityChecks.checknotcircular(
                    cupparser. parsetree. structdefs, 
                    new java.util.HashSet< java.lang.String > ( ),
                    new java.util.ArrayDeque< java.lang.String > ( ),
                    name, fields ); } );

         cupparser. parsetree. funcdefs. forEach( 
            ( name, func ) ->
                 semantics.SanityChecks.checkFunctionHeader( 
                    cupparser. parsetree. structdefs, name, 
                                   func. parameters, func.returntype )); 

         cupparser. parsetree. funcdefs. forEach(
            ( name, funcdef ) ->
              { semantics.FunctionChecker checker = 
                   new semantics.FunctionChecker( cupparser. parsetree, 
                                                  name, funcdef );
                checker. checkStatement( funcdef. body ); 
              } );

         System.out.println( "Result of Semantic Analysis" );
         System.out.println( cupparser. parsetree ); 
      } 
      catch( Exception e ) {
         System. out. println( "Error: " + e ); } 
      catch( semantics.Error e ) {
         System. out. println( "Error: " + e ); } 
      catch( semantics.NotFinished u ) { 
         System. out. println( u );  } 
   }


}



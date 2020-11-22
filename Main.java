class Main
{

   public static void main( java.lang.String[] args )
   {
      try
      {
         Lexer lex = new Lexer( new java.io.InputStreamReader( System.in ));
         lex = new Lexer( new java.io.FileReader( "test.cfl" ));

         Parser cupparser = new Parser( lex );

         System.out.println( "CUP Generated Parser\n" );
         java_cup.runtime.Symbol parse_tree = cupparser. parse();
         System.out. println( cupparser. parsetree );
      }
      catch( Exception e ) {
         System. out. println( "Error: " + e ); }

   }
}


package ast;

// String constans are NOT PART of the language. You don't need
// to type check them. The only reason they exist, is so that 
// it is possible to write print( "have a nice day" ); 
// Otherwise, you would have to do this character by character. 

public class String extends Tree  
{
   public java.lang.String s; 

   public String( java.lang.String s )
   {
      super( ); 
      this.s = s; 
   }

   public boolean isconstant( ) { return false; }

   public int treesize( ) { return 1; }

   java.lang.String printablestring( )
   {
      java.lang.StringBuilder res = new java.lang.StringBuilder( ); 

      for( int i = 0; i != s. length( ); ++ i )
      {
         char c = s.charAt(i);
         if( c == '\n' )
         {
            res. append( "\\n" );
         }
         else if( c == '\t' )
         {
            res. append( "\\t" ); 
         }
         else if( c == '\''  )
         {
            res. append( "\'" );
         }
         else if( c == '\"' )
         {
            res. append( "\"" );     
         }
         else
            res. append(c); 
      }
      return res. toString( ); 
   }


   public java.lang.String toString( int depth )
   {
      java.lang.String res = indentation( depth ) + 
                             "\"" + printablestring( ) + "\""; 

      res += super. typetoString( );
      res += "\n";
      return res;
   }
};




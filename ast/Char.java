
package ast;

// A character constant.

public class Char extends Tree  
{
   public char c; 

   public Char( char c )
   {
      super( ); 
      this.c = c;
   }

   public boolean isconstant( ) { return true; }

   public int treesize( ) { return 1; }

   public java.lang.String toString( int depth )
   {
      java.lang.String res = indentation( depth ) + "\'" + c + "\'";
      res += super. typetoString( );
      res += "\n";
      return res;
   }
};




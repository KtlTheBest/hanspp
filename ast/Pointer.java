
package ast;

// A pointer constant. Actually, we know only one pointer constant,
// which is null. It will be represented by the integer 0. 

public class Pointer extends Tree  
{
   public int p; 

   public Pointer( int p )
   {
      super( ); 
      this.p = p;
   }

   public boolean isconstant( ) { return true; }

   public int treesize( ) { return 1; }

   public java.lang.String toString( int depth )
   {
      java.lang.String res = indentation( depth );
      if( p == 0 )
         res += "Pointer( null )";
      else 
         res += "Pointer( " + p + " )";
      res += super. typetoString( );
      res += "\n";
      return res;
   }

};



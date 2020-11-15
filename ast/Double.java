
package ast;

// A double constant. 

public class Double extends Tree  
{
   public double d; 

   public Double( double d )
   {
      super( ); 
      this.d = d;
   }

   public boolean isconstant( ) { return true; }

   public int treesize( ) { return 1; }

   public java.lang.String toString( int depth )
   {
      java.lang.String res = indentation( depth ) + d;
      res += super. typetoString( );
      res += "\n";
      return res;
   }

};



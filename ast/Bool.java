
package ast;

// A Boolean constant.

public class Bool extends Tree  
{
   public boolean b;

   public Bool( boolean b )
   {
      super( ); 
      this.b = b;
   }

   public boolean isconstant( ) { return true; }

   public int treesize( ) { return 1; }

   public java.lang.String toString( int depth )
   {
      java.lang.String res = indentation( depth ) + b;
      res += super. typetoString( );
      res += "\n";
      return res;
   }

};



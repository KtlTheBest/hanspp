
package ast;

// An integer constant.

public class Integer extends Tree  
{
   public java.lang.Integer i;

   public Integer( int i )
   {
      super( ); 
      this.i = i; 
   }

   public boolean isconstant( ) { return true; }

   public int treesize( ) { return 1; }

   public java.lang.String toString( int depth )
   {
      java.lang.String res = indentation( depth ) + i;
      res += super. typetoString( );
      res += "\n";
      return res;
   }

};




package ast;

// An identifier. We distinguish between id( ) and id.
// The first type is an Apply without subtrees. The
// second is an identifier. 

public class Identifier extends Tree  
{
   public java.lang.String id; 

   public Identifier( java.lang.String id )
   {
      super( ); 
      this.id = id; 
   }

   public boolean isconstant( ) { return false; }

   public int treesize( ) { return 1; }

   public java.lang.String toString( int depth )
   {
      java.lang.String res = indentation( depth ) + id;
      res += super. typetoString( );
      res += "\n";
      return res;
   }
};




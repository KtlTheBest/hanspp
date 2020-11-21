
package ast;

// Selection of a field.

public class Select extends Tree
{
   public java.lang.String field;     // Field that we want to select.
   public Tree sub;

   public int treesize( )
   {
      return 1 + sub.treesize( ); 
   }
 
   public Select( java.lang.String field, Tree sub )
   {
      this. field = field; 
      this. sub = sub;
   }

   public int nrsubTrees( ) { return 1; }

   // String of the complete tree:

   public java.lang.String toString( int depth )
   {
      StringBuilder out = new StringBuilder( ); 
      out. append( indentation( depth )); 
      out. append( "select " + field ); 
      out. append( typetoString( ));
      out. append( "\n" );
 
      out. append( sub.toString( depth + 1 ));
      return out. toString( ); 
   }

};



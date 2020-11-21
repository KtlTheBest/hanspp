
package ast;

// Most of the ast-s are applies:

public class Apply extends Tree
{
   public java.lang.String function; // This can be an operator or a 
                                     // user defined function. 

   public Tree[] sub;

   public int treesize( )
   {
      int s = 1;
      for( Tree t : sub )
         s += t. treesize( );
      return s; 
   }
 
   public Apply( java.lang.String function, Tree ... sub )
   {
      this. function = function; 
      this. sub = sub;
   }

   public int nrsubTrees( ) { return sub. length; }
   public Tree getsubTree( int i ) { return sub[i]; } 

   // String representation of the complete tree:

   public java.lang.String toString( int depth )
   {
      StringBuilder out = new StringBuilder( ); 
      out. append( indentation( depth )); 
      out. append( function ); 
      out. append( typetoString( ));
      out. append( "\n" );
 
      for( int i = 0; i != sub. length; ++ i ) 
      {
         out. append( sub[i].toString( depth + 1 ));
      }
      return out. toString( ); 
   }

};



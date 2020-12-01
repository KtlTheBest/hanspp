
package ast;

public abstract class Tree 
{
   public type.Type type;
   public char lr;      // Must be 'L', 'R' or ' ' in case of unknown. 

   Tree( ) 
   {
      this.type = null;
      this.lr = ' ';

   }

   public boolean isconstant( ) { return false; }
      // Override this is you are a constant.

   public abstract int treesize( ); 
      // Size as tree. It has nothing to do with the size of the type,
      // which we don't know in general, because ast-s can be unchecked.

   public int nrsubTrees( ) { return 0; } 

   public type.Type trueType( ) 
   {
      if( lr == 'L' )
         return new type.Pointer( this. type );
 
      if( lr == 'R' )
         return this. type;

      return null;
   }
 
   // We removed the simple, one line toString, because
   // there are no simple ast-s. 

   public java.lang.String toString( ) { return toString(0); } 
   public abstract java.lang.String toString( int depth );

   public java.lang.String typetoString( )
   {
      java.lang.String res = "";
      if( type != null )
      {
         res += "         type : "; 
         res += type. toString( );
      }
 
      if( lr == 'L' )
         res += " / LVAL";
      if( lr == 'R' )
         res += " / RVAL";

      return res;       
   }

   protected java.lang.String indentation( int depth )
   {
      java.lang.StringBuilder res = new java.lang.StringBuilder( ); 
      for( int i = 0; i < depth; ++ i )
         res. append( "   " );
      return res. toString( );
   }

   abstract public Tree clone( );

};



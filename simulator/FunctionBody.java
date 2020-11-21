
package simulator; 

public class FunctionBody 
{
   public java.util.TreeMap< java.lang.String, Block > blocks;

   FunctionBody( )
   {
      this.blocks = new java.util.TreeMap<> ( ); 
   }

   // Both methods return the Block that was added, so that
   // it will be possible to make changes in it.

   public 
   Block create( java.lang.String name, Block block )
   {
      blocks. put( name, block );
      return block; 
   }

   public 
   Block create( java.lang.String name )
   {
      return create( name, new Block( ));
   }

   public java.lang.String toString( java.lang.String prefix )
   {
      java.lang.StringBuilder res = new java.lang.StringBuilder( );
      blocks. forEach( (k,v) ->
      {
         res.append( prefix ); 
         res.append( "block " );
         res.append(k); res. append( ":\n" );
         res.append( v. toString( prefix + "   " ));
      });
      return res. toString( ); 
   }

   public java.lang.String toString( ) 
   {
      return toString( "" );
   }
   
}



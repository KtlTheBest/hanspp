
package type;

public class StructStore
{
   java.util.HashMap< String, FieldArray > defs; 

   public StructStore( ) 
   {
      defs = new java.util.HashMap( );
   }

   // We don't overwrite. Function add returns true 
   // if the insertion took place.

   public boolean put( String s, FieldArray def )
   {
      // We look up s. If absent, we insert def and return true. 
      // If present, we do nothing and return false. 

      if( defs. get(s) == null )
      {
         defs. put( s, def );
         return true;
      }
      else
         return false; 
   }

   // True if we heard about this struct name: 
 
   public boolean contains( java.lang.String name )
   {
      return defs.get( name ) != null; 
   }

   public FieldArray get( java.lang.String name )
   {
      return defs.get( name );
   }

   public int nrStructs( ) 
   {
      return defs. size( );
   }

   // Type definitions are always global. Because of that, there are no
   // remove methods.


   public java.util.Set< java.util.Map.Entry< java.lang.String, FieldArray >>
   entrySet( ) 
   {
      return defs. entrySet( ); 
   }

   public java.lang.String toString( )
   {
      StringBuilder res = new StringBuilder( ); 
      res. append( "StructStore:\n" );
    
      for( java.util.Map.Entry< String, FieldArray > def: entrySet( ) ) 
      {
         res. append( "   " ); 
         res. append( "struct " );
         res. append( def. getKey( ). toString( ));
         res. append( " := " );
         res. append( def. getValue( ). toString( )); 
         res. append( "\n" );
      }

      return res. toString( ); 
   };

};




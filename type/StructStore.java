
package type;

public class StructStore
{
   java.util.HashMap< String, Field[] > defs; 

   public StructStore( ) 
   {
      defs = new java.util.HashMap< String, Field[] > ( );
   }

   // We don't overwrite. Function add returns true 
   // if the insertion took place.

   public boolean insert( String s, Field[] fields )
   {
      // We look up s. If absent, we insert empty Field array.
      // If present, we do nothing. 

      if( defs. get(s) == null )
      {
         defs. put( s, fields );
         return true;
      }
      else
         return false; 
   }

  
   // True if we contain this struct:
 
   public boolean contains( java.lang.String name )
   {
      return defs.get( name ) != null; 
   }

   // space in memory, needed for struct name.

   public int sizeof( java.lang.String name )
   {
      Field[] fields = defs. get( name );
      if( fields == null )
         throw new UndeclaredError( "struct def", name );

      int s = 0;
      for( int i = 0; i != fields. length; ++ i )
         s += fields[i]. tp. sizeof( this );
      return s;
   }

   // Number of structs that we contain. Should not be confused
   // with sizeof, which is about storage space. 

   public int size( ) 
   {
      return defs. size( );
   }

   // Type definitions are always global. Because of that, there are no
   // remove methods.


   // True if structname has fieldname:

   public 
   boolean hasfield( java.lang.String structname, java.lang.String fieldname )
   {
      Field[] fields = defs. get( structname );
      if( fields == null )
         return false;  
      for( int i = 0; i != fields. length; ++ i )
      {
         if( fields[i]. f. equals( fieldname ))
            return true;
      }
      return false;
   }


   // Position of fieldname in structname. 
   // If structname is declared as ( f1:double, f2:double ), then
   // the relative position of f1 is 0, and the relative position of
   // f2 is 1.

   public 
   int position( java.lang.String structname, java.lang.String fieldname )
   {
      Field[] fields = defs. get( structname );
      if( fields == null )
         throw new UndeclaredError( "struct", structname );
      for( int i = 0; i != fields. length; ++ i )
      {
         if( fields[i]. f. equals( fieldname ))
            return i; 
      }
      throw new UndeclaredError( "field of " + structname, fieldname );
   }

   // This is the offset of the i-th field in structname. 
   // For example if the fields have types
   // f1:struct(double,double), f2:int, then
   // 0-th field has offset 0, and 1-st field has offset 2.
   // We assume that existence of the field and the struct was
   // checked.

   public 
   int offset( java.lang.String structname, int i ) 
   {
      Field[] fields = defs. get( structname );
      if( fields == null )
         throw new UndeclaredError( "struct", structname );
      if( i >= fields. length )
         throw new UndeclaredError( "offset of " + structname, "" + i );

      int s = 0;
      for( int j = 0; j != i; ++ j )
      {
         s += fields[j]. tp. sizeof( this );
      }
      return s; 
   }

   // Type of the i-th field in structname.
   // If you want to find the type of a field from its name,
   // you first have to call position( fieldname ).

   public
   Type fieldtype( java.lang.String structname, int i )
   {
      Field[] fields = defs. get( structname );
      if( fields == null )
         throw new UndeclaredError( "struct", structname );
      if( i >= fields. length )
         throw new UndeclaredError( "offset of " + structname, "" + i );

      return fields[i]. tp;
   }
    
   public java.lang.String toString( )
   {
      StringBuilder res = new StringBuilder( ); 
      res. append( "StructStore:\n" );
    
      for( java.util.Map.Entry< String, Field[] > def: defs. entrySet( )) 
      {
         res. append( "   " ); 
         res. append( "struct " );
         res. append( def. getKey( ). toString( ));
         res. append( " := { " );
         Field[] val = def. getValue( );
         for( int i = 0; i != val. length; ++ i )
         {
            if( i != 0 ) res. append( ", " );
            res. append( val[i] ); 
         } 
         res. append( " }\n" );
      }

      return res. toString( ); 
   };

};





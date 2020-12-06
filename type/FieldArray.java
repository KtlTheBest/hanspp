
package type;

public class FieldArray
{

   public static class Field 
   {
      public java.lang.String name;
      public Type tp;

      public Field( java.lang.String name, Type tp )
         { this.name = name; this.tp = tp; }

      public java.lang.String toString( ) 
      {
         return name + " : " + tp. toString( ); 
      }
   };

   java.util.ArrayList< Field > fields;

   public FieldArray( ) 
   {
      this. fields = new java.util.ArrayList( ); 
   }

   public FieldArray( Field ... fields )
   {
      this. fields = new java.util.ArrayList( );

      for( int i = 0; i != fields. length; ++ i ) 
         this. fields. add( fields[i] );

      // Another proof that java is faildesign. How many proofs does
      // one need? 
   }

   // Add a field, without checking uniqueness:

   public void add( java.lang.String name, Type tp )
   {
      fields. add( new Field( name, tp ));
   }

   public void add( Field f ) 
   {
      fields. add(f);
   }

   // Number of fields that we have:

   public int nrFields( ) 
   {
      return fields. size( );
   }
 
 
   // Total size that we occupy in memory:
 
   public int memSize( StructStore structdefs )
   {
      int s = 0;
      for( Field f : fields ) 
         s += f. tp. memSize( structdefs );
      return s;
   }

   // Find the index of name. If name does not exist,
   // we use the C++ convention, which means that we return nrFields( ). 
   // This is consistent with find( ) returning end( ) when a 
   // key is not found.

   public 
   int getIndex( java.lang.String name )
   {
      for( int i = 0; i != fields. size( ); ++ i )
      {
         if( fields.get(i). name. equals( name ))
            return i; 
      }
      return fields. size( ); 
   }


   // This is the offset in memory of the field with index. 
   // f should have been obtained by getIndex( ). 
   // For example if the fields have types
   // f1:struct(double,double), f2:int, then
   // 0-th field has offset 0, and 1-st field has offset 2.
   // One can call getIndex( "f2" ) which will return 1.
   // After that, offset(1) returns 2.

   public int offset( StructStore structdefs, int index ) 
   {
      if( index < 0 || index >= fields. size( ))
         throw new java.lang.Error( "Index " + index + " out of range" ); 

      int s = 0;
      for( int i = 0; i != index; ++ i )
      {
         s += fields.get(i). tp. memSize( structdefs );
      }
      return s; 
   }


   // Name of the field with the given index:

   public java.lang.String getName( int index )
   {
      if( index < 0 || index >= fields. size( ))
         throw new java.lang.Error( "Index " + index + " out of range" );

      return fields. get( index ). name;
   }



   // Type of the field with the given index: 

   public Type getType( int index )
   {
      if( index < 0 || index >= fields. size( ))
         throw new java.lang.Error( "Index " + index + " out of range" );

      return fields. get( index ). tp;
   }
  
 
   public java.lang.String toString( )
   {
      StringBuilder res = new StringBuilder( ); 
      res. append( "(" );
      for( int i = 0; i != fields. size( ); ++ i )
      {
         if( i != 0 ) 
            res. append( ", " );
         else 
            res. append( " " ); 

         res. append( fields. get(i) ); 
      } 
      res. append( " )" );
      return res. toString( ); 
   };

};




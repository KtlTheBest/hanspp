
package type;

// Struct contains the NAME of a struct, without knowing if 
// this struct is defined, or what fields it has.
// A CONCRETE, KNOWN struct is represented by FieldArray. 
// In order to get the FieldArray from a name,
// call getFields( ) in StructStore.

public class Struct extends Type 
{
   public java.lang.String name;

   public Struct( java.lang.String name )
   {
      this. name = name;
   }

   public boolean isprimitive( ) { return false; }

   public int memSize( StructStore structdefs ) 
   { 
      FieldArray def = structdefs. get( name );
      if( def == null ) 
         throw new UndeclaredError( "struct", name ); 
      return def. memSize( structdefs );
   }

   public java.lang.String toString( ) 
   { 
      return "Struct( " + name + " )";
   }

   // On structs, we have intentional type equivalence: 

   public boolean equals( Type other )
   {
      if( other instanceof Struct )
      {
         Struct otherstruct = (Struct) other;
         if( name. equals( otherstruct. name ))
            return true; 
         else
            return false;
      }
      else
         return false; 
   }
};



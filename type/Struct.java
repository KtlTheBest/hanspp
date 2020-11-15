
package type;

// A struct is always defined through a name. 

public class Struct extends Type 
{
   public java.lang.String name;

   public Struct( java.lang.String name )
   {
      this. name = name;
   }

   public boolean isprimitive( ) { return false; }

   public int sizeof( StructStore structs ) 
   { 
      return structs. sizeof( name );
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



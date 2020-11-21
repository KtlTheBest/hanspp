
package type;

public class Char extends Type 
{

   public boolean isprimitive( ) { return true; }

   public int sizeof( StructStore structs ) { return 1; }

   public java.lang.String toString( ) 
      { return "Char"; }

   public boolean equals( Type other )
   {
      return other instanceof Char;
   } 
};



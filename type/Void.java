
package type;

public class Void extends Type 
{
   public boolean isprimitive( ) { return true; }

   public int sizeof( StructStore structs ) { return 0; }

   public java.lang.String toString( ) 
      { return "Void"; }

   public boolean equals( Type other )
   {
      return other instanceof Void;
   }
 
};



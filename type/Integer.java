
package type;

public class Integer extends Type 
{
   public Integer() {}

   public boolean isprimitive( ) { return true; }

   public int sizeof( StructStore structs ) { return 1; }

   public java.lang.String toString( ) 
      { return "Integer"; }

   public boolean equals( Type other )
   {
      return other instanceof Integer;
   }
};



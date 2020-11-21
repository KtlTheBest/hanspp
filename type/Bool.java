
package type;

public class Bool extends Type 
{
   public boolean isprimitive( ) { return true; }

   public int sizeof( StructStore structs ) { return 1; }

   public java.lang.String toString( ) 
      { return "Bool"; }

   public boolean equals( Type other )
   {
      return other instanceof Bool;
   } 
};



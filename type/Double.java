
package type;

public class Double extends Type 
{
   public boolean isprimitive( ) { return true; }

   public int memSize( StructStore structdefs ) { return 1; }

   public java.lang.String toString( ) 
      { return "Double"; }

   public boolean equals( Type other )
   {
      return other instanceof Double;
   } 
};



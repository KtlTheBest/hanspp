
package type;

public class Pointer extends Type 
{
   public Type tp;

   public Pointer( Type tp ) 
      { this.tp = tp; }

   public boolean isprimitive( ) { return false; }

   public int sizeof( StructStore structs ) 
      { return 1; } 

   public java.lang.String toString( ) 
      { return "Pointer( " + tp. toString( ) + " )"; } 

   public boolean equals( Type other )
   {
      return other instanceof Pointer &&
             ((Pointer) other ). tp. equals(tp);  
   }
};



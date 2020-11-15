
package type;

public class Array extends Type 
{
   public int s;
   public Type tp;

   public Array( int s, Type tp ) 
      {  this.s = s; this.tp = tp; }

   public boolean isprimitive( ) { return false; }

   public int sizeof( StructStore structs ) 
   { 
      return s * tp. sizeof( structs ); 
   } 

   public java.lang.String toString( ) 
      { return "Array( " + s + ", " + tp. toString( ) + " )"; } 

   public boolean equals( Type other )
   {
      if( other instanceof Array )
      {
         Array otherarray = (Array) other;
         return s == otherarray. s && tp. equals( otherarray. tp );
      }
      else
         return false; 
   }
};




// As defined in the slides.

package type; 

public abstract class Type 
{
   Type( )
   {
   }

   abstract public boolean isprimitive( );
  
   public abstract int memSize( StructStore structs );
      // The size that is needed to store the thing in memory. 
      // We throw UndeclaredError when encounter an undefined Struct.
 
   public abstract java.lang.String toString( ); 
   public abstract boolean equals( Type other );

};



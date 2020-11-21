
package semantic; 

class VarData
{
   type.Type tp;
   int offset;  
   int size;

   public VarData( type.Type tp, int offset, int size )
   {
      this. tp = tp;
      this. offset = offset;
      this. size = size; 
   }
  
   public java.lang.String toString( )
   {
      return "offset " + offset + ",   type " + tp. toString( ); 
   }
}

 

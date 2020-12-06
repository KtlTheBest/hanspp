
package translation;

// A NameGenerator generates names of form prefix01, prefix02,
// prefix03, ... 

public class NameGenerator
{
   final java.lang.String prefix;
   int counter; 

   public NameGenerator( java.lang.String prefix ) 
   {
      this. prefix = prefix; 
      this. counter = 1;
   }

   public java.lang.String create( )
   {
      java.lang.String result = prefix;
      if( counter < 10 )
         result += "0" + counter;
      else
         result += counter;  

      ++ counter;

      return result; 
   } 

   public java.lang.String toString( )
   {
      return "NameGenerator( " + prefix + " / " + counter + " )"; 
   }

}


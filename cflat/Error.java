
package cflat;

// Error should inherit from Exception of course, but
// then the parser has to be declared as throwing, which
// seems to be impossible.

public class Error extends java.lang.Error
{
   String problem;

   public Error( String problem )
   {
      this. problem = problem; 
   }

   public String toString( )
   {
      return "Error " + problem; 
   }
};




package type;

// Thrown when an undeclared identifier is used.

public class UndeclaredError extends java.lang.Error 
{
   java.lang.String what;    // What it was supposed to be, e.g. a field,
                             // or a typename. 
   java.lang.String ident;    // The name. 

   public UndeclaredError( java.lang.String what, java.lang.String ident )
   { 
      this. what = what; 
      this. ident = ident;
   }

   public String toString( ) 
   {
      return "undeclared " + what + " : " + ident;
   }
}



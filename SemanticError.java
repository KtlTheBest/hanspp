
// It should inherit from Exception of course, but
// then the parser has to be declared as throwing, which
// seems to be not possible.

public class SemanticError extends java.lang.Error 
{
   String reason;

   public SemanticError( String reason )
   {
      this. reason = reason; 
   }

   public String toString( )
   {
      return reason; 
   }
};




// Throw this exception when there is unfinished code.

public class NotFinished extends java.lang.Error 
{
   String where;

   public NotFinished( String where )
   { 
      this. where = where;
   }

   public String toString( ) 
   {
      return "trying to execute unfinished code at " + where;
   }
}



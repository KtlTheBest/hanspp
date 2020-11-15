
public class ScanError extends Exception
{
   String reason;
   int line;
   int column;

   public ScanError( String reason, int line, int column )
   {
      this. reason = reason; 
      this. line = line;
      this. column = column;
   }

   public String toString( )
   {
      return reason + " at position " + line + "/" + column;
   }
};




package simulator;

public class Error extends Exception
{
   java.lang.String funcname;
   java.lang.String cause;
   java.lang.String blockname;
   int i;
 
   public Error( java.lang.String funcname,
                 java.lang.String cause,
                 java.lang.String blockname,
                 int i ) 
   {
      this. funcname = funcname;
      this. cause = cause; 
      this. blockname = blockname;
      this. i = i; 
   }

   public String toString( )
   {
      java.lang.String res = ""; 
      if( blockname == null ) 
      {
         res = "function " + funcname + ":  ";
      }
      else
      {
         res = "position " + funcname + "." +
                             blockname + "[" + i + "] :  ";
      }
      res += cause;
      return res;  
   }
};


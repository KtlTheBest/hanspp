
package semantics; 

// Errors are terrible things. One is tempted to classify them by cause.
// This results in disaster. Instead one should classify them by the point
// where they occur. This works much better because the context
// information that you want to put in the message, depends on the
// place where it occurs, much more than on its cause.

public class Error extends java.lang.Exception
{
   String context;
   String problem;

   public Error( String context, String problem )
   {
      this. context = context;
      this. problem = problem; 
   }

   public String toString( )
   {
      return "In " + context + " : " + problem; 
   }

  
   static public class checkwellformed extends Error
   {
      public checkwellformed( String context, String problem )
      {
         super( context, problem );
      }
   }
        

   // Error while type checking a statement:

   static public class checkStatement extends Error
   {
      ast.Tree stat;

      public checkStatement( String context, ast.Tree stat, String problem )
      {
         super( context, problem );
         this. stat = stat;
      }

      public String toString( )
      {
         return "In " + context + "\n" +
            stat + "check failed: " + problem;
      }
   }


   // Error while type checking an expression:

   static public class checkExpr extends Error 
   {
      ast.Tree expr;

      public checkExpr( String context, ast.Tree expr, String problem )
      {
         super( context, problem );
         this. expr = expr;
      }

      public String toString( ) 
      {
         return "In " + context + "\n" + 
            expr + "check failed: " + problem;
      }
   }

};



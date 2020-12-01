
package semantics;

public abstract class SanityChecks 
{

   // Context is used only for generation of readable errors.
   // It could be something like "function f " or "struct f";

   public static void 
   checkwellformed( type.StructStore structdefs, 
                    java.lang.String context, type.Type tp ) 
   throws Error
   {
      System.out.println( "checking " + context + " " + tp ); 

      // It is a recursive procedure:

      if( tp instanceof type.Array )
      {
         type.Array arr = (type.Array) tp;
         if( arr.s < 0 )
         {
            throw new Error.checkwellformed( 
               context, "size is negative in " + tp. toString( )); 
         }
         checkwellformed( structdefs, context, arr.tp );
         return; 
      }

      if( tp instanceof type.Void ||
          tp instanceof type.Bool ||
          tp instanceof type.Char ||
          tp instanceof type.Integer ||
          tp instanceof type.Double )
      {
         return;    // nothing to check. 
      }

      throw new NotFinished( "checkwellformed" ); 
   }


   public static void 
   checkwellformed( type.StructStore structdefs, 
                    java.lang.String structname, type.Field[] struct ) 
   throws Error
   {
      for( int i1 = 0; i1 < struct. length; ++ i1 )
      {
         for( int i2 = 0; i2 < i1; ++ i2 )
         {
         }
      }

      for( int i = 0; i < struct. length; ++ i )
      {
      }
   }   


   public static void
   checknotcircular( type.StructStore structdefs,
                     java.util.Set< java.lang.String > visitedset,
                     java.util.Deque< java.lang.String > visitedstack, 
                     java.lang.String structname, type.Field[] fields ) 
   throws Error 
   {
      if( visitedset. contains( structname ))
      {
         throw new Error.checkwellformed( "structdef " + structname, 
                           "definition is circular" );
      }
   }


   public static void
   checknotcircular( type.StructStore structdefs,
                     java.util.Set< java.lang.String > visitedset,
                     java.util.Deque< java.lang.String > visitedstack,
                     type.Type tp )  
   throws Error 
   {
   }

   public static void
   checkFunctionHeader( type.StructStore structdefs, 
                        java.lang.String funcname, 
                        type.Field[] parameters,
                        type.Type returntype ) 
   throws Error 
   {
   }

}




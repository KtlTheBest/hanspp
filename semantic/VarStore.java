
package semantic;

public class VarStore 
{
   java.util.HashMap< String, java.util.ArrayDeque< VarData >> vardata; 
      // The last value is always the current value. 

   java.util.ArrayDeque< String > stack; 
      // This is the stack of known variables, in the order in which
      // they were initialized. We need this because variables
      // are created and removed in a stack-like fashion.

   int totalsize;  // sum of the sizes of the representations. 

   public VarStore( ) 
   {
      vardata = 
         new java.util.HashMap< String, java.util.ArrayDeque< VarData >> ( );
      stack = new java.util.ArrayDeque< String > ( );
      totalsize = 0; 
   }

   public void push( String var, type.StructStore structs, type.Type tp ) 
   {
      // We look up var. If absent, we insert empty Arraylist.

      java.util.ArrayDeque< VarData > val = vardata. compute( var, 
         (l,r) -> ( r == null ? 
                    new java.util.ArrayDeque< VarData > ( ) : r )); 

      int size = tp. sizeof( structs ); 
      val. push( new VarData( tp, totalsize, size )); 
      totalsize += size; 

      stack. push( var ); 
   }

   // We do not call it 'size' in order to avoid confusion with
   // the size in memory.

   public int nrvars( ) 
   {
      return stack. size( );
   }

   // Restore to a value that was returned by nrvars only.

   public void restore( int nr )
   {
      while( stack. size( ) > nr )
      {
         String last = stack. pop( );

         java.util.ArrayDeque< VarData > data = vardata. get( last );
         totalsize -= data. peek( ). size;

         data. pop( ); 
         if( data. isEmpty( ))
            vardata. remove( last ); 

      }
   }

 
   public boolean contains( java.lang.String var )
   {
      return vardata. get( var ) != null;
   }

   // Current offset of var. It is the sum of the sizes of 
   // the variables that come after it.

   public int currentoffset( java.lang.String var )
   {
      VarData data = vardata. get( var ). peek( );
      return totalsize - data. offset - data. size;
   }
   
   public type.Type gettype( java.lang.String var )
   {
      return vardata. get( var ). peek( ). tp;
   }
 
   public java.lang.String toString( )
   {
      java.lang.StringBuilder res = new java.lang.StringBuilder( ); 
      res.append( "Varstore:\n" );
      res.append( "   total size = " + totalsize + "\n" );

      res. append( "      (last declared is shown first)\n" ); 
      for( java.lang.String var : stack )
      {
         VarData data = vardata. get( var ). peek( );
         res. append( "   at offset " ); 
         res. append( totalsize - data. offset - data. size ); 
         res. append( "   " );
         res. append( var ); 
         res. append( " : " );
         res. append( data. tp );
         res. append( "\n" );
      }
 
      return res. toString( ); 
   };
} 





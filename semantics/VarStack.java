
// Written by Hans de Nivelle, part of course CSCI 355, Compiler Construction

package semantics;

public class VarStack
{

   static class VarData
   {
      java.lang.String name; 
      type.Type tp;

      int offset;   // sum of the sizes that occur before it. 
      int size;

      // Assume we declare v0 : Integer,
      //                   v1 : Array( 10, Boolean ), 
      //                   v2 : Complex 
      //                   v3 : Double 
      // We have
      //       name         tp               offset     size 
      //         v0      Integer,               0         1
      //         v1      Array(10,Boolean)      1        10
      //         v2      Complex               11         2
      //         v3      Double                13         1 
      
      VarData( java.lang.String name, type.Type tp, int offset, int size )
      {
         this. name = name; 
         this. tp = tp;
         this. offset = offset;
         this. size = size; 
      }
  
      public java.lang.String toString( )
      {
         return name + "   ( " + offset + " / " + size + " )    : " + tp;
      }
   }

   java.util.HashMap< String, java.util.ArrayList<java.lang.Integer>> lookup; 
      // Each name is mapped to a stack of indices into 
      // varstack. The last one is the current. 

   java.util.ArrayList< VarData > varstack; 
      // This is the stack of known variables, in the order in which
      // they were initialized. We need this because variables
      // are created and removed in a stack-like fashion.

   int totalsize;  // Total sum of the sizes of the representations. 

   public VarStack( ) 
   {
      lookup = new java.util.HashMap<> ( );
      varstack = new java.util.ArrayList<> ( );
      totalsize = 0; 
   }


   public void push( String name, int size, type.Type tp ) 
   {
      // We look up name. If absent, we insert an empty ArrayList. 

      java.util.ArrayList< java.lang.Integer > occurrences = 
         lookup. compute( name, (l,r) -> ( r == null ? 
                    new java.util.ArrayList< java.lang.Integer > ( ) : r )); 

      int position = varstack. size( ); 
      occurrences. add( position );     // We are adding an occurence. 

      varstack. add( new VarData( name, tp, totalsize, size )); 
      totalsize += size; 
   }

   // We do not call it 'size' in order to avoid confusion with
   // the size in memory.

   public int nrVariables( ) 
   {
      return varstack. size( );
   }

   // Restore to a value that was returned by nrvars only.
   // Don't restore to a size, it won't end well.

   public void restore( int nr )
   {
      while( varstack. size( ) > nr )
      {
         VarData last = varstack. get( varstack. size( ) - 1 );
         varstack. remove( varstack. size( ) - 1 );
 
         totalsize -= last. size;
         java.util.ArrayList< Integer > 
            occurrences = lookup. get( last. name );

         occurrences. remove( occurrences. size( ) - 1 ); 
         if( occurrences. isEmpty( )) lookup. remove( last. name ); 

      }
   }


   // The index of the last pushed variable is 0.
   // The index of the second last pushed variable is 1, etc.
   //  
   // If the variable is not known, we return nrvars( ).
   // This is a C++ style interface, for example map::find( ) returns
   // map::end( ) when the key is not found.
   // After considering several options, this is the best interface.
   // I am tired of using Java style interfaces, I tried to be patient. 

   public int getIndex( java.lang.String var )
   {
      java.util.ArrayList< Integer > occurrences  = lookup. get( var ); 
      if( occurrences == null )
         return varstack. size( );
      else 
         return occurrences. get( occurrences. size( ) - 1 ); 
   }

   // Current offset of the variable with the given index.
   // Note that the oldest variable has index 0, and the most recent 
   // has index size( ) - 1. If you use a value returned by index( ),
   // it will be fine. 

   public int getOffset( int index )
   {
      VarData data = varstack. get( index );
      return totalsize - data. offset - data. size;
   }

   public type.Type getType( int index )
   {
     return varstack. get( index ). tp;
   }
 
   public java.lang.String toString( )
   {
      java.lang.StringBuilder res = new java.lang.StringBuilder( ); 
      res.append( "VarStack ( last declared is at the end, " + 
                  "total size is " + totalsize + " ):\n" );

      for( VarData var : varstack ) 
      {
         res. append( "   " );
         res. append( var );
         res. append( "\n" );
      }

      return res. toString( ); 
   };
} 





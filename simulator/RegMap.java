
package simulator;

// A RegMap maps the registers to their values.
// We do not use HashMap or TreeMap, because we want
// to remember order of assignment.
// This is useful when one prints the map, because 
// the last assignments are usually the ones that matter
// for the current computation. 
// Secondly, remembering assignment order makes it possible to 
// implement phi-functions.
// Just pick the most recent assignment among the arguments.
//
// We use two arrays, with the last assignment at the end.
// This may be inefficient, but this simulator is intended
// for education, not efficiency. It is probably not even that bad, 
// because one mostly looks up registers that have recent assignments. 

public class RegMap 
{

   java.util.ArrayList< java.lang.String > regs; 
   java.util.ArrayList< ValType > valtypes; 
   
   public RegMap( ) 
   {
      regs = new java.util.ArrayList<> ( ); 
      valtypes = new java.util.ArrayList<> ( ); 
   }

   // Not useful, but easy to implement:

   public boolean isEmpty( ) 
   {
      return regs. size( ) == 0;
   }

   public int size( ) 
   {
      return regs. size( );
   }

   public void clear( )
   {
      regs. clear( );
      valtypes. clear( );
   }

   // One should not reassign:
  
   public void assign( java.lang.String reg, ValType valtype )
   {
      regs. add( reg );
      valtypes. add( valtype );
   }

   // If you are not in the mood to construct the ValType, 
   // we gladly do it for you: 

   public void assign( java.lang.String reg, Object val, type.Type type )
   {
      assign( reg, new ValType( val, type ));
   }


   static boolean contains( java.lang.String[] array, 
                            java.lang.String value )
   {
      for( java.lang.String s : array )
      {
         if( s. equals( value ))
            return true;  
      }
      return false;
   }


   // Returns the value of the most recent in wanted:

   public ValType lookup( java.lang.String ... wanted ) 
   {
      int i = valtypes. size( );
      while( i != 0 )
      {
         -- i;
         if( contains( wanted, regs.get(i) ))
            return valtypes.get(i); 
      }
      return null;
   }


   // Returns number of registers that were removed:

   public int remove( java.lang.String ... unwanted )
   {
      java.util.ArrayList< java.lang.String > regs2 =
                     new java.util.ArrayList<> ( );
      java.util.ArrayList< ValType > valtypes2 = 
                     new java.util.ArrayList<> ( );
   
      int removed = 0;   
      for( int i = 0; i != regs. size( ); ++ i )
      {
         if( !contains( unwanted, regs.get(i)) ) 
         {
            regs2. add( regs. get(i));
            valtypes2. add( valtypes. get(i));
         }
         else
            ++ removed;
      }  
      regs = regs2;
      valtypes = valtypes2; 
      return removed;
   }


   public java.lang.String toString( ) 
   {
      java.lang.String res = "Registers: ";

      // Most recent assignments must be shown first:

      int i = valtypes. size( ); 
      while( i != 0 ) 
      {
         -- i; 
         res += ( regs.get(i) + " = " + valtypes.get(i) ); 
         if( i != 0 )
            res += ", ";
      }
      return res; 
   }

}



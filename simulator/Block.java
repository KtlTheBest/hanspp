
package simulator;

public class Block
{
   // A block is just a sequence of instructions.
   // It has must contain a goto at the end, otherwise
   // there is no way of exiting it.

   public java.util.ArrayList< Instruction > instructions;

   public Block( )
   {
      this.instructions = new java.util.ArrayList<> ( );
   }

   public void add( Instruction ... ins )
   {
      for( Instruction i : ins ) 
         instructions. add( i );
   }

   public Instruction get( int i )
   {
      return instructions. get(i);
   }

   public java.lang.String toString( java.lang.String prefix )
   {
      java.lang.StringBuilder result = new java.lang.StringBuilder( ); 

      for( int i = 0; i != instructions.size( ); ++ i )
      {
         result. append( instructions.get(i). toString( prefix )); 
         result. append( "\n" );
      }
      return result. toString( );
   }

   public java.lang.String toString( )
   {
      return toString( "" );
   }

}


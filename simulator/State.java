
package simulator;

public class State
{
   CurrentPosition position;
   RegMap registers;
   boolean tracing;   

   State( CurrentPosition position, RegMap registers )
   {
      this. position = position;
      this. registers = registers;
      this. tracing = false; 
   }

   State( CurrentPosition position )
   {
      this. position = position;
      this. registers = new RegMap( );
      this. tracing = false; 
   }

   public void assign( java.lang.String reg, ValType valtype )
   {
      registers. assign( reg, valtype );
   }

   public void assign( java.lang.String reg, Object val, type.Type type )
   {
      registers. assign( reg, val, type );
   }

   public ValType lookup( java.lang.String ... wanted )
   {
      return registers. lookup( wanted );
   }
 
   public int remove( java.lang.String ... unwanted )
   {
      return registers. remove( unwanted );
   }

   public java.lang.String toString( ) 
   {
      java.lang.String res = "State:\n";
      res += position;
      res += ":\n";
      res += registers;  
      return res; 
   }
}



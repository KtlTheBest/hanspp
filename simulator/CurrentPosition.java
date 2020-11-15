
package simulator;

public class CurrentPosition 
{
   // Meaning that we are in the given function, in the given block, 
   // at position i.

   public java.lang.String funcname; 
   public FunctionBody func;
   public java.lang.String blockname;
   public Block block; 
   public int i; 

   public CurrentPosition( java.lang.String funcname, FunctionBody func, 
                           java.lang.String blockname, Block block, 
                           int i )
   {
      this. funcname = funcname; 
      this. func = func;
      this. blockname = blockname; 
      this. block = block;
      this. i = i;
   }

   public java.lang.String toString( ) 
   {
      return funcname + ":" + blockname + "[" + i + "]";
   }
}

 

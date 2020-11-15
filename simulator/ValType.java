
package simulator;

// A ValType is a pair consisting of a value and its type. 
// Since we have our own type system, we don't care about the Java type.

public class ValType
{
   Object val;
   type.Type type;

   public ValType( Object val, type.Type type )
   {
      this.val = val;
      this.type = type;
   }

   public java.lang.String toString( ) 
   {
      if( val != null )
         return val. toString( ) + " : " + type.toString( );
      else
         return "null : " + type.toString( );
   }

   public boolean getboolean( )
   {
      return ((java.lang.Boolean) val ). booleanValue( ); 
   }

   public char getchar( )
   {
      return ((java.lang.Character) val ). charValue( ); 
   }

   public int getint( )
   {
      return ((java.lang.Integer) val ). intValue( ); 
   }

   public Double getdouble( )
   {
      return ((java.lang.Double) val ). doubleValue( );
   }

   public Memory.Pointer getPointer( )
   {
      return (Memory.Pointer) val;
   }

   boolean seemswellformed( )
   {
      if( val instanceof java.lang.Boolean && type instanceof type.Bool )
         return true;

      if( val instanceof java.lang.Character && type instanceof type.Char )
         return true;

      if( val instanceof java.lang.Integer && type instanceof type.Integer )
         return true;
  
      if( val instanceof java.lang.Double && type instanceof type.Double )
         return true;
 
      if( val instanceof Memory.Pointer && type instanceof type.Pointer )
         return true; 

      return false;
   }

}
 

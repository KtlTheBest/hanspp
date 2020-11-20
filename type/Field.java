
package type;

public class Field 
{
   public java.lang.String f;
   public Type tp;

   public Field( java.lang.String f, Type tp )
      { this.f = f; this.tp = tp; }

   public java.lang.String toString( ) 
   {
      return f + " : " + tp. toString( ); 
   }
};



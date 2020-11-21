
package simulator;

// Since our data are untyped, we try to carry as little 
// type information in memory as possible. We do this 
// by making everything Object. 

// The memory is organized as a stack. 
// 0 is always top of stack, 1 is the location that comes after it,
// etc.

public class Memory
{
   java.util.ArrayList<Object> mem; 
      // There exists no true untypedness in Java, 
      // but we try to get as close as possible by putting
      // Object here.

   public Memory( ) 
   {
      this.mem = new java.util.ArrayList<> ( );
   }

   // Pointers are always negative:

   public static class Pointer  
   {
      int addr;

      public Pointer( int addr )
      {
         this.addr = addr;
      }

      public java.lang.String toString( ) 
      {
         return "#(" + addr + ")"; 
      }

      public Pointer plus( int s )
      {
         return new Pointer( addr + s );
      }

      public int compareTo( Pointer p )
      {
         if( addr < p. addr ) return -1;
         if( addr > p. addr ) return 1;
         return 0;
      }
   }

   public void clear( )
   {
      mem. clear( );
   }

   // s should be true size, not number of variables.

   public Pointer allocate( int s )
   {
      Pointer res = new Pointer( -mem. size( ) - s );

      for( int i = 0; i != s; ++ i )
         mem. add( null );
   
      return res;
   }

   public void deallocate( int s )
   {
      while( s != 0 )
      {
         mem. remove( mem. size( ) - 1 );
         -- s;
      }
   }

   public void store( Pointer p, Object val )
   {
      mem. set( -p. addr - 1, val );    // Because pointers are negative.
   }
 
   public Object load( Pointer p )
   {
      return mem.get( -p. addr - 1 );   // Because pointers are negative.
   }

   public Pointer getvariable( int s )
   {
      return new Pointer( -mem.size( ) + s );
   }

   // Parameter depth is not indentation depth. It is the number
   // of locations that we show. 

   public java.lang.String toString( int depth )
   {
      int d = 0;
      if( d + depth < mem. size( ))
         d = mem.size( ) - depth;

      java.lang.StringBuilder res = 
         new java.lang.StringBuilder( "Memory: " );

      int i = mem. size( );
      while( i != d )  
      {
         -- i; 
         res. append(-i-1); res.append( ": " ); res. append( mem.get(i)); 
         if( i != d ) res. append( ", " );
      }
      return res. toString( );
   }

   public java.lang.String toString( )
   {
      return toString( 10 );
   }
}
   
   

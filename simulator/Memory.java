
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
         return "#" + addr; 
      }

      public Pointer plus( int s )
      {
         return new Pointer( addr + s );
      }

      // Convert the pointer to an integer:
  
      int convInteger( )  
      {
         return addr;
      }
   }

   public void clear( )
   {
      mem. clear( );
   }

   // s should be true size, not number of variables skipped: 

   public Pointer allocate( int s )
   {
      Pointer res = new Pointer( -mem. size( ) - s );

      for( int i = 0; i != s; ++ i )
         mem. add( null );
   
      return res;
   }
 
   public int size( )
   {
      return mem. size( );
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
      int addr = -p.addr - 1;  // Pointers are always negative.

      if( addr < 0 || addr >= mem.size( ))
         throw new java.lang.Error( "simulator: segmentation fault!" );
 
      mem. set( addr, val ); 
   }
 
   public Object load( Pointer p )
   {
      int addr = -p.addr - 1;   // Pointers are always negative.

      if( addr < 0 || addr >= mem. size( ))
         throw new java.lang.Error( "simulator: segmentation fault!" );

      return mem.get( addr ); 
   }

   public Pointer getVariable( int s )
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
         if( i != d ) res. append( ",    " );
      }
      return res. toString( );
   }

   public java.lang.String toString( )
   {
      return toString( 20 );
   }
}
   


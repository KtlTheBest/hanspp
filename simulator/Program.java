
package simulator;

// A program is a set of function definitions:

public class Program
{
   java.util.TreeMap< java.lang.String, FunctionBody > funcdefs; 

   public Program( )
   {
      funcdefs = new java.util.TreeMap<> ( ); 
   }

   // Both methods return the FunctionBody that was added
   // (not the previous one), 
   // so that it will be possible to add Blocks to it later.

   public 
   FunctionBody create( java.lang.String name, FunctionBody body )
   {
      funcdefs. put( name, body );
      return body; 
   }

   public
   FunctionBody create( java.lang.String name )
   {
      return create( name, new FunctionBody( ));
   }

   // Start the simulation: Call func, which should return rettype, 
   // with parameters parvalues whose types are .
   // Note that in our system, parameters can have size > 1.
   // Also note that parameters are store in memory as follows: 
   // TOS        p1 | p2 | ... | pn

   public void run( type.StructStore structdefs, Memory mem, 
                    java.lang.String funcname, type.Type rettype, 
                    Object[] parvalues, type.Type[] partypes ) throws Error 
   {
      FunctionBody body = funcdefs. get( funcname ); 
      if( body == null )
         throw new Error( funcname, 
                          "tried to call but does not exist", null, 0 ); 

      // Allocate space in memory for the return type:
  
      int s = rettype. memSize( structdefs );
      Memory.Pointer res = mem. allocate(s);

      // Actually, we don't need partypes. We use it only for checking.
      // We compute the total size, and check if it agrees with the
      // length of parvalues.

      s = 0;
      for( int par = 0; par != partypes. length; ++ par )
         s += partypes[ par ]. memSize( structdefs );

      if( parvalues. length != s ) 
         throw new Error( "run( )", 
            "parvalues[] has wrong length, it must be " + s, null, 0 );
      
      Memory.Pointer p = mem. allocate(s);
      for( int i = 0; i != s; ++ i )
         mem.store( p. plus(i), parvalues[i] ); 

      // We look up the entry block:

      Block entry = body. blocks. get( "entry" );
      if( entry == null )
         throw new Error( funcname, "has no entry block", null, 0 );

      java.util.ArrayDeque< State > statestack = new java.util.ArrayDeque<> ();

      statestack. push( new State( new CurrentPosition( funcname, body,
                                   "entry", entry, 0 )));

      while( ! statestack. isEmpty( )) 
      {
         State topstate = statestack. peek( ); 
            // State on top of the stack. That's where we currently are.
 
         if( topstate. position. i >= topstate. position. block. size( ))
            throw new Error( topstate. position. funcname,
                             "fell over the end of the block",
                             topstate. position. blockname,
                             topstate. position. i );


         Instruction ins = topstate. position. block. get( 
                               topstate. position. i );

         if( statestack. peek( ). tracing ) 
         {
            System.out.println( "(level " + ( statestack. size() - 1 ) + ") " );
            System.out.println( topstate ); 
            System.out.println( mem ); 
            System. out. println( ins );

            java.util.Scanner sc = new java.util.Scanner( System.in );
            java.lang.String inp = sc.next( ); 
            if( inp. equals( "q" ) || inp. equals( "Q" ))
            {
               System.out.println( "Quit" ); 
               return;
            }

            if( inp. equals( "n" ) || inp. equals( "N" ))
               statestack. peek( ). tracing = false; 
         } 
         ++ topstate. position. i;
         ins. exec( structdefs, this, mem, statestack );
      }
      System.out.println( "\n" );
      System.out.println( "The computation ended peacefully" ); 
      System.out.println( mem ); 
   }

   public java.lang.String toString( ) 
   {
      java.lang.StringBuilder res = new java.lang.StringBuilder( );

      funcdefs. forEach( (k,v) -> 
      { 
         res. append( "function " );
         res. append(k); res. append( ":\n" );
         res. append( v. toString( "   " ));
         res. append( "\n" );
      });

      return res. toString( ); 
   }
}


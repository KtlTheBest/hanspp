
package cflat;

public class Program
{
   public class FuncDef
   {
      public type.FieldArray parameters;
      public type.Type returntype;
      public ast.Tree body;

      public FuncDef( type.FieldArray parameters,
                      type.Type returntype,
                      ast.Tree body )
      {
         this. parameters = parameters;
         this. returntype = returntype;
         this. body = body;
      }
   }

   public java.util.HashMap< java.lang.String, ast.Tree > constdefs;
   public type.StructStore structdefs;  
   public java.util.HashMap< java.lang.String, FuncDef > funcdefs;

   public Program( )
   {
      constdefs = new java.util.HashMap<> ( );
      structdefs = new type.StructStore( );
      funcdefs = new java.util.HashMap<> ( );
   }

   public void addconstant( java.lang.String id, ast.Tree val )
   {
      if( constdefs. get( id ) != null )
         throw new Error( "constant " + id + " redefined " ); 
      else
         constdefs. put( id, val );
   }

   public void addstruct( java.lang.String id, type.FieldArray fields )
   {
      if( structdefs. contains( id ))
         throw new Error( "struct " + id + " redefined " );
      else
         structdefs. put( id, fields );
   }

   public void addfunction( java.lang.String id,  
                            type. FieldArray parameters,
                            type.Type returntype,
                            ast.Tree body )
   {
      if( funcdefs. get( id ) != null )
         throw new Error( "function " + id + " redefined" );
      else
         funcdefs. put( id, new FuncDef( parameters, returntype, body ));
   }

 
   public java.lang.String toString( )
   {
      java.lang.StringBuilder res = new java.lang.StringBuilder( );
      res. append( "Constants:\n" );
      for( java.util.Map.Entry< java.lang.String, ast.Tree > 
              c : constdefs. entrySet( ))
      {
         res. append( "   " );
         res. append( c. getKey( )); 
         res. append( " = " );
         res. append( c. getValue( ));
      }
      res. append( "\n" ); 
      res. append( structdefs. toString( )); 
      res. append( "\n" );

      res. append( "Functions\n" );
      for( java.util.Map.Entry< java.lang.String, FuncDef >
              f : funcdefs. entrySet( ))
      {
         res. append( "   " );
         res. append( f. getKey( )); 
         res. append( f. getValue( ). parameters );
         res. append( ": " );
         res. append( f. getValue( ). returntype );
         res. append( "\n" ); 
         res. append( f. getValue( ). body. toString( 2 ));
         res. append( "\n" );
      }
      return res. toString( );
   }
      

}

 

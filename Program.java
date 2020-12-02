
public class Program
{
  public class FuncDef
  {
    public type.Field[] parameters;
    public type.Type returntype;
    public ast.Tree body;

    public FuncDef( type.Field[] parameters,
        type.Type returntype,
        ast.Tree body )
    {
      this. parameters = parameters;
      this. returntype = returntype;
      this. body = body;

      if(parameters.length == 1 && parameters[0].f.equals("n")){
        System.out.println("Program.java internal check: " + returntype.toString());
      }
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
      throw new SemanticError( "constant " + id + " redefined " ); 
    else
      constdefs. put( id, val );
  }

  public void addstruct( java.lang.String id, 
      java.util.ArrayList< type.Field > fields )
  {
    if( structdefs. contains( id ))
      throw new SemanticError( "struct " + id + " redefined " );
    else
      structdefs. insert( id, fields. toArray( new type.Field[] {} ));
  }

  public void addfunction( java.lang.String id,  
      java.util.ArrayList< type. Field  > parameters,
      type.Type returntype,
      ast.Tree body )
  {
    if( funcdefs. get( id ) != null )
      throw new SemanticError( "function " + id + " redefined" );
    else {
      System.out.println("KEEEK!!! " + id);
      if(id.equals("fact")){
        System.out.println("INTEEERNAAAAL: " + returntype.toString());
      }
      funcdefs. put( id, new FuncDef( 
            parameters. toArray( new type.Field[] {} ), 
            returntype, body ));
    }
  }

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
    res. append( "( " );
    for( int i = 0; i != f. getValue( ). parameters. length; ++ i )
    {
      if( i != 0 ) res. append( ", " );
      res. append( f. getValue( ). parameters[i] ); 
    }
    res. append( " ): " );
    res. append( f. getValue( ). returntype );
    res. append( "\n" ); 
    res. append( f. getValue( ). body. toString( 2 ));
    res. append( "\n" );
  }
  return res. toString( );
}

}



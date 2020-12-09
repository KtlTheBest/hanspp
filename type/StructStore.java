
package type;

public class StructStore
{
  java.util.HashMap< String, FieldArray > defs; 

  public StructStore( ) 
  {
    defs = new java.util.HashMap( );
  }

  // We don't overwrite. Function add returns true 
  // if the insertion took place.

  public boolean put( String s, FieldArray def )
  {
    // We look up s. If absent, we insert def and return true. 
    // If present, we do nothing and return false. 

    if( defs. get(s) == null )
    {
      defs. put( s, def );
      return true;
    }
    else
      return false; 
  }

  // True if we heard about this struct name: 

  public boolean contains( java.lang.String name )
  {
    return defs.get( name ) != null; 
  }

  public void printfields( java.lang.String name ){
    System.out.println("Fields of struct " + name);
    if( get( name ) == null ){
      System.out.println("null");
      return;
    }

    type.FieldArray farr = get( name );
    for(int i = 0; i < farr.nrFields(); ++ i){
      type.Type tp = farr.getType(i);
      java.lang.String id = farr.getName(i);
      System.out.println(id + " - " + tp);
    }
  }

  public boolean hasfield( java.lang.String name, java.lang.String field )
  {
    if( contains( name ) ){
      type.FieldArray farr = get(name);
      if(farr.getIndex( field ) == farr.nrFields() ) 
        return false;
      else
        return true;
    } else
      return false;
  }

  public int position( java.lang.String name, java.lang.String field )
  {
    if( get( name ) == null ) return 0;
    type.FieldArray farr = get( name );
    return farr.getIndex( field );
  }

  public type.Type fieldtype( java.lang.String name, int pos )
  {
    if( get( name ) == null ) return new type.Void();
    type.FieldArray farr = get( name );
    return farr.getType(pos);
  }

  public type.FieldArray get( java.lang.String name )
  {
    return defs.get( name );
  }

  public int nrStructs( ) 
  {
    return defs. size( );
  }

  // Type definitions are always global. Because of that, there are no
  // remove methods.


  public java.util.Set< java.util.Map.Entry< java.lang.String, FieldArray >>
    entrySet( ) 
    {
      return defs. entrySet( ); 
    }

  public java.lang.String toString( )
  {
    StringBuilder res = new StringBuilder( ); 
    res. append( "StructStore:\n" );

    for( java.util.Map.Entry< String, FieldArray > def: entrySet( ) ) 
    {
      res. append( "   " ); 
      res. append( "struct " );
      res. append( def. getKey( ). toString( ));
      res. append( " := " );
      res. append( def. getValue( ). toString( )); 
      res. append( "\n" );
    }

    return res. toString( ); 
  };

};




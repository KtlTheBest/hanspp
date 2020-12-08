
package semantics;

public abstract class SanityChecks 
{

  // Context is used only for generation of readable errors.
  // It could be something like "function f " or "struct f";

  public static void 
    checkwellformed( type.StructStore structdefs, 
        java.lang.String context, type.Type tp ) 
      throws Error
    {
      System.out.println( "checkwellformed - checking " + context + " " + tp ); 

      // It is a recursive procedure:

      if( tp instanceof type.Array )
      {
        type.Array arr = (type.Array) tp;
        if( arr.s < 0 )
        {
          throw new Error.checkwellformed( 
              context, "size is negative in " + tp. toString( )); 
        }
        checkwellformed( structdefs, context, arr.tp );
        return; 
      }

      if( tp instanceof type.Void ||
          tp instanceof type.Bool ||
          tp instanceof type.Char ||
          tp instanceof type.Integer ||
          tp instanceof type.Double )
      {
        return;    // nothing to check. 
      }
    }


  public static void 
    checkwellformed( type.StructStore structdefs, 
        java.lang.String structname, type.FieldArray struct ) 
      throws Error
    {
      for( int i1 = 0; i1 < struct. nrFields(); ++ i1 )
      {
        for( int i2 = 0; i2 < i1; ++ i2 )
        {
          if(struct.getName(i1).equals(struct.getName(i2))){
            throw new Error.checkwellformed("structdef " + structname, "has a multiple fields with name " + struct.getName(i1));
          }
        }
      }

      for( int i = 0; i < struct. nrFields(); ++ i )
      {
        checkwellformed(structdefs, structname + ":" + struct.getName(i), struct.getType(i));
        if(struct.getType(i) instanceof type.Void){
          throw new Error.checkwellformed("structdef " + structname, struct.getName(i) + " is of type Void");
        }
      }
    }   


  public static void
    checknotcircular( type.StructStore structdefs,
        java.util.Set< java.lang.String > visitedset,
        java.util.Deque< java.lang.String > visitedstack, 
        java.lang.String structname, type.FieldArray fields ) 
      throws Error 
    {
      if( visitedstack.contains( structname ) )
      {
        throw new Error.checkwellformed( "structdef " + structname, 
            "definition is circular" );
      }

      for(int i = 0; i < fields.nrFields(); ++i){
        type.Type tp = fields.getType(i);
        visitedstack. addFirst(structname);
        checknotcircular(structdefs, visitedset, visitedstack, tp);
        visitedstack. removeFirst();
      }

      visitedset. add(structname);
    }


  public static void
    checknotcircular( type.StructStore structdefs,
        java.util.Set< java.lang.String > visitedset,
        java.util.Deque< java.lang.String > visitedstack,
        type.Type tp )  
      throws Error 
    {
      if(tp instanceof type.Struct) {
        type.Struct struct = (type.Struct) tp;
        checknotcircular(
            structdefs, 
            visitedset, 
            visitedstack, 
            struct.name, 
            structdefs.get(struct.name));
      }
    }

  public static void
    checkFunctionHeader( type.StructStore structdefs, 
        java.lang.String funcname, 
        type.FieldArray parameters,
        type.Type returntype ) 
      throws Error 
    {
      checkwellformed(structdefs, funcname, returntype);
      for(int i = 0; i < parameters.nrFields(); ++ i){
        for(int j = 0; j < i; ++ j){
          if(parameters.getName(i).equals(parameters.getName(j))){
            throw new Error.checkwellformed("function " + funcname, "There are multiple instances of " + parameters.getName(i) + " in parameter definition");
          }
        }
        type.Type tp = parameters.getType(i);
        checkwellformed(structdefs, "function " + funcname, tp);
      }
    }

}




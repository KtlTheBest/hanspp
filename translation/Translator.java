
package translation;

import simulator.Instruction;

// No need to call it 'FunctionTranslator' or whatever,
// because the package name already contains the word
// translation.

public class Translator
{
  cflat.Program prog;

  java.lang.String funcname;
  semantics.VarStack localvars;

  simulator.FunctionBody function; 
  // Function that we are building.

  simulator.Block currentblock;  
  // Block into which we are currently emitting.

  NameGenerator uservars;
  NameGenerator hiddenvars; 
  NameGenerator registers;
  NameGenerator blocknames; 

  public Translator( cflat.Program prog,
      java.lang.String funcname,
      cflat.Program.FuncDef def,
      simulator.FunctionBody function )
  {
    this. prog = prog;

    this. funcname = funcname;
    this. localvars = new semantics.VarStack( );
    this. function = function; 
    this. currentblock = function. create( "entry" );

    this. uservars = new NameGenerator( "Var" ); 
    this. hiddenvars = new NameGenerator( "Hidden" );
    this. registers = new NameGenerator( "R" );
    this. blocknames = new NameGenerator( "Block" ); 
  }


  public void translate( cflat.Program.FuncDef func )
  {
    // Create a local variable called [retvar] for the return value:

    localvars. push( "[retvar]", 1, func. returntype );
    // The 1 is a place holder for the size of the return type. 
    // At this moment, it seems that we do not need sizes at all 
    // during translation. 

    // Create parameters as local variables:

    int i = func. parameters. nrFields( );
    while( i -- != 0 )
    {
      localvars. push( func. parameters.getName(i), 1, 
          func. parameters.getType(i) );
      // Again, 1 is placeholder for the size. It seems we will not
      // be using sizes during translation. 
    }

    translateStatement( func. body );  
  }

  // We append the translation of stat to currentblock.
  // We may also decide to start a new block.

  void translateStatement( ast.Tree stat )
  {
    System.out.println( "Trying to translate " );
    System.out.println( stat ); 
    System.out.println( localvars );

    if( stat instanceof ast.Apply )
    {
      ast.Apply appl = (ast.Apply) stat;

      if( appl. function. equals( "[if]" ) && appl. sub. length == 2 )
      {
        java.lang.String after = blocknames. create( );

        java.lang.String cond = regtranslateExpr( appl. sub[0] );
        System.out.println( "our register is " + cond );
        emit( new Instruction.Iffalse( cond, after ));
        emit( new Instruction.Comment( "start of yes-block of if : " ));

        translateStatement( appl. sub[1] );
        emit( new Instruction.Goto( after )); 

        currentblock = function. create( after );
        return;  
      }

      if( appl. function. equals( "[if]" ) && appl. sub. length == 3 )
      {
        java.lang.String elseblock = blocknames. create( );
        java.lang.String after = blocknames. create( );

        java.lang.String cond = regtranslateExpr( appl. sub[0] );
        emit( new Instruction.Iffalse( cond, elseblock ));
        emit( new Instruction.Comment( "start of yes-block of if : " ));

        translateStatement( appl. sub[1] );
        emit( new Instruction.Goto( after )); 

        currentblock = function. create( elseblock );
        emit( new Instruction.Comment( "elseblock : " )); 
        translateStatement( appl. sub[2] );
        emit( new Instruction.Goto( after ));

        currentblock = function. create( after );
        return;
      }

      if( appl. function. equals( "[while]" ) && appl. sub. length == 2 )
      {
        java.lang.String repeat = blocknames. create( );
        // Where we will jump back. This creates only the 
        // name, not the blocks. 

        java.lang.String after = blocknames. create( );
        // Where we continue after the while.

        emit( new Instruction.Goto( repeat ));

        // Now create the repeat block and make it current block: 

        currentblock = function. create( repeat );

        java.lang.String cond = regtranslateExpr( appl. sub[0] ); 
        System.out.println( "our register is " + cond ); 

        emit( new Instruction.Iffalse( cond, after ));
        emit( new Instruction.Comment( "start of body of while " 
              + repeat + " : " ));

        translateStatement( appl. sub[1] ); 
        emit( new Instruction.Goto( repeat )); 

        // We create a new block with label after, and 
        // make it current block.

        currentblock = function. create( after );
        return; 
      }      

      if( appl. function. equals( "[compound]" ))
      {
        int s = localvars. nrVariables( );

        for( int i = 0; i != appl. sub. length; ++ i )
          translateStatement( appl. sub[i] );  
        // The idea is that translateStatement may decide 
        // to start a new block. 

        emitDeallocs(s);  
        localvars. restore(s);
        return; 
      }

      if( appl. function. equals( "[print]" ) &&
          appl. sub. length == 1 )
      {
        if( appl.sub[0] instanceof ast.String ) 
        {
          java.lang.String string = ( (ast.String) appl.sub[0] ).s;
          emit( new Instruction.PrintString( string ));
          return; 
        }

        type.Type tp = appl.sub[0]. type;   // It must be Rvalue.

        if( tp instanceof type.Bool ||
            tp instanceof type.Char ||
            tp instanceof type.Integer ||
            tp instanceof type.Double )
        {
          java.lang.String reg = regtranslateExpr( appl. sub[0] );
          emit( new Instruction.Print( reg ));
          return; 
        } 

      }

      if( appl. function. equals( "[newline]" ) &&
          appl. sub. length == 0 )
      {
        emit( new Instruction.NewLine( ));
        return;
      }

      if( appl. function. equals( "[trace]" ) &&
          appl. sub. length == 0 )
      {
        emit( new Instruction.Trace( ));
        return;
      }

      if( appl. function. equals( "[return]" ) &&
          appl. sub. length == 0 )
      {
        emitDeallocs(1);   
        // Emit statements that deallocate everything 
        // except the [return] variable. 
        emit( new Instruction.Return( ));
        return;
      }

      if( appl. function. equals( "[expr]" ) &&
          appl. sub. length == 1 )
      {
        type.Type tp = appl.sub[0]. trueType( ); 

        if( issimple( tp ) && !isUserFunctionCall( appl. sub[0] ))
        {
          regtranslateExpr( appl. sub[0] );  
          // and we ignore the register
          return;
        }
        else
        {
          memtranslateExpr( appl. sub[0] ); 
          if( ! ( tp instanceof type.Void )) 
          {
            emitDeallocs( localvars. nrVariables( ) - 1 );
            localvars. restore( localvars. nrVariables( ) - 1 );
          }
          return; 
        }
      }

      if( appl. function. equals( "[decl]" ) && 
          appl. sub. length == 1 )
      {
        java.lang.String id = ( (ast.Identifier) appl.sub[0] ). id; 
        type.Type tp = appl. type; 
        localvars. push( id, 1, tp );

        java.lang.String resreg = uservars. create( );  
        emit( new simulator.Instruction.Alloc( resreg, tp ));
        return; 
      }

    }

    throw new NotFinished( "function " + funcname + "\n" + stat + 
        "cannot translate: unimplemented statement type" ); 
  }


  java.lang.String regtranslateExpr( ast.Tree expr )
  {
    // System.out.println( "Translating expression into register" );
    // System.out.println( expr );

    // Constant types:

    System.out.println("Hello from Arailym in regtranslateExpr\n" + expr);

    if( expr instanceof ast.Pointer )
    {
      java.lang.String resreg = registers.create( );
      emit( new Instruction.Comment( "The type of pointer is " + ( (type.Pointer) expr.type). tp + " and the tree looks like this:\n" + expr) );
      type.Type trueType = ((type.Pointer) expr.type). tp;
      if( trueType instanceof type.Void ) trueType = new type.Pointer( trueType );
      emit( new Instruction.Constant( resreg, new simulator.Memory.Pointer(((ast.Pointer) expr).p), trueType ) );
      return resreg;
    }

    if( expr instanceof ast.Bool )
    {
      java.lang.String result = registers. create( );
      emit( new Instruction.Constant( result, ((ast.Bool) expr).b,
            expr. type ));
      return result;
    }

    if( expr instanceof ast.Char ) 
    {
      java.lang.String result = registers. create( );
      emit( new Instruction.Constant( result, ((ast.Char) expr).c,
            expr. type ));
      return result; 
    }

    if( expr instanceof ast.Integer )
    {
      java.lang.String result = registers. create( );
      emit( new Instruction.Constant( result, ((ast.Integer) expr).i,
            expr. type ));
      return result; 
    }

    if( expr instanceof ast.Double )
    {
      java.lang.String result = registers. create( );
      emit( new Instruction.Constant( result, ((ast.Double) expr).d,
            expr. type ));
      return result; 
    }

    if( expr instanceof ast.Identifier )
    {
      java.lang.String var = ((ast.Identifier) expr ). id; 

      int index = localvars. getIndex( var );
      java.util.ArrayList< type.Type > skipped =
        new java.util.ArrayList<> ( );

      int i = localvars. nrVariables( );
      while( i > index + 1 )
      {
        -- i;
        skipped. add( localvars. getType(i) );
      }
      java.lang.String result = registers. create( ); 
      emit( new Instruction.Variable( result, 
            expr. trueType( ), 
            skipped.toArray( new type.Type[0] )));
      return result;
    }

    if( isUserFunctionCall( expr ))
    {
      memtranslateExpr( expr );

      type.Type rettype = expr.type;
      java.lang.String unused = registers. create( );
      java.lang.String result = registers. create( ); 
      emit( new Instruction.Variable( unused, new type.Pointer( rettype ))); 
      emit( new Instruction.Load( result, rettype, unused ));  
      emitDeallocs( localvars. nrVariables( ) - 1 );  
      localvars. restore( localvars. nrVariables( ) - 1 );
      return result; 
    }

    if( expr instanceof ast.Apply && 
        ((ast.Apply) expr ).sub. length == 1 )
    {
      System.out.println("Hello from Zhalgas\n" + expr);
      ast.Apply appl = (ast.Apply) expr;  
      java.lang.String unop = appl. function;  
      java.lang.String reg1 = regtranslateExpr( appl.sub[0] );

      if( unop. equals( "[load]" ))
      {
        java.lang.String result = registers. create( );
        emit( new Instruction.Load( result, appl. trueType( ), reg1 ));
        return result;
      }

      if( unop. equals( "[conv]" ))
      {
        type.Type from = appl.sub[0]. trueType( );
        type.Type into = appl. trueType( );

        // If the types are the same, nothing needs to be done: 

        if( from. equals( into ))
          return reg1; 

        // If both types are pointers, we also assume that nothing needs
        // to be done:

        java.lang.String result = registers. create( );

        if( from instanceof type.Pointer && into instanceof type.Pointer ){
          emit( new Instruction.Conv( result, into, reg1 ) );
        }

        // In other cases, we emit a Conv instruction and hope for
        // the best:

        emit( new Instruction.Conv( result, into, reg1 )); 
        return result;  
      }

      if( unop. equals( "neg" ))
      {
        java.lang.String resReg = registers.create( );
        emit( new Instruction.Unary(unop, resReg, appl.type, reg1) );
        return resReg;
      }

      if( unop. equals( "[xpp]" ) ||
          unop. equals( "[xmm]" ) ||
          unop. equals( "[ppx]" ) ||
          unop. equals( "[mmx]" ) )
      {
        java.lang.String tempreg = registers. create( );
        java.lang.String regval = registers. create( );
        emit( new Instruction.Constant( tempreg, new java.lang.Integer(1), appl.trueType( ) ) );
        emit( new Instruction.Load( regval, expr. type, reg1 ) );

        java.lang.String resReg = registers. create( );
        java.lang.String op = "string";

        if( unop. equals("[xpp]")){
          op = "add";
          resReg = regval;
        }
        if( unop. equals("[xmm]")){
          op = "sub";
          resReg = regval;
        }
        if( unop. equals("[ppx]")){
          op = "add";
        }
        if( unop. equals("[mmx]")){
          op = "sub";
        }

        emit( new Instruction.Comment( "Trying to perform " + unop + " on " + regval + " and " + tempreg + " and store result in " + resReg ) );
        emit( new Instruction.Binary(
              op,
              resReg,
              appl.trueType(),
              regval,
              tempreg
              ));
        emit( new Instruction.Store( resReg, reg1 ) );
        return resReg;
      }

    }

    if( expr instanceof ast.Apply && 
        ((ast.Apply) expr ).sub. length == 2 ) 
    {
      System.out.println("Hello from Zhuldyz: \n" + expr);
      ast.Apply appl = (ast.Apply) expr;  
      java.lang.String binop = appl. function; 
      System.out.println(binop);
      java.lang.String reg1 = regtranslateExpr( appl.sub[0] );
      System.out.println(reg1);
      java.lang.String reg2 = regtranslateExpr( appl.sub[1] );
      System.out.println(reg2);
      // Here is room for optimization, one could do the 
      // bigger first, or better said: The wider first. 

      if( binop. equals( "[assign]" ))
      {
        emit( new Instruction.Store( reg2, reg1 ));
        return reg2;
      } 

      if( binop. equals( "eq" ) || binop. equals( "ne" ) || 
          binop. equals( "lt" ) || binop. equals( "gt" ) || 
          binop. equals( "le" ) || binop. equals( "ge" )) 
      {
        System.out.println("Entered the if from Zhuldyz");
        java.lang.String result = registers. create( );
        emit( new Instruction.Binary( binop, result, new type.Bool( ), 
              reg1, reg2 ));
        System.out.println("RETURNING!");
        return result;
      }

      if( binop. equals( "add" ) ||
          binop. equals( "sub" ) ||
          binop. equals( "mul" ) ||
          binop. equals( "truediv" ) ||
          binop. equals( "mod" ))
      {
        java.lang.String resReg = registers.create( );
        emit( new Instruction.Binary( binop, resReg, appl.trueType( ), reg1, reg2 ) );
        return resReg;
      }
    }      

    if( expr instanceof ast.Apply && 
        ((ast.Apply) expr ).sub. length == 3 ) 
    {
      ast.Apply appl = (ast.Apply) expr;  
      java.lang.String terop = appl. function; 
      java.lang.String reg1 = regtranslateExpr( appl.sub[0] );
      // Here is room for optimization, one could do the 
      // bigger first, or better said: The wider first. 

      if( terop. equals( "[??]" ))
      {
        java.lang.String resReg = registers.create();
        java.lang.String failblock = blocknames.create( );
        java.lang.String afterblock = blocknames.create( );

        emit( new Instruction.Iffalse(reg1, failblock) );

        java.lang.String reg2 = regtranslateExpr( appl.sub[1] );
        emit( new Instruction.Load(resReg, appl.sub[1].type, reg2) );
        emit( new Instruction.Goto(afterblock));

        currentblock = function.create(failblock);
        java.lang.String reg3 = regtranslateExpr( appl.sub[2] );
        emit( new Instruction.Goto( afterblock ) );

        currentblock = function.create(afterblock);

        return resReg;
      } 

    }      

    if( expr instanceof ast.Select )
    {
      ast.Select sel = (ast.Select) expr;
      ast.Tree sub = sel.sub;

      java.lang.String structreg = regtranslateExpr( sub );
      System.out.println("Trying to get closer to the truth");

      System.out.println(sel. field);

      java.lang.String structname = ((type.Struct) sub.type).name;

      System.out.println("Trying to get closer to the truth.");
      System.out.println(structname);
      int index = sel. index;
      java.lang.String offsetreg = registers. create( );

      System.out.println("Trying to get closer to the truth..");

      emit( new Instruction.Comment( "Trying to get offset of " + sel.field + " in struct " + structname ) );
      emit( new Instruction.Comment( "The offset is " + index ) );
            
      type.Type fieldtype = new type.Pointer( prog. structdefs. fieldtype( structname, index ) );

      java.lang.String fieldreg = registers. create( );

      emit( new Instruction.Constant( offsetreg, new Integer( index ), new type.Integer() ) );
      emit( new Instruction.Binary( "add", fieldreg, fieldtype, structreg, offsetreg ) );

      return fieldreg;
    }

    throw new NotFinished( "function " + funcname + "\n" + expr +
        "cannot regtranslate: unimplemented expression type" );
  }

  java.lang.String extractid(ast.Tree expr){
    if(expr instanceof ast.Identifier)
    {
      return ((ast.Identifier) expr). id;
    }

    if(expr instanceof ast.Apply)
    {
      ast.Apply appl = (ast.Apply) expr;
      for(int i = 0; i < appl.sub.length; ++ i)
      {
        java.lang.String t = extractid(appl.sub[i]);
        if(t.equals("not found")) continue;
        return t;
      }
    }

    return "not found";
  }

  // Translate expr into memory: That means: 
  // Allocate space for the result on top of the stack, and write 
  // the result into
  // this space. Also create a hidden variable for the result
  // in localvars.

  void memtranslateExpr( ast.Tree expr )
  {
    System.out.println( "Translating expression into memory" );
    System.out.println( expr );
    System.out.println( localvars );

    // There are only a few cases, user function calls,
    // field selection, and assignment.
    // The other cases are handled by calling regtranslateExpr( ).

    if( isUserFunctionCall( expr ))
    {
      ast.Apply appl = (ast.Apply) expr;

      type.Type restype = appl. trueType( );
      java.lang.String resname = hiddenvars. create( );

      if( ! ( restype instanceof type.Void ))
      {
        emit( new Instruction.Alloc( resname, restype ));
        localvars. push( "[" + resname + "]", 1, restype );
        // Place holder for the result.
      }

      // Translate arguments in reverse order, so that 
      // they appear in right order on the stack.

      int i = appl. sub. length;
      while( i -- != 0 )  
        memtranslateExpr( appl.sub[i] );

      emit( new Instruction.Call( appl. function )); 
      localvars. restore( 
          localvars. nrVariables( ) - appl. sub. length ); 

      return; 
    }

    if( expr instanceof ast.Apply &&
        ((ast.Apply) expr ). sub. length == 1 )
    {
      ast.Apply appl = (ast.Apply) expr;
      java.lang.String unop = appl. function;
      ast.Tree sub = appl.sub[0];

      if( unop. equals( "[load]" ))
      {
        java.lang.String from = regtranslateExpr( sub ); 

        java.lang.String resname = hiddenvars. create( ); 
        java.lang.String into = registers. create( );
        emit( new Instruction.Alloc( resname, sub.type )); 
        localvars. push( "[" + resname + "]", 1, sub.type ); 
        emit( new Instruction.Variable( into, sub.trueType( ) ));
        emit( new Instruction.Memcopy( from, into ));
        return;
      }

      if( unop. equals( "select" ) ){
        System.out.println("Hello from Kurmankul and Zhuldyz");
      }
    }

    if( expr instanceof ast.Apply &&
        ((ast.Apply) expr ). sub. length == 2 )
    {
      ast.Apply appl = (ast.Apply) expr;
      java.lang.String binop = appl. function;
      ast.Tree sub1 = appl.sub[0];
      ast.Tree sub2 = appl.sub[1];

      if( binop. equals( "[assign]" ))
      {
        System.out.println( "assign" ); 

        memtranslateExpr( sub2 ); 
        java.lang.String into = regtranslateExpr( sub1 );
        java.lang.String from = registers. create( );

        emit( new Instruction.Variable( from, sub1. trueType( ))); 
        emit( new Instruction.Memcopy( from, into ));
        return; 
      }
    }

    if( expr instanceof ast.Select )
    {
      int index = ((ast.Select) expr ). index;
      ast.Tree sub = ((ast.Select) expr ). sub;

      // Place where result will be stored:

      java.lang.String resname = hiddenvars. create( );
      emit( new Instruction.Alloc( resname, expr. type ));
      localvars. push( "[" + resname + "]", 1, expr. type );

      memtranslateExpr( sub );  

      java.lang.String r1 = registers. create( );
      java.lang.String r2 = registers. create( );
      java.lang.String r3 = registers. create( );
      java.lang.String r4 = registers. create( );

      emit( new Instruction.Variable( r1, new type.Pointer( sub. type )));
      emit( new Instruction.Constant( r2, index, new type.Integer( ))); 
      emit( new Instruction.Binary( "add", r3, 
            new type.Pointer( expr. type ),
            r1, r2 ));
      emit( new Instruction.Variable( r4, new type.Pointer( expr. type ),
            sub. type ));
      emit( new Instruction.Memcopy( r3, r4 ));

      emitDeallocs( localvars. nrVariables( ) - 1 );
      localvars. restore( localvars. nrVariables( ) -1 );
      return;
    } 

    // From the slides:
    // If the previous cases cannot be applied, then t.truetype must be
    // simple. We call translate and store the result in memory.

    if( !issimple( expr.trueType( )))
    {
      throw new NotFinished( "function " + funcname + "\n" +
          "type not simple, this cannot happen" );
    } 

    java.lang.String result = regtranslateExpr( expr );

    // We need to store result into memory:

    java.lang.String resname = hiddenvars. create( );
    emit( new Instruction.Alloc( resname, expr.trueType( )));
    localvars. push( "[" + resname + "]", 1, expr. trueType( ));

    java.lang.String resultaddr = registers. create( ); 
    emit( new Instruction.Variable( resultaddr, 
          new type.Pointer( expr. trueType( ))));
    emit( new Instruction.Store( result, resultaddr ));  
  }


  public void emit( simulator.Instruction ins )
  {
    System.out.println( "emit:   " + ins ); 
    currentblock. add( ins ); 
  }


  // A type is simple if it can be put in a register.

  boolean issimple( type.Type tp )
  {
    if( tp instanceof type.Void ) return true;
    if( tp instanceof type.Bool ) return true;
    if( tp instanceof type.Char ) return true;
    if( tp instanceof type.Integer ) return true;
    if( tp instanceof type.Double ) return true;
    if( tp instanceof type.Pointer ) return true;

    return false;   // Only Structs are not simple. 

  }

  // It is a user call if it has a definition in
  // funcdefs. This is of course ridiculous. In a real implementation,
  // one would put a pointer to the function in the AST after checking
  // it, so that there is no need to lookup anything twice 

  boolean isUserFunctionCall( ast.Tree expr )
  {
    if( expr instanceof ast.Apply )
    {
      ast.Apply appl = (ast.Apply) expr;
      java.lang.String f = appl. function;
      return prog.funcdefs.get(f) != null;
    }
    return false; 
  }


  // Emit required deallocations to restore localvars to size s. 
  // Don't restore localvars. 

  void emitDeallocs( int s )
  {
    // We won't emit an empty Dealloc.

    if( s < localvars. nrVariables( ))
    {
      java.util.ArrayList< type.Type > remove =
        new java.util.ArrayList<>( );

      int i = localvars. nrVariables( );
      while( i > s )  
      {
        -- i;
        remove. add( localvars. getType(i));
      }

      emit( new Instruction.Dealloc(
            remove.toArray( new type.Type[0] )));
    }
  }

}


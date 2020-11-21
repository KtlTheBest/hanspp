
import java_cup.runtime.* ; 

%%

%class Lexer
%unicode
%cup
%line
%column
%yylexthrow ScanError

%{
   StringBuffer readstring = new StringBuffer();

   private Symbol symbol( int type ) { 
      return new Symbol( type, yyline, yycolumn ); 
   }

   private Symbol symbol( int type, Object value ) 
   {
      return new Symbol( type, yyline, yycolumn, value ); 
   }
%}

LineTerminator = \r|\n|\r\n
WhiteSpace = {LineTerminator} | [ \t\f]
BlockComment = \/\*( (\*)+[^/*] | [^*])*(\*)+\/
QuotedString = \"( [^\"\\] | (\\n) | (\\t) | (\\\\) )* \"

%%

"void" { return symbol( sym. VOID ); }
"bool" { return symbol( sym. BOOL ); }
"char" { return symbol( sym. CHAR ); }
"integer" { return symbol( sym. INTEGER ); }

"null" { return symbol( sym.POINTERCONST, new ast.Pointer(0) ); }

"("    { return symbol( sym. LPAR ); }
")"    { return symbol( sym. RPAR ); }

[:jletter:]([:jletter:] | [0-9])*
          { return symbol( sym.IDENTIFIER, new ast.Identifier( yytext( ) )); }

"0" | [1-9][0-9]*
          { return symbol( sym.INTEGERCONST,
                     new ast.Integer( new java.lang.Integer( yytext( ) ))); }

{WhiteSpace}     { }
{BlockComment}  { } 
"#"              { return symbol( sym. EOF ); }

// Error fallback:

[^]    { throw new ScanError( "Unrecognized character <" + yytext( ) + ">",
                              yyline, yycolumn ); }


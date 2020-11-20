
// This is supposed to specify LISP tokens.

import java_cup.runtime.* ; 

%%

%class Lexer
%unicode
%cup
%line
%column

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
InputCharacter = [^\r\n]
WhiteSpace = {LineTerminator} | [ \t\f]

Integer = (0|[1-9][0-9]*)
Double  = (0|[1-9][0-9]*)\.[0-9]+((e|E)-?0*[1-9][0-9]*)?

Operator = [-+*/<>=!|\\/\^&~]
Alphabet = [a-zA-Z_]
AlnumCharsAndMore = [a-zA-Z0-9_]
Identifier = [:jletter:]([:jletter:] | [0-9])*

// reserved words

IfClause = if
ThenClause = then
ElseClause = else
WhileClause = while
DoClause = do
PointerClause = pointer
ArrayClause = array
FunctionClause = function
StructClause = structdef
ConstantClause = constant
NullClause = null
BoolClause = bool
CharClause = char
IntegerClause = integer
DoubleClause = double
VoidClause = void
PrintClause = print
ReturnClause = return
BeginClause = begin
EndClause = end

MultiLineComment = "/*"([^*]|\*[^/])*"*/"
SingleLineComment = "//"{InputCharacter}*{LineTerminator}?
QuotedString = \"( [^\"\\] | (\\n) | (\\t) | (\\\\) )* \"

%%

"="    { return symbol( sym. ASSIGN ); }
"."    { return symbol( sym. DOT ); }
"["    { return symbol( sym. LSQPAR ); }
"]"    { return symbol( sym. RSQPAR ); }
"("    { return symbol( sym. LPAR ); }
")"    { return symbol( sym. RPAR ); }
"."    { return symbol( sym. DOT ); }
","    { return symbol( sym. COMMA ); }
"+"    { return symbol( sym. ADD ); }
"-"    { return symbol( sym. SUB ); }
"*"    { return symbol( sym. MUL ); }
"/"    { return symbol( sym. TRUEDIV ); }
"%"    { return symbol( sym. MODULO ); }
"&&"   { return symbol( sym. AND ); }
"||"   { return symbol( sym. OR ); }
"?"    { return symbol( sym. QUESTION ); }
"&"    { return symbol( sym. AMPERSAND ); }
"++"   { return symbol( sym. PLUSPLUS ); }
"--"   { return symbol( sym. MINUSMINUS ); }
"=="   { return symbol( sym. EQ ); }
"<="   { return symbol( sym. LE ); }
">="   { return symbol( sym. GE ); }
"!="   { return symbol( sym. NE ); }
">"    { return symbol( sym. GT ); }
"<"    { return symbol( sym. LT ); }
"!"    { return symbol( sym. NOT ); }
"?"    { return symbol( sym. QUESTION ); }
"->"   { return symbol( sym. ARROW ); }
":"    { return symbol( sym. COLON ); }
";"    { return symbol( sym. SEMICOLON ); }

{WhiteSpace}     { }
{SingleLineComment} { }
{MultiLineComment} { }
";"              { return symbol( sym. EOF ); }
{Integer} { return symbol( sym.INTEGER, new ast.Integer(new java.lang.Integer( yytext() ) ) ); }
{Double}  { return symbol( sym.DOUBLE, new ast.Double(new java.lang.Double( yytext() ) ) ); }

{IfClause} { return symbol( sym.IF ); }
{ThenClause} { return symbol( sym.THEN ); }
{ElseClause} { return symbol( sym.ELSE ); }
{WhileClause} { return symbol( sym.WHILE ); }
{DoClause} { return symbol( sym.DO ); }
{PointerClause} { return symbol( sym.POINTER ); }
{ArrayClause} { return symbol( sym.ARRAY ); }
{FunctionClause} { return symbol( sym.FUNCTION ); }
{StructClause} { return symbol( sym.STRUCT ); }
{ConstantClause} { return symbol( sym.CONSTANT ); }
{NullClause} { return symbol( sym.NULL ); }
{BoolClause} { return symbol( sym.BOOL ); }
{CharClause} { return symbol( sym.CHAR ); }
{IntegerClause} { return symbol( sym.INT ); }
{DoubleClause} { return symbol( sym.DBL ); }
{VoidClause} { return symbol( sym.VOID, new ast.Pointer(0)); }
{PrintClause} { return symbol( sym.PRINT ); }
{ReturnClause} { return symbold( sym.RETURN ); }
{BeginClause} { return symbold( sym.BEGIN ); }
{EndClause} { return symbold( sym.END ); }

{Identifier} { return symbol( sym.IDENTIFIER, new ast.Identifier( new java.lang.String( yytext() ) ) ); }

// Error fallback:

[^]    { throw new java.lang.Error( "Illegal character <" + yytext( ) + ">" ); }


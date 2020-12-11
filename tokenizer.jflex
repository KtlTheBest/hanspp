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

IntegerConst = (0|[1-9][0-9]*)
DoubleConst  = (0|[1-9][0-9]*)\.[0-9]+((e|E)-?0*[1-9][0-9]*)?
CharConstAlnum = \'[a-zA-Z0-9]\'
StringConst = \".+?\"

Operator = [-+*/<>=!|\\/\^&~]
Alphabet = [a-zA-Z_]
AlnumCharsAndMore = [a-zA-Z0-9_]
Identifier = [:jletter:]([:jletter:] | [0-9])*

// reserved words

TraceClause = trace
NewlineClause = newline
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
SingleLineCommentOld = "//"{InputCharacter}*{LineTerminator}?
SingleLineComment = "#"{InputCharacter}*{LineTerminator}?
QuotedString = \"( [^\"\\] | (\\n) | (\\t) | (\\\\) )* \"

Alnum = [0-9a-zA-Z]
SpecialChar = [^\r\n\t ]
EscapeSymbols = [brtnf\'\"\\0]
EscapeChar = "\\"{EscapeSymbols}

CharConst = \'({Alnum}|{SpecialChar}|{EscapeChar})\'

%%
"++"   { return symbol( sym. PLUSPLUS ); }
"--"   { return symbol( sym. MINUSMINUS ); }
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

"true"  { return symbol( sym. BOOLCONST, new ast.Bool(true) ); }
"false" { return symbol( sym. BOOLCONST, new ast.Bool(false) ); }
{IntegerConst} { return symbol( sym.INTEGERCONST, new ast.Integer(new java.lang.Integer( yytext() ) ) ); }
{DoubleConst}  { return symbol( sym.DOUBLECONST, new ast.Double(new java.lang.Double( yytext() ) ) ); }
{QuotedString}  { String x = yytext(); return symbol( sym.STRING, new ast.String(new java.lang.String( x.substring(1, x.length() - 1) ) ) ); }
{CharConst}  { 
  String x = yytext();
  char c = x.charAt(0);
  if(c == '\\'){
    c = x.charAt(1);
    if(c == '0') return symbol( sym. POINTERCONST, new ast.Pointer(0) );
    if(c == 'b') c = '\b';
    if(c == 'f') c = '\f';
    if(c == 't') c = '\t';
    if(c == 'r') c = '\r';
    if(c == 'n') c = '\n';
  }
  
  return symbol( sym. CHARCONST, new ast.Char(c) );}

{TraceClause} { return symbol( sym. TRACE ); }
{NewlineClause} { return symbol( sym. NEWLINE ); }
{IfClause} { return symbol( sym.IF ); }
{ThenClause} { return symbol( sym.THEN ); }
{ElseClause} { return symbol( sym.ELSE ); }
{WhileClause} { return symbol( sym.WHILE ); }
{DoClause} { return symbol( sym.DO ); }

{PointerClause} { return symbol( sym.POINTER ); }
{ArrayClause} { return symbol( sym.ARRAY ); }
{FunctionClause} { return symbol( sym.FUNCTION ); }

{StructClause} { return symbol( sym.STRUCTDEF ); }
{ConstantClause} { return symbol( sym.CONSTANT ); }
{NullClause} { return symbol( sym.POINTERCONST, new ast.Pointer(0) ); }
{BoolClause} { return symbol( sym.BOOL ); }
{CharClause} { return symbol( sym.CHAR ); }
{IntegerClause} { return symbol( sym.INTEGER ); }
{DoubleClause} { return symbol( sym.DOUBLE ); }
{VoidClause} { return symbol( sym.VOID, new ast.Pointer(0)); }

{PrintClause} { return symbol( sym.PRINT ); }
{ReturnClause} { return symbol( sym.RETURN ); }
{BeginClause} { return symbol( sym.BEGIN ); }
{EndClause} { return symbol( sym.END ); }

{Identifier} { return symbol( sym.IDENTIFIER, new ast.Identifier( new java.lang.String( yytext() ) ) ); }

// Error fallback:

[^]    { throw new java.lang.Error( "Illegal character <" + yytext( ) + "> at line " + Integer.toString(yyline + 1) + " and column " + Integer.toString(yycolumn + 1) ); }


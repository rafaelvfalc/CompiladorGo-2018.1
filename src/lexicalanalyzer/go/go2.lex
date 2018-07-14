/* Analisador lexico para a disciplina de Compiladores 
*/
                 
package lexicalanalyzer.go;

import java_cup.runtime.*;
%%

%class GoLexicalAnalyzer
%line
%column

%cupsym sym

%cup


%{
  StringBuilder string = new StringBuilder();  

  private Symbol symbol(int type) {
    return new Symbol(type, yyline, yycolumn);
  }

  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline, yycolumn, value);
  }
%}


/* Definicao de palavras/strings, digitos/numeros e espacos
    em branco para utilizacao das regras mais abaixo */ 
    
StringCharacter = [^\r\n\"\\]
LineTerminator = \r|\n|\r\n

WHITE={LineTerminator} | [ \t\f]
Identifier = [:jletter:][:jletterdigit:]*

comentario = "/*" [^*] ~"*/" | "/*" "*"+ "/" | "//" [^\r\n]* {LineTerminator}? |  "/*" "*"+ [^/*] ~"*/"

imaginaryLiteral = i
decIntLiteral = 0 | [0-9][0-9]*
hexIntLiteral = 0 [xX] 0* [0-9a-fA-F] {1,8}
octIntLiteral = 0+ [1-3]? [0-7] {1,15}
octLongLiteral = 0+ 1? [0-7] {1,21} [lL]
stringLiteral = \".*\" | \`.*\`

floatLiteral = [+-]?([0-9]*[.])?[0-9]+
//floatLiteral = ([0-9]+ \. [0-9]* |\. [0-9]+ |[0-9]+) [eE] [+-]? [0-9]+?

%state STRING


%%

/* Simbolos terminais que serao utilizados pelo analisador
   sintatico (CUP) */

<YYINITIAL> {
  
  /* Palavras reservadas de GO*/
  "break"         {return symbol(sym.BREAK, new String(yytext()));}
  "case"          {return symbol(sym.CASE, new String(yytext()));}
  "chan"    	  {return symbol(sym.CHAN, new String(yytext()));}
  "const"         {return symbol(sym.CONST, new String(yytext()));}
  "continue"      {return symbol(sym.CONTINUE, new String(yytext()));}
  "default"       {return symbol(sym.DEFAULT, new String(yytext()));}
  "defer"         {return symbol(sym.DEFER, new String(yytext()));}
  "else"          {return symbol(sym.ELSE, new String(yytext()));}
  "fallthrough"   {return symbol(sym.FALLTHROUGH, new String(yytext()));}
  "for"           {return symbol(sym.FOR, new String(yytext()));}
  "func"          {return symbol(sym.FUNC, new String(yytext()));}
  "go"            {return symbol(sym.GO, new String(yytext()));}
  "goto"          {return symbol(sym.GOTO, new String(yytext()));}
  "if"            {return symbol(sym.IF, new String(yytext()));}
  "import"        {return symbol(sym.IMPORT, new String(yytext()));}
  "interface"     {return symbol(sym.INTERFACE, new String(yytext()));}
  "map"           {return symbol(sym.MAP, new String(yytext()));}
  "package"       {return symbol(sym.PACKAGE, new String(yytext()));}
  "range"         {return symbol(sym.RANGE, new String(yytext()));}
  "return"        {return symbol(sym.RETURN, new String(yytext()));}
  "select"        {return symbol(sym.SELECT, new String(yytext()));}
  "struct"        {return symbol(sym.STRUCT, new String(yytext()));}
  "switch"        {return symbol(sym.SWITCH, new String(yytext()));}
  "type"          {return symbol(sym.TYPE, new String(yytext()));}
  "var"           {return symbol(sym.VAR, new String(yytext()));}

/* Tipos primitivos de GO */
  "bool"          {return symbol(sym.BOOL, new String(yytext())); }
  "string"        {return symbol(sym.STRING, new String(yytext())); }
  "int"           {return symbol(sym.INT, new String(yytext())); }
  "int8"          {return symbol(sym.INT8, new String(yytext())); }
  "int16"         {return symbol(sym.INT16, new String(yytext())); }
  "int32"         {return symbol(sym.INT32, new String(yytext())); }
  "int64"         {return symbol(sym.INT64, new String(yytext())); }
  "uint"          {return symbol(sym.UINT, new String(yytext()));}
  "uint8"         {return symbol(sym.UINT8, new String(yytext()));}
  "uint16"        {return symbol(sym.UINT16, new String(yytext())); }
  "uint32"        {return symbol(sym.UINT32, new String(yytext())); }
  "uint64"        {return symbol(sym.UINT64, new String(yytext())); }
  "uintptr"       {return symbol(sym.UINTPTR, new String(yytext())); }
  "byte"          {return symbol(sym.BYTE, new String(yytext())); }
  "rune"          {return symbol(sym.RUNE, new String(yytext())); }
  "float32"       {return symbol(sym.FLOAT32, new String(yytext())); }
  "float64"       {return symbol(sym.FLOAT64, new String(yytext()));}
  "complex64"     {return symbol(sym.COMPLEX64, new String(yytext()));}
  "complex128"    {return symbol(sym.COMPLEX128, new String(yytext()));}
  
  /* Literais booleanos */
  
  "true"          {return symbol(sym.TRUE, new String(yytext()));}
  "false"         {return symbol(sym.FALSE, new String(yytext()));}
 
 
 /* Separadores */
  "("             {return symbol(sym.LPAREN, new String(yytext()));}
  ")"             {return symbol(sym.RPAREN, new String(yytext()));}
  "{"             {return symbol(sym.LBRACE, new String(yytext()));}
  "}"             {return symbol(sym.RBRACE, new String(yytext()));}
  "["             {return symbol(sym.LBRACK, new String(yytext()));}
  "]"             {return symbol(sym.RBRACK, new String(yytext()));}
  ";"             {return symbol(sym.SEMICOLON, new String(yytext()));}
  ","             {return symbol(sym.COMMA, new String(yytext()));}
  "."             {return symbol(sym.DOT, new String(yytext()));}
  ":"             {return symbol(sym.COLON, new String(yytext())); }
  
 /* Operadores relacionais e booleanos */
  "="             {return symbol(sym.EQUAL, new String(yytext())); }
  ">"             {return symbol(sym.GREATER, new String(yytext())); }
  "<"             {return symbol(sym.LESS, new String(yytext())); }
  "!"             {return symbol(sym.NOT, new String(yytext())); }
  "=="            {return symbol(sym.EQEQ, new String(yytext())); }
  "<="            {return symbol(sym.LTEQ, new String(yytext())); }
  ">="            {return symbol(sym.GTEQ, new String(yytext())); }
  "!="            {return symbol(sym.NOTEQ, new String(yytext())); }
  "&&"            {return symbol(sym.ANDAND, new String(yytext())); }
  "||"            {return symbol(sym.OROR, new String(yytext())); }
  "++"            {return symbol(sym.PLUSPLUS, new String(yytext())); }
  "--"            {return symbol(sym.MINUSMINUS, new String(yytext())); }
  "+"             {return symbol(sym.PLUS, new String(yytext())); }
  "-"             {return symbol(sym.MINUS, new String(yytext())); }
  "*"             {return symbol(sym.MULT, new String(yytext())); }
  "/"             {return symbol(sym.DIV, new String(yytext())); }
  "&"             {return symbol(sym.AND, new String(yytext())); }
  "|"             {return symbol(sym.OR, new String(yytext())); }
  "^"             {return symbol(sym.XOR, new String(yytext())); }
  "%"             {return symbol(sym.MOD, new String(yytext())); }
  "<<"            {return symbol(sym.LSHIFT, new String(yytext())); }
  ">>"            {return symbol(sym.RSHIFT, new String(yytext())); }
  "+="            {return symbol(sym.PLUSEQ, new String(yytext())); }
  "-="            {return symbol(sym.MINUSEQ, new String(yytext())); }
  "*="            {return symbol(sym.MULTEQ, new String(yytext())); }
  "/="            {return symbol(sym.DIVEQ, new String(yytext())); }
  "&="            {return symbol(sym.ANDEQ, new String(yytext())); }
  "|="            {return symbol(sym.OREQ, new String(yytext())); }
  "^="            {return symbol(sym.XOREQ, new String(yytext())); }
  "%="            {return symbol(sym.MODEQ, new String(yytext())); }
  "<<="           {return symbol(sym.LSHIFTEQ, new String(yytext())); }
  ">>="           {return symbol(sym.RSHIFTEQ, new String(yytext())); }
  "_"             {return symbol(sym.UNDERSCORE, new String(yytext())); }
  "\""            {return symbol(sym.QUOTE, new String(yytext())); }
  "..."           {return symbol(sym.SUSPOINTS, new String(yytext())); }
  "&^"            {return symbol(sym.ANDNOT, new String(yytext())); }
  "&^="           {return symbol(sym.ANDNOTEQ, new String(yytext())); }
  "<-"            {return symbol(sym.CHANNEL, new String(yytext())); }
  ":="            {return symbol(sym.DECEQ, new String(yytext())); }

  {decIntLiteral}           { return symbol(sym.INTEGER_LITERAL, new String(yytext())); }
  {hexIntLiteral}           { return symbol(sym.HEXAL_LITERAL, new String(yytext())); }
  {octIntLiteral}           { return symbol(sym.OCTAL_LITERAL, new String(yytext())); }
  {floatLiteral}            { return symbol(sym.FLOAT_LITERAL, new String(yytext()));}
  {imaginaryLiteral}        { return symbol(sym.IMAGINARY_LITERAL, new String(yytext()));}
  {stringLiteral}           { return symbol(sym.ALL_STRINGS_LITERAL, new String(yytext()));}

  /* Definicao de espaco em branco, id, numero e comentario */

  {WHITE}      {/*Ignore*/}
  {comentario} {/*Ignore*/}
  {Identifier} {return symbol(sym.ID, new String(yytext()));} 
}


<STRING>{
  \"                             { yybegin(YYINITIAL);
                                   return symbol(sym.STRING_LITERAL, new String(yytext())); }
  
  {StringCharacter}+             { string.append( yytext() ); }
  
  "\\b"                          { string.append( '\b' ); }
  "\\t"                          { string.append( '\t' ); }
  "\\n"                          { string.append( '\n' ); }
  "\\f"                          { string.append( '\f' ); }
  "\\r"                          { string.append( '\r' ); }
  "\\\""                         { string.append( '\"' ); }
  "\\'"                          { string.append( '\'' ); }
  "\\\\"                         { string.append( '\\' ); }

  \\.                            { throw new RuntimeException(
                                  "Illegal escape sequence \""+yytext()+"\""); }
  {LineTerminator}               { throw new RuntimeException(
                                       "Unterminated string at end of line"); }  
}

[^]              { throw new RuntimeException("Illegal character \""+yytext()+
                             "\" at line "+yyline+", column "+yycolumn); }
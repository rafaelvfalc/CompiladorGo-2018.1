package lexicalanalyzer.go;

import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.ComplexSymbolFactory.Location;
import java_cup.runtime.Symbol;
import lexicalanalyzer.go.Sym;

%%

%class GoLexicalAnalyzer
%cup
%line
%column
%public
%unicode


%{

	private void ignore(String pattern, String content) {
		System.out.println("Ignorando " + pattern + ", codigo: " + content);
	}
	
	ComplexSymbolFactory symbolFactory;
    public GoLexicalAnalyzer(java.io.Reader in, ComplexSymbolFactory symbol){
    	this(in);
    	symbolFactory = symbol;
    }
  
    private Symbol symbol(int sym) {
    	//System.out.println("Token " + Sym.terminalNames[sym] + ", Linha: " + yyline + ", tamanho:" + yylength());
      	return symbolFactory.newSymbol("sym", sym, new Location(yyline+1,yycolumn+1,yychar), new Location(yyline+1,yycolumn+yylength(),yychar+yylength()));
  	}
  
  	private Symbol symbol(int sym, Object val) {
  		//System.out.println("Token (" + Sym.terminalNames[sym] + ", " + val +  ") , Linha: " + yyline + ", tamanho:" + yylength());
		Location esq = new Location(yyline+1,yycolumn+1,yychar);
		Location dir = new Location(yyline+1,yycolumn+yylength(), yychar+yylength());
		return symbolFactory.newSymbol("sym", sym, esq, dir ,val);
  	}
  	
    private Symbol symbol(int sym, Object val,int buflength) {
        //System.out.println("Token (" + Sym.terminalNames[sym] + ", " + val +  ") , Linha: " + yyline + ", tamanho:" + yylength());
        Location esq = new Location(yyline+1,yycolumn+yylength()-buflength,yychar+yylength()-buflength);
        Location dir = new Location(yyline+1,yycolumn+yylength(), yychar+yylength());
        return symbolFactory.newSymbol("sym", sym, esq, dir ,val);
    }
	
	private void reportError(String invalidPattern, int linha, int coluna) {
		System.err.println("Erro Lexico: " + invalidPattern + ", linha: " + linha + ", coluna: " + coluna);
	}
%}

%eofval{
     return symbolFactory.newSymbol("EOF", Sym.EOF, new Location(yyline+1, yycolumn+1), new Location(yyline+1, yycolumn+1));
%eofval}

// Regex capturando novas linhas
new_line = \r|\n|\r\n

// UNICODE CHAR
unicode_char = [^\r|\n|\r\n]
unicode_1 = [^\r|\n|\r\n|"\""]
unicode_2 = [^\r|\n|\r\n|"`"]

// Letra + digito
letra_unicode = [:letter:]
digito_unicode = [:digit:]

letra_underline = {letra_unicode}|"_"
digito_decimal = [0-9]
digito_octal = [0-7]
digito_hex = [0-9]|[A-F]|[a-f]

// Numeros (decimais, octais, hexadecimais e inteiros)
literal_decimal = [1-9]{digito_decimal}*
literal_octal = "0"{digito_octal}*
literal_hex = "0"("x"|"X"){digito_hex}{digito_hex}*
literal_inteiro = {literal_decimal}|{literal_octal}|{literal_hex}

// decimais + float + exponenciais
decimais = {digito_decimal}{digito_decimal}*
exponentes = ("e"|"E")("+"|"-")?{decimais}
literal_float = {decimais}"."{decimais}?{exponentes}?|{decimais}{exponentes}|"."{decimais}{exponentes}?

literal_imaginario = ({decimais}|{literal_float})"i"

// Rune Literals (ENTENDER MELHOR)
OctalByteValue = "\\"{digito_octal}{digito_octal}{digito_octal}
ByteValue = {OctalByteValue} | {HexByteValue}
HexByteValue = "\\""x"{digito_hex}{digito_hex}
LittleUValue = "\\""u"{digito_hex}{digito_hex}{digito_hex}{digito_hex}
BigUValue = "\\""U"{digito_hex}{digito_hex}{digito_hex}{digito_hex}{digito_hex}{digito_hex}{digito_hex}{digito_hex}
EscapedChar = "\\"( "a" | "b" | "f" | "n" | "r" | "t" | "v" | "\\" | "'" | "\"")

UnicodeValue = {unicode_char} | {LittleUValue} | {BigUValue} | {EscapedChar}
RuneLit = "'" ( {UnicodeValue} | {ByteValue}) "'" 

// Regex capturando STRINGS
unicode_value_dash = {unicode_1} | {LittleUValue} | {BigUValue} | {EscapedChar}
raw_string_literal = "`"({unicode_2} | {new_line})* "`"
inteiro_string_literal = "\""({unicode_value_dash} | {ByteValue})* "\""
literal_string = {raw_string_literal} | {inteiro_string_literal}

// Comentarios e linhas em branco
WHITE = {new_line} | [ \t\f]
comment_line = "//"({unicode_char} | "|" | "^" | "*")*{new_line}?
block_comment = "/*" ([^*] | "*" + [^*/])* "*" + "/"

aux_invalido = {digito_decimal} | {letra_underline}
invalido = {aux_invalido}{aux_invalido}*

// IDENTIFIER
id = {letra_underline}({letra_underline} | {digito_unicode})*

%%

 /* Palavras chaves */

"go"                { return symbol(Sym.GO, "go"); }
"map"               { return symbol(Sym.MAP, "map"); }
"struct"            { return symbol(Sym.STRUCT, "struct"); }
"true"              { return symbol(Sym.TRUE, "true"); }
"false"             { return symbol(Sym.FALSE, "false"); }
"break"             { return symbol(Sym.BREAK, "break"); }
"default"           { return symbol(Sym.DEFAULT, "default"); }
"select"            { return symbol(Sym.SELECT, "select"); }
"const"             { return symbol(Sym.CONST, "const"); }
"fallthrough"       { return symbol(Sym.FALLTHROUGH, "fallthrough"); }
"case"              { return symbol(Sym.CASE, "case"); }
"defer"             { return symbol(Sym.DEFER, "defer"); }
"if"                { return symbol(Sym.IF, "if"); }
"range"             { return symbol(Sym.RANGE, "range"); }
"type"              { return symbol(Sym.TYPE, "type"); }
"goto"              { return symbol(Sym.GOTO, "goto"); }
"import"            { return symbol(Sym.IMPORT, "import"); }
"return"            { return symbol(Sym.RETURN, "return"); }
"var"               { return symbol(Sym.VAR, "var"); }
"func"              { return symbol(Sym.FUNC, "func"); }
"interface"         { return symbol(Sym.INTERFACE, "interface"); }
"package"           { return symbol(Sym.PACKAGE, "package"); }
"switch"            { return symbol(Sym.SWITCH, "switch"); }
"continue"          { return symbol(Sym.CONTINUE, "continue"); }
"chan"              { return symbol(Sym.CHAN, "chan"); }
"else"              { return symbol(Sym.ELSE, "else"); }
"for"               { return symbol(Sym.FOR, "for"); }


 /* Operadores relacionais e booleanos */

"%"                 { return symbol(Sym.MOD, "%"); }
"!"                 { return symbol(Sym.NOT, "!"); }
"-"                 { return symbol(Sym.MINUS, "-"); }
"|"                 { return symbol(Sym.OR, "|"); }
"!="                { return symbol(Sym.NOTEQ, "!="); }
"||"                { return symbol(Sym.OROR, "||"); }
"<-"                { return symbol(Sym.CHANNEL, "<-"); }
"<<="               { return symbol(Sym.LSHIFTEQ, "<<="); }
"="                 { return symbol(Sym.EQUAL, "="); }
":="                { return symbol(Sym.CHANNELEQ, ":="); }
"<"                 { return symbol(Sym.LESS, "<"); }
"+="                { return symbol(Sym.PLUSEQ, "+="); }
"*"                 { return symbol(Sym.MULT, "*"); }
"^"                 { return symbol(Sym.XOR, "^"); }
">"                 { return symbol(Sym.GREATER, ">"); }
">="                { return symbol(Sym.GTEQ, ">="); }
"<="                { return symbol(Sym.LTEQ, "<="); }
"&="                { return symbol(Sym.ANDEQ, "&="); }
"-="                { return symbol(Sym.MINUSEQ, "-="); }
"/"                 { return symbol(Sym.DIV, "/"); }
"+"                 { return symbol(Sym.PLUS, "+"); }
"<<"                { return symbol(Sym.LSHIFT, "<<"); }
"++"                { return symbol(Sym.PLUSPLUS, "++"); }
">>"                { return symbol(Sym.RSHIFT, ">>"); }
"--"                { return symbol(Sym.MINUSMINUS, "--"); }
"&^"                { return symbol(Sym.ANDNOT, "&^"); }
">>="               { return symbol(Sym.RSHIFTEQ, ">>="); }
"&"                 { return symbol(Sym.AND, "&"); }
"&&"                { return symbol(Sym.ANDAND, "&&"); }
"|="                { return symbol(Sym.OREQ, "|="); }
"*="                { return symbol(Sym.MULTEQ, "*="); }
"^="                { return symbol(Sym.XOREQ, "^="); }
"/="                { return symbol(Sym.DIVEQ, "/="); }
"&^="               { return symbol(Sym.ANDNOTEQ, "&^="); }
"=="                { return symbol(Sym.EQEQ, "=="); }

 /* Separadores */

","                 { return symbol(Sym.COMMA, ","); }
";"                 { return symbol(Sym.SEMICOLON, ";"); }
"..."			    { return symbol(Sym.SUSPOINTS,"..."); }
"."                 { return symbol(Sym.DOT, "."); }
":"                 { return symbol(Sym.COLON, ":"); }
"("                 { return symbol(Sym.LPAREN, "("); }
")"                 { return symbol(Sym.RPAREN, ")"); }
"["                 { return symbol(Sym.LBRACK, "["); }
"]"                 { return symbol(Sym.RBRACK, "]"); }
"{"                 { return symbol(Sym.LBRACE, "{"); }
"}"                 { return symbol(Sym.RBRACE, "}"); }

 /* NECESSARIO? ENTENDER MELHOR */
"?"                           { return symbol(Sym.INTERROGATION, "?"); }
"#"                           { return symbol(Sym.HASH, "#"); }
"??"                          { return symbol(Sym.DOUBLE_INTERROGATION, "??"); }

 /* COMENTARIOS */
{comment_line}         { ignore("Comentario em linha", yytext()); }
{block_comment}      { ignore("Comentario em bloco", yytext()); }

 /* TERMINAIS JA DEFINIDOS */
 
{WHITE} {} // Ignore espacos em branco

// Literais
{literal_inteiro}                      { return symbol(Sym.INT_LITERAL, yytext()); }
{literal_float}                    { return symbol(Sym.FLOAT_LITERAL, yytext()); }
{literal_imaginario}                { return symbol(Sym.IMG_LITERAL, yytext()); }
{RuneLit}                     { return symbol(Sym.RUNE_LITERAL, yytext()); }
{literal_string}                   { return symbol(Sym.STRING_LITERAL, yytext()); }
 
"_"                        { return symbol(Sym.BLANK_IDENTIFIER, "_"); }
{id}                  { return symbol(Sym.IDENTIFIER, yytext()); }

 /* ERROS */
{invalido}   { reportError(yytext(), yyline + 1, yycolumn + 1); }

[^]  { reportError(yytext(), yyline + 1, yycolumn + 1); }



package lexer;
import java_cup.runtime.Symbol;
import parser.sym;

%%

%public
%class Lexer
%unicode
%cup
%line
%column
%{
    private Symbol symbol(int type) {
        return new Symbol(type, yyline + 1, yycolumn + 1);
    }

    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline + 1, yycolumn + 1, value);
    }
%}
Letra   = [a-zA-Z]
Digito  = [0-9]
Id      = {Letra}({Letra}|{Digito})*
Nro     = {Digito}+
Espacio = [ \t\r\n]+
%%

"int"       { return symbol(sym.INT); }
"bool"      { return symbol(sym.BOOL); }
"void"      { return symbol(sym.VOID); }
"main"      { return symbol(sym.MAIN); }
"return"    { return symbol(sym.RETURN); }

"true"      { return symbol(sym.BOOL_CONST, Boolean.TRUE); }
"false"     { return symbol(sym.BOOL_CONST, Boolean.FALSE); }

"+"         { return symbol(sym.PLUS); }
"*"         { return symbol(sym.TIMES); }
"="         { return symbol(sym.ASSIGN); }
"("         { return symbol(sym.LPAREN); }
")"         { return symbol(sym.RPAREN); }
"{"         { return symbol(sym.LBRACE); }
"}"         { return symbol(sym.RBRACE); }
";"         { return symbol(sym.SEMI); }
{Nro}       { return symbol(sym.NRO, Integer.parseInt(yytext())); }
{Id}        { return symbol(sym.ID, yytext()); }
{Espacio}   { /* ignorar espacios en blanco */ }

[^]         { throw new RuntimeException("Error léxico en línea " + (yyline + 1) + ", col " + (yycolumn + 1) + ": '" + yytext() + "'"); }
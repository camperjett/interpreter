package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.craftinginterpreters.lox.TokenType.*;
public class Scanner {
    // final
    // https://stackoverflow.com/questions/15655012/how-does-the-final-keyword-in-java-work-i-can-still-modify-an-object
    // Rule If you have initialized a final variable, then you cannot change it to refer to a different object.
    // (In this case ArrayList)
    // final classes cannot be subclassed
    // final methods cannot be overridden. (This method is in superclass)
    // final methods can override. (Read this in grammatical way. This method is in a subclass)
    private final String source; // We store the raw source code as a simple string
    private final List<Token> tokens = new ArrayList<>();
    // The start field points to the first character in the lexeme being scanned,
    // and current points at the character currently being considered.
    private int start = 0;
    private int current = 0;
    private int line = 1;

    private static final Map<String, TokenType> keywords;
    // static block
    // https://stackoverflow.com/questions/2943556/static-block-in-java#:~:text=Static%20block%20can%20be%20used,run%20without%20main%20function%20also.&text=A%20static%20block%20executes%20once,executes%20before%20the%20main%20method.
    static {
        keywords = new HashMap<>();
        keywords.put("and", AND);
        keywords.put("class", CLASS);
        keywords.put("else",   ELSE);
        keywords.put("false",  FALSE);
        keywords.put("for",    FOR);
        keywords.put("fun",    FUN);
        keywords.put("if",     IF);
        keywords.put("nil",    NIL);
        keywords.put("or",     OR);
        keywords.put("print",  PRINT);
        keywords.put("return", RETURN);
        keywords.put("super",  SUPER);
        keywords.put("this",   THIS);
        keywords.put("true",   TRUE);
        keywords.put("var",    VAR);
        keywords.put("while",  WHILE);
    }
    Scanner(String source){
        this.source = source;
    }
    private boolean isAtEnd(){
        return current >= source.length();
    }
    public List<Token> scanTokens() {
        while(!isAtEnd()){
            start = current;
            scanToken();
        }
        // one final “end of file” token. That isn’t strictly needed,
        // but it makes our parser a little cleaner.
        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }
    private void scanToken(){
        char c = advance();
        switch (c) {
            case '(' -> addToken(LEFT_PAREN);
            case ')' -> addToken(RIGHT_PAREN);
            case '{' -> addToken(LEFT_BRACE);
            case '}' -> addToken(RIGHT_BRACE);
            case ',' -> addToken(COMMA);
            case '.' -> addToken(DOT);
            case '-' -> addToken(MINUS);
            case '+' -> addToken(PLUS);
            case ';' -> addToken(SEMICOLON);
            case '*' -> addToken(STAR);
            case '!' -> addToken(match('=') ? BANG_EQUAL : BANG);
            case '=' -> addToken(match('=') ? EQUAL_EQUAL : EQUAL);
            case '<' -> addToken(match('=') ? LESS_EQUAL : LESS);
            case '>' -> addToken(match('=') ? GREATER_EQUAL : GREATER);
            case '/' -> {
                if(match('/')){
                    // case of comment. keep advancing until EOL
                    while(!isAtEnd() && peek() != '\n') advance();
                }
                else if(match('*')){
                    blockComment();
                }
                else addToken(SLASH);
            }
            case ' ', '\t' -> {}
            case '\n' -> line++;
            case '"' -> string();
            default -> {
                if(isDigit(c)){
                    number();
                } else if(isAlpha(c)){
                    identifier();
                } else {
                    Lox.error(line, "Unexpected character.");
                }
            }
        }
    }

    private void blockComment() {
        // case of block comment, scan until "*/" is found
        while(!isAtEnd() && !endOfBlockComment()) {
            advance();
        }
        if(isAtEnd()) Lox.error(line, "Unterminated block comment");
        else{
            advance(); // skip '*'
            advance(); // skip '/'
        }
    }

    private boolean endOfBlockComment() {
        if(peek() != '*') return false;
        else return peekNext() == '/';
    }

    private void identifier() {
        while(isAlphaNumeric(peek())) advance();
        // we check if the lexeme scanned was a reserved word
        String lexeme = source.substring(start, current);
        TokenType type = keywords.get(lexeme);
        if(type == null) type = IDENTIFIER;
        addToken(type);
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isAlpha(char c) {
        return c == '_' || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private void number() {
//        while a dig keep advancing
        while(isDigit(peek())) advance();
//        if we now see a '.' then continue ...
        if(peek() == '.' && isDigit(peekNext())) {
            advance(); // consume the '.'
            while(isDigit(peek())) advance();
        }
        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
//        what if 923.sqrt()? is allowed? or only 923.34? this is not of concern during scanning
//        @TODO we check that during semantic analysis (or syntax analysis?)

    }

    private char peekNext() {
        if(current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private void string() {
        while(!isAtEnd() && peek() != '"'){
            if(peek() == '\n') line++;
            advance();
        }
        if(isAtEnd()){
            Lox.error(line, "Unterminated String.");
            return;
        }
        advance(); // skip over '"'
        String value = source.substring(start+1, current-1); // trim both the '"'
        addToken(STRING, value);
    }

    private char peek() {
        if(isAtEnd())
            //  https://stackoverflow.com/questions/14461695/what-does-0-stand-for#:~:text=To%20the%20C%20language%2C%20'%5C,particular%20zero%20as%20a%20character.
            //  The character has much more significance in C, and it serves as a reserved character used to signify the
            //  end of a string
            return '\0';
        else return source.charAt(current);
    }

    private boolean match(char expected) {
        if(isAtEnd()) return false;
        else if(source.charAt(current) != expected) return false;
        current++;
        return true;
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String lexeme = source.substring(start, current);
        tokens.add(new Token(type, lexeme, literal, line));
    }


    private char advance() {
        return source.charAt(current++);
    }

}

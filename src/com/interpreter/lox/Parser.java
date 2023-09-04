package com.interpreter.lox;

import java.util.List;

import static com.interpreter.lox.TokenType.*;

public class Parser {
    /**
     * expression     → comma | expression "?" expression ":" expression;
     * comma          → equality ( "," equality )*;
     * equality       → comparison ( ( "!=" | "==" ) comparison )* ;
     * comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
     * term           → factor ( ( "-" | "+" ) factor )* ;
     * factor         → unary ( ( "/" | "*" ) unary )* ;
     * unary          → ( "!" | "-" ) unary
     *                | primary ;
     * primary        → NUMBER | STRING | "true" | "false" | "nil"
     *                | "(" expression ")";
     * */

    private static class ParseError extends RuntimeException{

    }
    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens){
        this.tokens = tokens;
    }

    Expr parse(){
        try{
            return expression();
        } catch (ParseError error){
            /*
             * Syntax error recovery is the parser’s job, so we don’t want
             * the ParseError exception to escape into the rest of the interpreter.
             * */
            return null;
        }
    }

    private Expr expression(){
        Expr expr = comma();
        if(match(QUERY)){
            Expr expr2 = expression();
            consume(COLON, "Expect : after expression");
            Expr expr3 = expression();
            expr = new Expr.Ternary(expr, expr2, expr3);
        }
        return expr;
    }

    private Expr comma() {
        Expr expr = equality();
        while(match(COMMA)){
            Token Operator = previous();
            Expr right = equality();
            expr = new Expr.Binary(expr, Operator, right);
        }
        return expr;
    }


    private Expr equality() {
        Expr expr = comparison();
        while(match(BANG_EQUAL, EQUAL_EQUAL)){
            Token Operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, Operator, right);
        }
        return expr;
    }

    private Expr comparison() {
        Expr expr = term();
        while(match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)){
            Token Operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, Operator, right);
        }
        return expr;
    }

    private Expr term() {
        Expr expr = factor();
        while(match(MINUS, PLUS)){
            Token Operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, Operator, right);
        }
        return expr;
    }

    private Expr factor() {
        Expr expr = unary();
        while(match(SLASH, STAR)){
            Token Operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, Operator, right);
        }
        return expr;
    }

    private Expr unary() {
        if(match(BANG, MINUS)){
            Token Operator = previous();
            Expr right = unary();
            return new Expr.Unary(Operator, right);
        }
        return primary();
    }

    private Expr primary() {
        if(match(FALSE)) return new Expr.Literal(false);
        if(match(TRUE)) return new Expr.Literal(true);
        if(match(NIL)) return new Expr.Literal(null);
        if(match(NUMBER, STRING)) return new Expr.Literal(previous().literal);
        if(match(LEFT_PAREN)) {
            Expr expr = expression();
            // error handler called for checking RIGHT_PAREN
            consume(RIGHT_PAREN, "Expect ')' after expression");
            return new Expr.Grouping(expr);

        }
        throw error(peek(), "Expect expression.");
    }

    //  error handler called for checking presence of an expected token
    private Token consume(TokenType type, String message) {
        if(check(type)) return advance();
        throw error(peek(), message);
    }
    /*
    The error() method returns the error instead of throwing it because
    we want to let the calling method inside the parser decide whether
    to unwind or not. Some parse errors occur in places where the parser
    isn’t likely to get into a weird state, and we don’t need to synchronize.
    In those places, we simply report the error and keep on truckin’.
    **/
    private ParseError error(Token token, String message) {
        Lox.error(token, message);
        return new ParseError();
    }

    /**
     * Method used to synchronize a recursive descent parser:
     * We want to discard tokens until we’re right at the beginning of the
     * next statement. After a semicolon, we’re probably finished
     * with a statement. Most statements start with a keyword—for, if, return,
     * var, etc. When the next token is any of those, we’re probably about
     * to start a statement.
     * */
    private void synchronize(){
        advance(); // advance the current token and check it as previous();
        while(!isAtEnd()){
            if(previous().type == SEMICOLON) return;
            switch (peek().type){
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }
            advance();
        }

    }

    private Token previous() {
        // @TODO may give error -- (when current is 0);
        return tokens.get(current-1);
    }

    private boolean match(TokenType... types) {
        for(TokenType type: types){
            if(check(type)){
                advance();
                return true;
            }
        }
        return false;
    }

    private Token advance() {
        if(!isAtEnd()) current++;
        return previous();
    }

    private boolean check(TokenType type) {
        if(isAtEnd()) return false;
        return peek().type == type;
    }

    private Token peek() {
        // @TODO may give exception if current is at EOF
        return tokens.get(this.current);
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }
}

package com.interpreter.lox;
/*
* package-private
* Default. When we don't use any keyword explicitly, Java will set a
* default access to a given class, method or property. The default access
*  modifier is also called package-private, which means that all members
*  are visible within the same package but aren't accessible from other
*  packages: package com.
* */
class Token {
    final String lexeme;
    final int line;
    final TokenType type;
    // literal used for exact value of the token. like for STRING, we store its value here (same for NUMBER)
    final Object literal; // not sure why is this?
    Token (TokenType type, String lexeme, Object literal, int line){
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }
    public String toString(){
        if(literal == null) return type + " " + lexeme;
        return type + " " + lexeme + " " + literal;
    }

}
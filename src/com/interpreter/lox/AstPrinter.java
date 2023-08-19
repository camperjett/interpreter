package com.interpreter.lox;

public class AstPrinter implements Expr.Visitor<String>{

    String print(Expr expr){
        return expr.accept(this);
    }

    @Override
    public String visitBinary(Expr.Binary expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitGrouping(Expr.Grouping expr) {
        return parenthesize("group", expr.expression);
    }

    @Override
    public String visitTernary(Expr.Ternary expr){
        return parenthesize("ternary", expr.expr1, expr.expr2, expr.expr3);
    }

    @Override
    public String visitUnary(Expr.Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
    }

    @Override
    public String visitLiteral(Expr.Literal expr) {
        return expr.value == null ? "nil" : expr.value.toString();
    }

    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(name);
        for(Expr expr : exprs){
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");
        return builder.toString();
    }
    public static void main(String[] args){
        // test expression
        Expr expression = new Expr.Binary(new Expr.Unary(
                new Token(TokenType.MINUS, "-", null, 1),
                new Expr.Literal(123)),
                new Token(TokenType.STAR, "*", null, 1),
                new Expr.Grouping(
                        new Expr.Literal(45.67)));
        // two ways to invoke:
        // first is to define a function in AstPrinter to do it
        System.out.println(new AstPrinter().print(expression));
        // second is to call accept() from expression
        //  System.out.println(expression.accept(new AstPrinter()));

    }

}

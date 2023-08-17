package com.interpreter.lox;

public class ReversePolish implements Expr.Visitor<String> {
    @Override
    public String visitBinary(Expr.Binary binary) {
        return util(binary.left) + " " + util(binary.right) + " "  + binary.operator.lexeme;
    }

    private String util(Expr expression) {
        return expression.accept(this);
    }

    @Override
    public String visitGrouping(Expr.Grouping grouping) {
        return util(grouping.expression) + " " + "group";
    }

    @Override
    public String visitUnary(Expr.Unary unary) {
        return util(unary.right) + " " + unary.operator;
    }

    @Override
    public String visitLiteral(Expr.Literal literal) {
        return literal.value == null ? "nil" : literal.value.toString();
    }

    public static void main(String[] args){
//        Expr expression = new Expr.Binary(new Expr.Grouping())
    }
}

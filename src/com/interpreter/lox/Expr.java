package com.interpreter.lox;

abstract class Expr {
    interface Visitor<R> {
        R visitBinary(Binary binary);
        R visitGrouping(Grouping grouping);
        R visitUnary(Unary unary);
        R visitLiteral(Literal literal);
    }
    static class Binary extends Expr {
        Binary (Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }
        @Override
        <R> R accept(Visitor<R> visitor){
            return visitor.visitBinary(this);
        }
        final Expr left;
        final Token operator;
        final Expr right;
    }

    static class Grouping extends Expr {
        Grouping (Expr expression) {
            this.expression = expression;
        }
        @Override
        <R> R accept(Visitor<R> visitor){
            return visitor.visitGrouping(this);
        }
        final Expr expression;
    }

    static class Unary extends Expr {
        Unary (Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }
        @Override
        <R> R accept(Visitor<R> visitor){
            return visitor.visitUnary(this);
        }
        final Token operator;
        final Expr right;
    }

    static class Literal extends Expr {
        Literal (Object value) {
            this.value = value;
        }
        @Override
        <R> R accept(Visitor<R> visitor){
            return visitor.visitLiteral(this);
        }
        final Object value;
    }


    abstract <R> R accept(Visitor<R> visitor);
}

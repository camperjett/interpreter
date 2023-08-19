package com.interpreter.lox;

abstract class Expr {
    interface Visitor<R> {
        R visitBinary(Binary binary);
        R visitGrouping(Grouping grouping);
        R visitTernary(Ternary ternary);
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
    static class Ternary extends Expr {
        Ternary(Expr expr1, Expr expr2, Expr expr3){
            this.expr1 = expr1;
            this.expr2 = expr2;
            this.expr3 = expr3;
        }
        @Override
        <R> R accept(Visitor<R> visitor){ return visitor.visitTernary(this); }
        final Expr expr1;
        final Expr expr2;
        final Expr expr3;
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

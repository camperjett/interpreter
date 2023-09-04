package com.interpreter.lox;

public class Interpreter implements Expr.Visitor<Object>{

    void interpret(Expr expression){
        try {
            Object value = evaluate(expression);
            System.out.println(stringify(value));
        } catch (RuntimeError error){
            Lox.runtimeError(error);
        }
    }

    @Override
    public Object visitLiteral(Expr.Literal expr){
        return expr.value;
    }

    @Override
    public Object visitUnary(Expr.Unary expr) {
        Object right = evaluate(expr.right);
        switch (expr.operator.type){
            case MINUS:
                checkNumberOperand(expr.operator, right);
                return -(double)right;
            case BANG:
                return !isTruthy(right);
        }

        // Unreachable
        return null;
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if(operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    // @TODO: What if obj is a number 0? shouldn't it return false?

    private boolean isTruthy(Object obj) {
        if (obj == null) return false;
        else if(obj instanceof Boolean) return (boolean) obj;
        else return true;
    }
    @Override
    public Object visitBinary(Expr.Binary expr) {
        // first evaluate both operands, we evaluate the operands in left-to-right order
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);
        // now, evaluate the binary operation!
        switch (expr.operator.type){
            case GREATER:
                checkNumberOperands(expr.operator, left, right);
                return (double) left > (double) right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double) left >= (double) right;
            case LESS:
                checkNumberOperands(expr.operator, left, right);
                return (double) left < (double) right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double) left <= (double) right;
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
            case PLUS:
                if(left instanceof Double && right instanceof Double)
                    //  add if both number
                    return (double)left + (double) right;
                else if(left instanceof String && right instanceof String)
                    //  concatenate if strings
                    return (String)left + (String)right;
                throw new RuntimeError(expr.operator, "Operands must be either two numbers or two strings.");
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                return (double)right - (double) left;
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return (double) left * (double) right;
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                return (double) left / (double) right;
        }
        // unreachable
        return null;
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if(left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator, "Both operands must be numbers.");
    }

    /**
     *  the equality operators support operands of any type, even mixed ones.
     *  You can’t ask Lox if 3 is less than "three", but you can ask if it’s equal to it.
     * */
    private boolean isEqual(Object a, Object b) {
        if(a == null && b == null) return true;
        if(a == null) return false;
        return a.equals(b);
    }

    private String stringify(Object object) {
        if(object == null) return "nil";
        if(object instanceof Double){
            String text = object.toString();
            if(text.endsWith(".0")){
                text = text.substring(0, text.length()-2);
                return text;
            }
        }
        return object.toString();
    }

    @Override
    public Object visitGrouping(Expr.Grouping expr){
        return evaluate(expr.expression);
    }

    @Override
    public Object visitTernary(Expr.Ternary ternary) {
        return null;
    }


    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

}

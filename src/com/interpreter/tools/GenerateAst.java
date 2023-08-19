package com.interpreter.tools;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

//  What?
//  This file is a script used to generate a .java file containing baseClass along
//  with some subclasses.
//  Why?
//  I needed to create lots of subclasses for each production rule inside Expr class.
//  Instead, I automated that by creating a script!

/***
 * Generates like this:
 *  abstract class Expr {
 *     static class Binary extends Expr {
 *         Binary (Expr left, Token operator, Expr right) {
 *             this.left = left;
 *             this.operator = operator;
 *             this.right = right;
 *         }
 *         final Expr left;
 *         final Token operator;
 *         final Expr right;
 *     }
 *      ...
 *   }
 * */

public class GenerateAst {
    public static void main(String [] args) throws IOException{
        if(args.length != 1) {
            System.err.println("Usage: generate_ast <output directory>");
            System.exit(64);
        }
        String outputDir = args[0];
        defineAst(outputDir, "Expr", Arrays.asList(
                "Binary     :   Expr left, Token operator, Expr right",
                "Grouping   :   Expr expression",
                "Ternary    :   Expr expr1, Expr expr2, Expr expr3",
                "Unary      :   Token operator, Expr right",
                "Literal    :   Object value"
        ));
    }

    private static void defineAst(String outputDir, String baseName, List<String> types) throws IOException {
        //  create a file
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        //  add boiler code for declaring baseName class
        writer.println("package com.interpreter.lox;");
        writer.println();
        //  add some necessary imports
        writer.println("abstract class " + baseName + " {");

        //  add interface for visitor
        //  https://craftinginterpreters.com/representing-code.html#visitors-for-expressions
        defineVisitor(writer, baseName, types);

        //  for each type in types, create a subclass
        //  The AST classes.
        for(String type : types){
            String subClassName = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            // in each subclass, add a constructor along with variables
            defineType(writer, baseName, subClassName, fields);
        }
        writer.println();
        writer.println("    abstract <R> R accept(Visitor<R> visitor);");
        //  close the class
        writer.println("}");
        writer.close();
    }

    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
        writer.println("    interface Visitor<R> {");
        //  for each type, we create visit Method
        for(String type : types){
            String subClassName = type.split(":")[0].trim();
            writer.println("        R visit" + subClassName + "(" + subClassName + " " + subClassName.toLowerCase() + ");");
        }
        writer.println("    }");
    }

    private static void defineType(PrintWriter writer, String baseName, String subClassName, String fieldList) {
        writer.println("    static class " + subClassName + " extends " + baseName + " {");
        //  constructor
        writer.println("        " + subClassName + " (" + fieldList + ") {");
        String[] fields = fieldList.split(", ");
        for(String field : fields){
            String name = field.split(" ")[1];
            writer.println("            this." + name + " = " + name + ";");
        }
        writer.println("        }");
        writer.println("        @Override");
        writer.println("        <R> R accept(Visitor<R> visitor){");
        writer.println("            return visitor.visit" + subClassName + "(this);");
        writer.println("        }");

        //  fields
        for(String field : fields){
            writer.println("        final " + field + ";");
        }
        writer.println("    }");
        writer.println();

    }
}

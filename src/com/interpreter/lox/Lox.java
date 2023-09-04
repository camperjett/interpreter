package com.interpreter.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
    // We make the field static so that successive calls to run() inside a
    // REPL session reuse the same interpreter. The interpreter stores global
    // variables. Those variables should persist throughout the REPL session.
    private static final Interpreter interpreter = new Interpreter();
    static boolean hadError = false;
    private static boolean hadRuntimeError = false;

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        } else if (args.length == 1) { // file mode
            runFile(args[0]);
        } else { // interpreter mode
            runPrompt();
        }
    }
    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        // Indicate an error in the exit code.
        if (hadError) System.exit(65);
        if(hadRuntimeError) System.exit(70);
    }
    private static void runPrompt() throws IOException {
        InputStreamReader input  = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for(;;){
            System.out.println("> ");
            String line = reader.readLine();
            if (line == null) break;
            run(line);
        }
    }
    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);
        Expr expression = parser.parse();
        //  Stop if there was a syntax error;
        if(hadError) return;
        interpreter.interpret(expression);
//        System.out.println(new AstPrinter().print(expression));
    }
    public static void printTokens(List<Token> tokens){
        for(Token token : tokens){
            System.out.println(token.toString());
        }
    }
    static void error(Token token, String message){
        if(token.type == TokenType.EOF) report(token.line, " at end", message);
        else report(token.line, " at '" + token.lexeme + "'", message);
    }
    static void error(int line, String message) {
        report(line, "", message);
    }
    private static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
         hadError = true;
    }

    public static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() + "\n[line " + error.token.line + "]");
        hadRuntimeError = true;
    }
}
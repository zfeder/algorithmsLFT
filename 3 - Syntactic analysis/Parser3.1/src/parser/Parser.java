package parser;

import java.io.*;

public class Parser {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Parser(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

    void move() {
        look = lex.lexical_scan(pbr); // Si muove token per token
        System.out.println("token = " + look);  // Stampa il token
    }

    void error(String s) {
        throw new Error("near line " + lex.line + ": " + s);
    } // Errore

    void match(int t) {
        if (look.tag == t) {
            if (look.tag != Tag.EOF) move();
        } else error("syntax error");
    }

    public void start() {
        switch (look.tag) {
            case '(', Tag.NUM:
                expr();
                match(Tag.EOF);
                break;
            default:
                error("Start error");
        }
    }

    private void expr() {
        switch (look.tag) {
            case '(', Tag.NUM:
                term();
                exprp();
                break;
            default:
                error("expr error");
        }
    }

    private void exprp() {
        switch (look.tag) {
            case '+':
                match('+');
                term();
                exprp();
                break;
            case '-':
                match('-');
                term();
                exprp();
                break;
            case ')', Tag.EOF:
                break;
            default:
                error("error exprp");
        }
    }

    private void term() {
        switch (look.tag){
            case '(', Tag.NUM:
                fact();
                termp();
                break;
            default:
                error("error term");
        }
    }

    private void termp() {
        switch (look.tag) {
            case '/':
                match('/');
                fact();
                termp();
                break;
            case '*':
                match('*');
                fact();
                termp();
                break;
            case '+', '-', '(', Tag.EOF:
                break;
        }
    }

    private void fact() {
        switch (look.tag){
            case '(':
                match('(');
                expr();
                match(')');
                break;
            case Tag.NUM:
                match(Tag.NUM);
                break;
            default:
                error("fact error");
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "C:\\Users\\feder\\Desktop\\Laboratorio LFT\\3 - Analisi Sintattica\\Parser3.1\\src\\parser\\input.txt";
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser parser = new Parser(lex, br);
            parser.start();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}

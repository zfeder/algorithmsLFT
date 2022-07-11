package parser;

import java.awt.*;
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
        look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }

    void error(String s) {
        throw new Error("near line " + lex.line + ": " + s);
    }

    void match(int t) {
        //System.out.println(look.tag + " t=" + t);
        if (look.tag == t) {
            if (look.tag != Tag.EOF) {
                move();
            }
        } else error("syntax error");
    }

    public void prog() {
        switch (look.tag) {
            case Tag.ASSIGN, Tag.PRINT, Tag.READ, Tag.WHILE, Tag.IF, Tag.TO, '{':
                statlist();
                match(Tag.EOF);
                break;
            default:
                error("prog error");
        }
    }

    private void statlistp() {
        switch (look.tag) {
            case ';':
                match(';');
                stat();
                statlistp();
                break;
            case Tag.EOF, '}':
                break;
            default:
                error("statlistp error");
        }
    }

    private void statlist() {
        switch (look.tag) {
            case Tag.ASSIGN, Tag.PRINT, Tag.READ, Tag.WHILE, Tag.IF, Tag.TO, '{':
                stat();
                statlistp();
                break;
            default:
                error("statlist error");
        }
    }

    private void stat() {
        switch (look.tag) {
            case Tag.ASSIGN:
                match(Tag.ASSIGN);
                expr();
                match(Tag.TO);
                idlist();
                break;
            case Tag.PRINT:
                match(Tag.PRINT);
                match('(');
                exprlist();
                match(')');
                break;
            case Tag.READ:
                match(Tag.READ);
                match('(');
                idlist();
                match(')');
                break;
            case Tag.WHILE:
                match(Tag.WHILE);
                match('(');
                bexpr();
                match(')');
                stat();
                break;
            case Tag.IF:
                match(Tag.IF);
                match('(');
                bexpr();
                match(')');
                stat();
                if(look.tag == Tag.END) {
                    match(Tag.END);
                }
                else if(look.tag == Tag.ELSE) {
                    match(Tag.ELSE);
                    stat();
                    match(Tag.END);
                }
                break;
            case '{':
                match('{');
                statlist();
                match('}');
                break;
            default:
                error("stat error");
        }
    }

    private void idlist() {
        switch (look.tag) {
            case Tag.ID:
                match(Tag.ID);
                idlistp();
                break;
            default:
                error("idlist error");
        }
    }

    private void idlistp() {
        switch (look.tag) {
            case ',':
                match(',');
                match(Tag.ID);
                idlistp();
                break;
            case Tag.EOF,';',')',Tag.END,Tag.ELSE,'}':
                break;
            default:
                error("idlistp error");
            }
    }

    private void bexpr() {
        switch (look.tag) {
            case Tag.RELOP:
                match(Tag.RELOP);
                expr();
                expr();
                break;
            default:
                error("bexpr error");
        }
    }

    private void expr() {
        switch (look.tag) {
            case '+':
                match('+');
                match('(');
                exprlist();
                match(')');
                break;
            case '-':
                match('-');
                expr();
                expr();
                break;
            case '*':
                match('*');
                match('(');
                exprlist();
                match(')');
                break;
            case '/':
                match('/');
                expr();
                expr();
                break;
            case Tag.NUM:
                match(Tag.NUM);
                break;
            case Tag.ID:
                match(Tag.ID);
                break;
            default:
                error("expr error");
        }
    }

        private void exprlist() {
            switch (look.tag) {
                case '+', '-', '*', '/', Tag.NUM, Tag.ID:
                    expr();
                    exprlistp();
                    break;
                default:
                    error("exprlist error");
            }
        }

        private void exprlistp() {
            switch (look.tag) {
                case ',':
                    match(',');
                    expr();
                    exprlistp();
                    break;
                case ')':
                    break;
                default:
                    error("exprlistp error");
            }
        }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "C:\\Users\\feder\\Desktop\\Laboratorio LFT\\3 - Analisi Sintattica\\Parser3.2\\src\\parser\\input.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser parser = new Parser(lex, br);
            parser.prog();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}

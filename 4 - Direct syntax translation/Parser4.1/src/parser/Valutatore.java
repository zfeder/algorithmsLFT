package parser;

import java.io.*;

public class Valutatore {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Valutatore(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

    void move() {
        // come in Esercizio 3.1
        look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }

    void error(String s) {
        // come in Esercizio 3.1
        throw new Error("near line " + lex.line + ": " + s);

    }

    void match(int t) {
        // come in Esercizio 3.1
        if (look.tag == t) {
            if (look.tag != Tag.EOF) move();
        } else error("syntax error");

    }

    public void start() {
        int expr_val;

        // ... completare ...
        if(look==Token.lpt || look.tag==Tag.NUM){
            expr_val = expr();
            match(Tag.EOF);
            System.out.println("Result: " + expr_val);
        }else{
            error("start error");
        }
    }


    private int expr() {
        int term_val, exprp_val=0;

        // ... completare ...
        if(look==Token.lpt || look.tag==Tag.NUM){
            term_val = term();
            exprp_val = exprp(term_val);
        }else{
            error("expr error");
        }
        // ... completare ...
        return exprp_val;
    }

    private int exprp(int exprp_i) {
        int term_val, exprp_val=0;
        switch (look.tag) {
            case '+':
                match('+');
                term_val = term();
                exprp_val = exprp(exprp_i + term_val);
                break;
            case '-':
                match('-');
                term_val = term();
                exprp_val = exprp(exprp_i - term_val);
                break;
            case Tag.EOF, ')':
                match(Tag.EOF);
                exprp_val = exprp_i;
                break;
            default:
                error("exprp error");
            // ... completare ...
        }
        return exprp_val;
    }

    private int term() {
        int fact_val, termp_val=0;
        if(look==Token.lpt || look.tag == Tag.NUM){
            fact_val = fact();
            termp_val = termp(fact_val);
        }else{
            error("term error");
        }
        return termp_val;
    }

    private int termp(int termp_i) {
        int termp_val=0, fact_val;
        switch (look.tag) {
            case '*':
                match('*');
                fact_val = fact();
                termp_val = termp(termp_i * fact_val);
                break;
            case '/':
                match('/');
                fact_val = fact();
                termp_val = termp(termp_i / fact_val);
                break;
            case Tag.EOF,')','+','-':
                termp_val = termp_i;
                break;
            default:
                error("termp error");
                // ... completare ...
        }
        return termp_val;
    }

    private int fact() {
        int fact_val=0;
        switch (look.tag) {
            case '(':
                match('(');
                fact_val = expr();
                match(')');
                break;
            case Tag.NUM:
                fact_val = ((NumberTok)look).n;
                match(Tag.NUM);
                break;
            default:
                error("fact error");

        }
        // ... completare ...
        return fact_val;
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "C:\\Users\\yasya\\OneDrive\\Desktop\\Laboratorio LFT\\4 - Traduzione diretta della sintassi\\Parser4.1\\src\\parser\\prova.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Valutatore valutatore = new Valutatore(lex, br);
            valutatore.start();
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}
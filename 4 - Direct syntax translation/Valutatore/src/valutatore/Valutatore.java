package valutatore;

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

    // Valori degli attributi ereditati in input
    // Funzione restituisce i valori degli attributi sintetizzati
    // val = attributo sintetizzato
    // i = attributo ereditato
    public void start() {
        int expr_val;
        switch (look.tag) {
            case '(', Tag.NUM:
                expr_val = expr();
                match(Tag.EOF);
                System.out.println("Result: " + expr_val);
                break;
            default:
                error("Start error");
        }
    }


    private int expr() {
        int term_val, exprp_val=0;
        switch (look.tag) {
            case '(', Tag.NUM:
                term_val = term();
                exprp_val = exprp(term_val);
                break;
            default:
                error("expr error");
        }
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
            default:
                exprp_val = exprp_i;
                break;
            // ... completare ...
        }
        return exprp_val;
    }

    private int term() {
        int fact_val, termp_val=0;
        switch (look.tag){
            case '(', Tag.NUM:
                fact_val = fact();
                termp_val = termp(fact_val);
                break;
            default:
                error("error term");
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
            default:
                termp_val = termp_i;
                break;
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
        String path = "C:\\Users\\feder\\Desktop\\Laboratorio LFT\\4 - Traduzione diretta della sintassi\\Valutatore\\src\\valutatore\\input.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Valutatore valutatore = new Valutatore(lex, br);
            valutatore.start();
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}

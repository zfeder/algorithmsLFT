package lexer;

import lexer.CodeGenerator;
import lexer.OpCode;
import lexer.SymbolTable;

import java.io.*;
import java.util.LinkedList;
import java.util.List;


// javac Translator.java
// java Translator
// java -jar jasmin.jar Output.j
// java Output
import java.io.*;

public class Translator {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    SymbolTable st = new SymbolTable();
    CodeGenerator code = new CodeGenerator();
    int count=0;

    public Translator(Lexer l, BufferedReader br) {
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
                try {
                    code.toJasmin();
                }
                catch(java.io.IOException e) {
                    System.out.println("IO error\n");
                };
                break;
            default:
                error("prog error");
        }
    }

    private void statlist() {
        switch (look.tag) {
            case Tag.ASSIGN, Tag.PRINT, Tag.READ, Tag.WHILE, Tag.IF, Tag.TO, '{':
                stat();
                statlistp();
                break;
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

    public void stat() {
        switch (look.tag) {
            case Tag.ASSIGN:
                match(Tag.ASSIGN);
                expr();
                match(Tag.TO);
                idlist(Tag.ASSIGN);
                break;
            case Tag.PRINT:
                match(Tag.PRINT);
                match('(');
                exprlist(new Instruction(OpCode.invokestatic, 1));
                match(')');
                break;
            case Tag.READ:
                match(Tag.READ);
                match('(');
                idlist(Tag.READ);
                match(')');
                break;
            case Tag.WHILE:

                match(Tag.WHILE);
                match('(');
                int label = code.newLabel();
                int EndLabel = code.newLabel();
                int nexLabel = code.newLabel();
                code.emitLabel(nexLabel);
                bexpr(label, EndLabel);
                match(')');
                code.emitLabel(label);
                stat();
                code.emit(OpCode.GOto, nexLabel);
                code.emitLabel(EndLabel);
                break;
            case Tag.IF:
                match(Tag.IF);
                match('(');
                int iFlabel = code.newLabel();
                int iFEndLabel = code.newLabel();
                int iFnexLabel = code.newLabel();
                bexpr(iFlabel, iFnexLabel);
                match(')');
                code.emitLabel(iFlabel);
                stat();
                code.emit(OpCode.GOto, iFEndLabel);
                statp(iFEndLabel, iFnexLabel);
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

    private void statp(int iFEndLabel, int iFnexLabel) {
        switch (look.tag) {
            case Tag.END:
                code.emitLabel(iFEndLabel);
                match(Tag.END);
                break;

            case Tag.ELSE:
                code.emitLabel(iFnexLabel);
                match(Tag.ELSE);
                stat();
                code.emit(OpCode.GOto, iFEndLabel);
                match(Tag.END);
                code.emitLabel(iFEndLabel);

        }
    }


    private void idlist(int check) {
        switch(look.tag) {
            case Tag.ID:
                int id_addr = st.lookupAddress(((Word)look).lexeme);
                if (id_addr==-1) {
                    id_addr = count;
                    st.insert(((Word)look).lexeme,count++);
                }
                match(Tag.ID);
                if (Tag.READ == check) {
                    code.emit(OpCode.invokestatic, 0);
                    code.emit(OpCode.istore, id_addr);
                }
                if (Tag.ASSIGN == check) {
                    code.emit(OpCode.dup);
                    code.emit(OpCode.istore, id_addr);
                }
                idlistp(check);

        }
    }

    private void idlistp(int check) {
        switch (look.tag) {
            case ',':
                match(',');
                match(Tag.ID);
                idlist(check);
                break;
            default:
                break;
        }
    }

    private void bexpr(int bexTrue, int bexNext) {
        switch (look.tag) {
            case Tag.RELOP:
                String bex = ((Word) look).lexeme;
                match(Tag.RELOP);
                expr();
                expr();
                switch (bex) {
                    case ">":
                        code.emit(OpCode.if_icmpgt, bexTrue);
                        break;
                    case "<":
                        code.emit(OpCode.if_icmplt, bexTrue);
                        break;
                    case "==":
                        code.emit(OpCode.if_icmpeq, bexTrue);
                        break;
                    case ">=":
                        code.emit(OpCode.if_icmpge, bexTrue);
                        break;
                    case "<=":
                        code.emit(OpCode.if_icmple, bexTrue);
                        break;
                    case "<>":
                        code.emit(OpCode.if_icmpne, bexTrue);
                        break;
                }
                code.emit(OpCode.GOto, bexNext);
                break;

        }
    }


    private void expr() {
        switch (look.tag) {
            case '+':
                match('+');
                match('(');
                exprlist(new Instruction(OpCode.iadd, 1));
                match(')');
                break;
            case '-':
                match('-');
                expr();
                expr();
                code.emit(OpCode.isub);
                break;
            case '*':
                match('*');
                match('(');
                exprlist(new Instruction(OpCode.imul, 1));
                match(')');
                break;
            case '/':
                match('/');
                expr();
                expr();
                code.emit(OpCode.idiv);
                break;
            case Tag.NUM:
                code.emit(OpCode.ldc, ((NumberTok) look).n);
                match(Tag.NUM);
                break;
            case Tag.ID:
                code.emit(OpCode.iload, st.lookupAddress(((Word) look).lexeme));
                match(Tag.ID);
                break;
            default:
                error("expr error");
        }
    }

    private void exprlist(Instruction instruction) {
        switch (look.tag) {
            case '+', '-', '*', '/', Tag.NUM, Tag.ID:
                expr();
                code.emit(instruction);
                exprlistp(instruction);
                break;
            default:
                error("exprlist error");
        }
    }

    private void exprlistp(Instruction instruction) {
        switch (look.tag) {
            case ',':
                match(',');
                expr();
                code.emit(instruction);
                exprlistp(instruction);
                break;
            case ')':
                break;
            default:
                error("exprlistp error");
        }
    }


    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "C:\\Users\\feder\\Desktop\\Linguaggi Formali e Traduttori\\Laboratorio LFT\\5 - Generazione del bytecode\\Jasmine\\src\\lexer\\test.lft"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Translator parser = new Translator(lex, br);
            parser.prog();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
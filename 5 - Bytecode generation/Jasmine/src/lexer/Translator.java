package lexer;

import lexer.CodeGenerator;
import lexer.OpCode;
import lexer.SymbolTable;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

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
        System.out.println("token = " + look);    }

    void error(String s) {
        throw new Error("near line " + lex.line + ": " + s);
    }

    void match(int t) {
        if (look.tag == t) {
            if (look.tag != Tag.EOF) move();
        } else error("syntax error");
    }

    public void start() {
        prog();
        try {
            code.toJasmin();
        }
        catch(IOException e) {
            System.out.println("IO error\n");
        }
    }

    public void prog() {
        // ... completare ...
        switch (look.tag) {
            case Tag.ASSIGN, Tag.PRINT, Tag.READ, Tag.WHILE, Tag.IF, '{':
                statlist();
                match(Tag.EOF);
                break;
            default:
                error("prog error");
        }

        //int lnext_prog = code.newLabel();
        //code.emitLabel(lnext_prog);
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

    private void statlistp() {
        switch (look.tag) {
            case ';':
                match(';');
                stat();
                statlistp();
                break;
            case Tag.EOF, '}':
                break;
        }
    }


    private void stat() {
        switch (look.tag) {
            case Tag.ASSIGN:
                match(Tag.ASSIGN);
                expr();
                match(Tag.TO);
                List<Integer> to = idlist();
                for (int i = 0; i < to.size(); i++) {
                    if (i < to.size() - 1) {
                        code.emit(OpCode.dup);
                    }
                    code.emit(OpCode.istore, to.get(i));
                }                break;
            case Tag.PRINT:
                match(Tag.PRINT);
                match('(');
                exprlist(new Instruction(OpCode.invokestatic, 1), false);
                match(')');
                break;
            case Tag.READ:
                match(Tag.READ);
                match('(');
                List<Integer> ids = idlist();
                match(')');
                for (int i = 0; i < ids.size(); i++) {
                    code.emit(OpCode.invokestatic, 0);
                    code.emit(OpCode.istore, ids.get(i));
                }                break;
            case Tag.WHILE: {
                match(Tag.WHILE);
                match('(');
                int while_true = code.newLabel();
                int stat_next = code.newLabel();
                code.emitLabel(stat_next);
                bexpr(while_true, false);
                match(')');
                code.emitLabel(stat_next);
                stat();
                code.emit(OpCode.GOto, stat_next);
                code.emitLabel(while_true);
                break;
            }
            case Tag.IF:
                int thenEndLabel = code.newLabel();

                match(Tag.IF);
                match('(');
                bexpr(thenEndLabel, false);
                match(')');
                stat();
                statp(thenEndLabel);
                break;
            case '{':
                match('{');
                statlist();
                match('}');
                break;
        }
    }

    private void statp(int thenEndLabel) {
        if (look.tag != Tag.END && look.tag != Tag.ELSE) {
            error("expected 'end', 'else'");
        }

        switch (look.tag) {
            case Tag.END:
                code.emitLabel(thenEndLabel);
                match(Tag.END);
                break;
            case Tag.ELSE:
                int ifEndLabel = code.newLabel();
                code.emit(OpCode.GOto, ifEndLabel);
                code.emitLabel(thenEndLabel);
                match(Tag.ELSE);
                stat();
                match(Tag.END);
                code.emitLabel(ifEndLabel);
                break;
        }
    }

    private List<Integer> idlist() {
        if (look.tag != Tag.ID) {
            error("expected identifier");
        }

        List<Integer> ids = new LinkedList<>();
        if (look.tag == Tag.ID) {
            int id_addr = st.lookupAddress(((Word) look).lexeme);
            if (id_addr == -1) {
                id_addr = count;
                st.insert(((Word) look).lexeme, count++);
            }
            ids.add(id_addr);
        }
        match(Tag.ID);
        idlistp(ids);
        return ids;
    }

    private void idlistp(List<Integer> ids) {
        //if (look.tag != ',' && look.tag != ';' && look.tag != Tag.END && look.tag != Tag.ELSE && look.tag != Tag.EOF && look.tag != '}' && look.tag != ')') {
        //  error("expected ',', ';', 'end', 'else', '}', ')', end of file");
        //}

        if (look.tag == ',') {
            match(',');
            if (look.tag == Tag.ID) {
                int id_addr = st.lookupAddress(((Word) look).lexeme);
                if (id_addr == -1) {
                    id_addr = count;
                    st.insert(((Word) look).lexeme, count++);
                }
                ids.add(id_addr);
            }
            match(Tag.ID);
            idlistp(ids);
        }
    }


    private void bexpr(int label, boolean expected) {
        if (look.tag != Tag.RELOP && look.tag != '!' && look.tag != Tag.AND && look.tag != Tag.OR) {
            error("expected '==', '<>', '<', '>', '<=', '>=', '!', '&&', '||'");
        }

        Token t = look;
        // 5.2 & 5.3
        switch (t.tag) {
            case '!':
                match('!');
                bexpr(label, !expected);
                break;
            case Tag.AND:
                match(Tag.AND);
                if (expected) { // AND
                    int andEndLabel = code.newLabel();
                    bexpr(andEndLabel, false);
                    bexpr(label, true);
                    code.emitLabel(andEndLabel);
                } else { // NAND
                    bexpr(label, false);
                    bexpr(label, false);
                }
                break;
            case Tag.OR:
                match(Tag.OR);
                if (expected) { // OR
                    bexpr(label, true);
                    bexpr(label, true);
                } else { // NOR
                    int orEndLabel = code.newLabel();
                    bexpr(orEndLabel, true);
                    bexpr(label, false);
                    code.emitLabel(orEndLabel);
                }
                break;
            case Tag.RELOP:
                // 5.2 & 5.3
                match(Tag.RELOP);
                expr();
                expr();
                switch (((Word) t).lexeme) {
                    case "==":
                        code.emit(expected ? OpCode.if_icmpeq : OpCode.if_icmpne, label);
                        break;
                    case "<>":
                        code.emit(expected ? OpCode.if_icmpne : OpCode.if_icmpeq, label);
                        break;
                    case "<":
                        code.emit(expected ? OpCode.if_icmplt : OpCode.if_icmpge, label);
                        break;
                    case "<=":
                        code.emit(expected ? OpCode.if_icmple : OpCode.if_icmpgt, label);
                        break;
                    case ">":
                        code.emit(expected ? OpCode.if_icmpgt : OpCode.if_icmple, label);
                        break;
                    case ">=":
                        code.emit(expected ? OpCode.if_icmpge : OpCode.if_icmplt, label);
                        break;
                }
                // 5.2 & 5.3
                break;
        }
        // 5.2 & 5.3
    }

    private void expr() {
        switch (look.tag) {
            case '+':
                match('+');
                match('(');
                exprlist(new Instruction(OpCode.iadd), true);
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
                exprlist(new Instruction(OpCode.imul), true);
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

    private void exprlist(Instruction instruction, boolean binary) {
        if (look.tag != '+' && look.tag != '-' && look.tag != '*' && look.tag != '/' && look.tag != Tag.NUM && look.tag != Tag.ID) {
            error("expected '+', '-', '*', '/', number, identifier");
        }

        expr();
        if (!binary) code.emit(instruction);
        exprlistp(instruction);
    }

    private void exprlistp(Instruction instruction) {
        //if (look.tag != ',' && look.tag != ')') {
        //  error("expected ',', ')'");
        //}

        if (look.tag == ',') {
            match(',');
            expr();
            code.emit(instruction);
            exprlistp(instruction);
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "C:\\Users\\yasya\\OneDrive\\Desktop\\Laboratorio LFT\\5 - Generazione del bytecode\\Jasmine\\src\\lexer\\test.lft"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Translator translator = new Translator(lex, br);
            translator.start();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

// ... completare ...
}


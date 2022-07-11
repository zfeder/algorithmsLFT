package parser;

import java.io.*;
import java.util.*;

public class Lexer {

    public static int line = 1;
    private char peek = ' ';


    private void readch(BufferedReader br) {
        try {
            peek = (char) br.read();
        } catch (IOException exc) {
            peek = (char) -1; // ERROR
        }
    }

    public Token lexical_scan(BufferedReader br) {
        while (peek == ' ' || peek == '\t' || peek == '\n'  || peek == '\r' || peek == '/') {

            //Gestire commenti

            if (peek == '/') {
                readch(br);
                if (peek == '*') {
                    readch(br);
                    boolean closed = true;
                    while (closed && peek != (char) -1) {
                        if (peek == '*') {
                            readch(br);
                            if (peek == '/') {
                                closed = false;
                            }
                        }
                        if (peek == '\n') {
                            line++;
                        }
                        readch(br);
                    }
                    if (closed && peek == (char) -1) {
                        System.out.println("Il commento è stato gestito in modo errato.");
                        return null;
                    }
                } else if (peek == '/') {
                    while (peek != '\n' && peek != (char) -1) {
                        readch(br);
                    }
                } else {
                    return Token.div;
                }
            }

            if (peek == '\n') line++;
            readch(br);
        }

        switch (peek) {
            case '!':
                peek = ' ';
                return Token.not;

            // ... gestire i casi di ( ) { } + - * / ; , ... //

            case '(':
                peek = ' ';
                return Token.lpt;

            case ')':
                peek = ' ';
                return Token.rpt;

            case '{':
                peek = ' ';
                return Token.lpg;

            case '}':
                peek = ' ';
                return Token.rpg;

            case '+':
                peek = ' ';
                return Token.plus;

            case '-':
                peek = ' ';
                return Token.minus;

            case '*':
                peek = ' ';
                return Token.mult;

            case '/':
                peek = ' ';
                return Token.div;

            case ';':
                peek = ' ';
                return Token.semicolon;

            case ',':
                peek = ' ';
                return Token.comma;

            case '&':
                readch(br);
                if (peek == '&') {
                    peek = ' ';
                    return Word.and;
                } else {
                    System.err.println("Erroneous character"
                            + " after & : "  + peek );
                    return null;
                }

                // ... gestire i casi di || < > <= >= == <> ... //

            case '|':
                readch(br);
                if (peek == '|') {
                    peek = ' ';
                    return Word.or;
                } else {
                    System.err.println("Erroneous character"
                            + " after | : " + peek);
                    return null;
                }
            case '<':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.le;
                } else if (peek == '>') {
                    peek = ' ';
                    return Word.ne;
                } else {
                    return Word.lt;
                }
            case '>':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.ge;
                } else {
                    return Word.gt;
                }
            case '=':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.eq;
                } else {
                    return Word.assign;
                }

            case (char)-1:
                return new Token(Tag.EOF);

            default:
                if (Character.isLetter(peek)) {

                    // ... gestire il caso degli identificatori e delle parole chiave //
                    String id = "";
                    if(peek == '_'){
                        readch(br);
                        while (peek == '_'){
                            id += peek;
                            readch(br);
                        }
                    }
                    while (Character.isDigit(peek) || Character.isLetter(peek) || peek == '_') {
                        id += peek;
                        readch(br);
                    }
                    switch (id) {
                        case "assign":
                            return Word.assign;
                        case "to":
                            return Word.to;
                        case "if":
                            return Word.iftok;
                        case "else":
                            return Word.elsetok;
                        case "while":
                            return Word.whiletok;
                        case "begin":
                            return Word.begin;
                        case "end":
                            return Word.end;
                        case "print":
                            return Word.print;
                        case "read":
                            return Word.read;
                        default:
                            return new Word(Tag.ID, id);
                    }

                } else if (Character.isDigit(peek)) {

                    // ... gestire il caso dei numeri ... //

                    String number = "" + peek;
                    readch(br);
                    while (Character.isDigit(peek)) {
                        number = number + peek;
                        readch(br);
                    }
                    if (Character.isLetter(peek) || peek == '_') {
                        System.err.println("Error, " + number + " cannot be a number.");
                        return null;
                    }
                    if (number.length() > 1 && number.charAt(0) == '0') {
                        System.err.println("Error, " + number + " cannot be a number.");
                        return null;
                    } else {
                        return new NumberTok(Tag.NUM, Integer.parseInt(number));
                    }


                } else {
                    System.err.println("Erroneous character: "
                            + peek );
                    return null;
                }
        }
    }
}

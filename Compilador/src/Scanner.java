import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {

    private static final Map<String, TipoToken> palabrasReservadas;

    static {
        palabrasReservadas = new HashMap<>();
        palabrasReservadas.put("and",    TipoToken.AND);
        palabrasReservadas.put("else",   TipoToken.ELSE);
        palabrasReservadas.put("false",  TipoToken.FALSE);
        palabrasReservadas.put("for",    TipoToken.FOR);
        palabrasReservadas.put("fun",    TipoToken.FUN);
        palabrasReservadas.put("if",     TipoToken.IF);
        palabrasReservadas.put("null",   TipoToken.NULL);
        palabrasReservadas.put("or",     TipoToken.OR);
        palabrasReservadas.put("print",  TipoToken.PRINT);
        palabrasReservadas.put("return", TipoToken.RETURN);
        palabrasReservadas.put("true",   TipoToken.TRUE);
        palabrasReservadas.put("var",    TipoToken.VAR);
        palabrasReservadas.put("while",  TipoToken.WHILE);
    }

    private final String source;

    private final List<Token> tokens = new ArrayList<>();
    
    public Scanner(String source){
        this.source = source + " ";
    }

    public List<Token> scan() throws Exception {
        String lexema = "";
        int estado = 0;
        char c;

        for(int i=0; i<source.length(); i++){
            c = source.charAt(i);

            switch (estado){
                case 0:

                    if (c == '*') {
                        tokens.add(new Token(TipoToken.STAR, lexema));
                    }else if (c == '+') {
                        tokens.add(new Token(TipoToken.PLUS, lexema));
                    } else if (c == '-') {
                        tokens.add(new Token(TipoToken.MINUS, lexema));
                    } else if (c == '(') {
                        tokens.add(new Token(TipoToken.LEFT_PAREN, lexema));
                    } else if (c == ')') {
                        tokens.add(new Token(TipoToken.RIGHT_PAREN, lexema ));
                    } else if (c == '{') {
                        tokens.add(new Token(TipoToken.LEFT_BRACE, lexema));
                    } else if (c == '}') {
                        tokens.add(new Token(TipoToken.RIGHT_BRACE, lexema));
                    } else if (c == ',') {
                        tokens.add(new Token(TipoToken.COMMA, lexema));
                    } else if (c == '.') {
                        tokens.add(new Token(TipoToken.DOT, lexema));
                    } else if (c == ';') {
                        tokens.add(new Token(TipoToken.SEMICOLON,lexema));
                    } else if (Character.isAlphabetic(c)) {
                        estado = 1;
                        lexema = lexema + c;
                    } else if (Character.isDigit(c)) {
                        estado = 2;
                        lexema = lexema + c;
                    } else if (c == '>') {
                        estado = 8;
                    } else if (c == '<') {
                        estado = 9;
                    } else if (c == '=') {
                        estado = 10;
                    } else if (c == '!') {
                        estado = 11;
                    } else if (c == '/') {
                        estado = 12;
                    } else if (c == '"') {
                        estado = 13;
                    } else if (c == '\n') {
                    }else {
                        //throw new RuntimeException("Token no definido"+caracter);
                    }
                    break;

                case 8:
                    if (c == '=') {
                        tokens.add(new Token(TipoToken.GREATER_EQUAL, lexema ));
                    } else {
                        tokens.add(new Token(TipoToken.GREATER, lexema));
                        i--;
                    }
                    estado = 0;
                    break;
                case 9:
                    if (c == '=') {
                        tokens.add(new Token(TipoToken.LESS_EQUAL, lexema));
                    } else {
                        tokens.add(new Token(TipoToken.LESS, lexema));
                        i--;
                    }
                    estado = 0;
                    break;
                case 10:
                    if (c == '=') {
                        tokens.add(new Token(TipoToken.EQUAL_EQUAL, lexema));
                    } else {
                        tokens.add(new Token(TipoToken.EQUAL, lexema));
                        i--;
                    }
                    estado = 0;
                    break;
                case 11:
                    if (c == '=') {
                        tokens.add(new Token(TipoToken.BANG_EQUAL, lexema));
                    } else {
                        tokens.add(new Token(TipoToken.BANG, lexema));
                        i--;
                    }
                    estado = 0;
                    break;

            }
        }


        return tokens;
    }
}

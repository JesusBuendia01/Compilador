
import java.util.*;

public class AnalizadorLexico {

    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private static final Map<String, TipoToken> palabrasReservadas;
    private static int numerolinea;

    static {
        palabrasReservadas = new HashMap<>();
        palabrasReservadas.put("and", TipoToken.AND);
        palabrasReservadas.put("class", TipoToken.CLASS);
        palabrasReservadas.put("else", TipoToken.ELSE);
        palabrasReservadas.put("false", TipoToken.FALSE);
        palabrasReservadas.put("for", TipoToken.FOR);
        palabrasReservadas.put("fun", TipoToken.FUN);
        palabrasReservadas.put("if", TipoToken.IF);
        palabrasReservadas.put("null", TipoToken.NULL);
        palabrasReservadas.put("or", TipoToken.OR);
        palabrasReservadas.put("print", TipoToken.PRINT);
        palabrasReservadas.put("return", TipoToken.RETURN);
        palabrasReservadas.put("super", TipoToken.SUPER);
        palabrasReservadas.put("this", TipoToken.THIS);
        palabrasReservadas.put("var", TipoToken.VAR);
        palabrasReservadas.put("true", TipoToken.TRUE);
        palabrasReservadas.put("while", TipoToken.WHILE);
    }

    public AnalizadorLexico(String source) {
        this.source = source + " ";
        numerolinea = 1;
    }

    public List<Token> scanTokens() {
        int estado = 0;
        char caracter = 0;
        String lexema = "";

        for (int i = 0; i < source.length(); i++) {
            caracter = source.charAt(i);
            switch (estado) {
                case 0:
                    if (caracter == '*') {
                        tokens.add(new Token(TipoToken.STAR, "*", numerolinea));
                    } else if (caracter == '+') {
                        tokens.add(new Token(TipoToken.PLUS, "+", numerolinea));
                    } else if (caracter == '-') {
                        tokens.add(new Token(TipoToken.MINUS, "-", numerolinea));
                    } else if (caracter == '(') {
                        tokens.add(new Token(TipoToken.LEFT_PAREN, "(", numerolinea));
                    } else if (caracter == ')') {
                        tokens.add(new Token(TipoToken.RIGHT_PAREN, ")", numerolinea));
                    } else if (caracter == '{') {
                        tokens.add(new Token(TipoToken.LEFT_BRACE, "{", numerolinea));
                    } else if (caracter == '}') {
                        tokens.add(new Token(TipoToken.RIGHT_BRACE, "}", numerolinea));
                    } else if (caracter == ',') {
                        tokens.add(new Token(TipoToken.COMMA, ",", numerolinea));
                    } else if (caracter == '.') {
                        tokens.add(new Token(TipoToken.DOT, ".", numerolinea));
                    } else if (caracter == ';') {
                        tokens.add(new Token(TipoToken.SEMICOLON, ";", numerolinea));
                    } else if (caracter == '(') {
                        tokens.add(new Token(TipoToken.LEFT_PAREN, "(", numerolinea));
                    } else if (Character.isAlphabetic(caracter)) {
                        estado = 1;
                        lexema = lexema + caracter;
                    } else if (Character.isDigit(caracter)) {
                        estado = 2;
                        lexema = lexema + caracter;
                    } else if (caracter == '>') {
                        estado = 8;
                    } else if (caracter == '<') {
                        estado = 9;
                    } else if (caracter == '=') {
                        estado = 10;
                    } else if (caracter == '!') {
                        estado = 11;
                    } else if (caracter == '/') {
                        estado = 12;
                    } else if (caracter == '"') {
                        estado = 13;
                    } else if (caracter == '\n') {
                        numerolinea++;
                    }else {
                        //throw new RuntimeException("Token no definido"+caracter);
                    }
                    break;
                case 1:
                    if (Character.isAlphabetic(caracter) || Character.isDigit(caracter)) {
                        lexema = lexema + caracter;
                    } else {
                        TipoToken tt = palabrasReservadas.get(lexema);
                        if (tt == null) {
                                tokens.add(new Token(TipoToken.IDENTIFIER, lexema, lexema, numerolinea));
                        } else {
                            if (lexema == "verdadero") {
                                tokens.add(new Token(TipoToken.TRUE, lexema, true, numerolinea));
                            } else if (lexema=="falso") {
                                tokens.add(new Token(TipoToken.FALSE,lexema,false,numerolinea));
                            } else if (lexema=="nulo") {
                                tokens.add(new Token(TipoToken.NULL,lexema,null,numerolinea));
                            } else {
                                tokens.add(new Token(tt, lexema, numerolinea));
                            }
                        }
                        estado = 0;
                        i--;
                        lexema = "";
                    }
                    break;
                case 2:
                    if (Character.isDigit(caracter)) {
                        estado = 2;
                        lexema = lexema + caracter;
                    } else if (caracter == '.') {
                        estado = 3;
                        lexema = lexema + caracter;
                    } else if (caracter == 'E') {
                        estado = 5;
                        lexema = lexema + caracter;
                    } else {
                        tokens.add(new Token(TipoToken.NUMBER, lexema, Double.valueOf(lexema), numerolinea));
                        estado = 0;
                        lexema = "";
                        i--;
                    }
                    break;
                case 3:
                    if (Character.isDigit(caracter)) {
                        estado = 4;
                        lexema = lexema + caracter;
                    } else {
                        // Lanzar error
                    }
                    break;
                case 4:
                    if (Character.isDigit(caracter)) {
                        estado = 4;
                        lexema = lexema + caracter;
                    } else if (caracter == 'E') {
                        estado = 5;
                        lexema = lexema + caracter;
                    } else {
                        tokens.add(new Token(TipoToken.NUMBER, lexema, Double.valueOf(lexema), numerolinea));
                        estado = 0;
                        lexema = "";
                        i--;
                    }
                    break;
                case 5:
                    if (caracter == '+' || caracter == '-') {
                        estado = 6;
                        lexema = lexema + caracter;
                    } else if (Character.isDigit(caracter)) {
                        estado = 7;
                        lexema = lexema + caracter;
                    } else {
                        // Lanzar error
                    }
                    break;
                case 6:
                    if (Character.isDigit(caracter)) {
                        estado = 7;
                        lexema = lexema + caracter;
                    } else {
                        // Lanzar error
                    }
                    break;
                case 7:
                    if (Character.isDigit(caracter)) {
                        estado = 7;
                        lexema = lexema + caracter;
                    } else {
                        tokens.add(new Token(TipoToken.NUMBER, lexema, Double.valueOf(lexema), numerolinea));
                        estado = 0;
                        lexema = "";
                        i--;
                    }
                    break;
                case 8:
                    if (caracter == '=') {
                        tokens.add(new Token(TipoToken.GREATER_EQUAL, ">=", numerolinea));
                    } else {
                        tokens.add(new Token(TipoToken.GREATER, ">", numerolinea));
                        i--;
                    }
                    estado = 0;
                    break;
                case 9:
                    if (caracter == '=') {
                        tokens.add(new Token(TipoToken.LESS_EQUAL, "<=", numerolinea));
                    } else {
                        tokens.add(new Token(TipoToken.LESS, "<", numerolinea));
                        i--;
                    }
                    estado = 0;
                    break;
                case 10:
                    if (caracter == '=') {
                        tokens.add(new Token(TipoToken.EQUAL_EQUAL, "==", numerolinea));
                    } else {
                        tokens.add(new Token(TipoToken.EQUAL, "=", numerolinea));
                        i--;
                    }
                    estado = 0;
                    break;
                case 11:
                    if (caracter == '=') {
                        tokens.add(new Token(TipoToken.BANG_EQUAL, "!=", numerolinea));
                    } else {
                        tokens.add(new Token(TipoToken.BANG, "!", numerolinea));
                        i--;
                    }
                    estado = 0;
                    break;
                case 12:
                    if (caracter == '/') {
                        int finComentario = source.indexOf('\n', i);
                        if (finComentario == -1) {
                            i = source.length() - 1;
                        } else {
                            i = finComentario - 1;
                        }
                    } else if (caracter == '*') { // Comentario Multilinea
                        int finComentario = source.indexOf("*/", i);
                        if (finComentario == -1) {
                            i = source.length() - 1;
                        } else {
                            int saltosDeLinea = contarSaltosDeLinea(source.substring(i, finComentario));
                            numerolinea += saltosDeLinea;
                            i = finComentario + 1;
                        }
                        estado = 0;
                    } else {
                        tokens.add(new Token(TipoToken.SLASH, "/", numerolinea));
                        i--;
                    }
                    estado = 0;
                    break;
                case 13:
                    if (caracter == '"') {
                        tokens.add(new Token(TipoToken.STRING, lexema,lexema.toString(), numerolinea));
                        estado = 0;
                        lexema = "";
                    } else {
                        lexema = lexema + caracter;
                    }

                    break;
            }

        }

        tokens.add(new Token(TipoToken.EOF, "", numerolinea));

        return tokens;
    }

    private int contarSaltosDeLinea(String texto) {
        int count = 0;
        int index = 0;
        while ((index = texto.indexOf('\n', index)) != -1) {
            count++;
            index++;
        }
        return count;
    }

}

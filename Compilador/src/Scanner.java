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
                        lexema += c;
                        estado = 13;
                    } else if (c == '"') {
                        lexema += c;
                        estado = 12;
                    } else if (c == '\n' || c == '\0' || c == '\t' || c == '\r' || c == ' ') {
                        if (lexema.compareTo("") != 0) {
                            throw new RuntimeException("No se puede concatenar: " + c);
                        }
                        estado = 0;
                    }else {
                        throw new RuntimeException("Token no definido: " + c);
                    }
                    break;
                
                // Identificadores y palabras reservadas
                case 1:
                    if (!(Character.isAlphabetic(c) || Character.isDigit(c))) {
                        TipoToken tipoToken = palabrasReservadas.get(lexema);
                        Token token = null;
                        if (tipoToken == null) {
                            token = new Token(TipoToken.IDENTIFIER, lexema);
                        } else {
                            token = new Token(tipoToken, lexema);
                        }
                        tokens.add(token);
                        estado = 0;
                        lexema = "";
                        i -= 1;
                    } else {
                        lexema += c;
                    }
                    break; 
                
                // Decimals and integer numbers
                case 2:
                    if (Character.isDigit(c)) {
                        lexema += c;
                    } else if (c == '.') {
                        lexema += c;
                        estado = 3;
                    } else if (c == 'E') {
                        lexema += c;
                        estado = 4;
                    } else {
                        Token token = new Token(
                            TipoToken.NUMBER,
                            lexema,
                            Integer.valueOf(lexema)
                        );
                        tokens.add(token);
                        estado = 0;
                        lexema = "";
                        i -= 1;
                    }
                    break;
                    
                // Reading decimal numbers
                case 3:
                    if (c == '.') {
                        throw new RuntimeException("Error en Decimal!: " + c);
                    } 
                    if (Character.isDigit(c)) {
                        lexema += c;
                    } else if (c == 'E') {
                        lexema += c;
                        estado = 4;
                    } else {
                        Token token = new Token(
                            TipoToken.NUMBER,
                            lexema,
                            Double.parseDouble(lexema)
                        );
                        tokens.add(token);
                        estado = 0;
                        lexema = "";
                        i -= 1;
                    }
                    break;
                
                // Exponent number
                case 4:
                    if (c == '.') {
                        throw new RuntimeException("Error en Decimal!: " + c);
                    } 
                    if (c == '+' || c == '-') {
                        lexema += c;
                    } else if (Character.isDigit(c)) {
                        lexema += c;
                    } else {
                        throw new RuntimeException("No se puede concatenar: " + c);
                    }
                    estado = 5;
                    break;

                // Last decimals                
                case 5:
                    if (Character.isDigit(c)) {
                        lexema += c;
                    } else {
                        Token token = new Token(
                            TipoToken.NUMBER,
                            lexema,
                            Double.parseDouble(lexema)
                        );
                        tokens.add(token);
                        estado = 0;
                        lexema = "";
                        i -= 1;
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
                    lexema = "";
                    break;
                case 9:
                    if (c == '=') {
                        tokens.add(new Token(TipoToken.LESS_EQUAL, lexema));
                    } else {
                        tokens.add(new Token(TipoToken.LESS, lexema));
                        i--;
                    }
                    estado = 0;
                    lexema = "";
                    break;
                case 10:
                    if (c == '=') {
                        tokens.add(new Token(TipoToken.EQUAL_EQUAL, lexema));
                    } else {
                        tokens.add(new Token(TipoToken.EQUAL, lexema));
                        i--;
                    }
                    estado = 0;
                    lexema = "";
                    break;
                case 11:
                    if (c == '=') {
                        tokens.add(new Token(TipoToken.BANG_EQUAL, lexema));
                    } else {
                        tokens.add(new Token(TipoToken.BANG, lexema));
                        i--;
                    }
                    estado = 0;
                    lexema = "";
                    break;
                
                // Strings
                case 12:
                    if (c == '\n' || c == '\0') {
                        throw new RuntimeException("No se puede concatenar: " + c);
                    } if (c == '"') {
                        lexema += c;
                        Token token = new Token(
                            TipoToken.STRING, 
                            lexema, 
                            lexema.substring(1, lexema.length() - 1)
                        );
                        tokens.add(token);
                        estado = 0;
                        lexema = "";
                    } else {
                        lexema += c;
                    }
                    break;
                    
                // Division
                case 13:
                    if (c == '/') {
                        lexema += c;
                        estado = 14;
                    } else if (c == '*') {
                        lexema += c;
                        estado = 15;
                    } else {
                        Token token = new Token(
                            TipoToken.SLASH, 
                            lexema
                        );
                        tokens.add(token);
                        lexema = "";
                        estado = 0;
                    }
                    lexema = "";
                    break;
                
                // Line comments
                case 14:
                    if (c == '\n' || c == '\0') {
                        estado = 0;
                    }
                    break;
                
                // Block comments
                case 15:
                    if (c == '*') {
                        estado = 16;
                    }
                    break;
                case 16:
                    if (c == '/') {
                        estado = 0;
                    } else if (c != '*') {
                        estado = 15;
                    }
                    break;
                
            }
        }


        return tokens;
    }
}



/*
Analisis sintactico descendente.

instr -> expr;
      -> if(expr) instr
      -> for (expropc, expropc, expropc) instr
      -> Otras

expropc -> E
        -> otras
*/
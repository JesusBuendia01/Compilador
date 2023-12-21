
import java.util.*;

public class AnalizadorSintactico {

    private List<Statement> threeList;
    private Statement three = null;
    private Queue<Token> tokensToGroup;
    private boolean newAssignment = false;
    private boolean newFunction = false;
    private int blockRecursiveId = 0;

    private int i = 0;
    private boolean hayErrores = false;
    private Token preanalisis;
    private final List<Token> tokens;

    public AnalizadorSintactico(List<Token> tokens) {
        this.tokens = tokens;
        this.threeList = new ArrayList<Statement>();
        this.tokensToGroup = new LinkedList<>();
    }

    public void analizadorSintactico() {
        this.i = 0;
        preanalisis = tokens.get(this.i);
        PROGRAM();
        if (!hayErrores && !(preanalisis.tipoToken == TipoToken.EOF)) {
            System.out.println(
                    "Error en la linea :" + preanalisis.linea + ". No se esperaba el token " + preanalisis.tipoToken);
        } else if (!hayErrores && preanalisis.tipoToken == TipoToken.EOF) {
            System.out.println("Consulta válida");
        }
    }

    private void PROGRAM() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.CLASS
                || preanalisis.tipoToken == TipoToken.FUN
                || preanalisis.tipoToken == TipoToken.VAR
                || preanalisis.tipoToken == TipoToken.BANG
                || preanalisis.tipoToken == TipoToken.MINUS
                || preanalisis.tipoToken == TipoToken.TRUE
                || preanalisis.tipoToken == TipoToken.FALSE
                || preanalisis.tipoToken == TipoToken.NULL
                || preanalisis.tipoToken == TipoToken.THIS
                || preanalisis.tipoToken == TipoToken.NUMBER
                || preanalisis.tipoToken == TipoToken.STRING
                || preanalisis.tipoToken == TipoToken.IDENTIFIER
                || preanalisis.tipoToken == TipoToken.LEFT_PAREN
                || preanalisis.tipoToken == TipoToken.SUPER
                || preanalisis.tipoToken == TipoToken.FOR
                || preanalisis.tipoToken == TipoToken.IF
                || preanalisis.tipoToken == TipoToken.PRINT
                || preanalisis.tipoToken == TipoToken.RETURN
                || preanalisis.tipoToken == TipoToken.WHILE
                || preanalisis.tipoToken == TipoToken.LEFT_BRACE
        ) {
            DECLARATION();
        } else {
            System.out.println("ERROR IN FUNCTION PROGRAM");
            hayErrores = true;
            System.out.println("Error en la linea: :" + preanalisis.linea +
                    " se esperaba alguna de las siguientes palabras/simbolos: " +
                    "clase, funcion, var, !, -, verdadero, falso, nulo, este, numero, "+
                    "cadena, id, (, super, para, si, imprimir, retornar, mientras, {"
            );
        }

    }

    private void DECLARATION() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.CLASS) {
            CLASS_DECL();
            DECLARATION();
        } else if (preanalisis.tipoToken == TipoToken.FUN) {
            FUN_DECL();
            DECLARATION();
        } else if (preanalisis.tipoToken == TipoToken.VAR) {
            VAR_DECL();
            DECLARATION();
        } else if (preanalisis.tipoToken == TipoToken.BANG
                || preanalisis.tipoToken == TipoToken.MINUS
                || preanalisis.tipoToken == TipoToken.TRUE
                || preanalisis.tipoToken == TipoToken.FALSE
                || preanalisis.tipoToken == TipoToken.NULL
                || preanalisis.tipoToken == TipoToken.THIS
                || preanalisis.tipoToken == TipoToken.NUMBER
                || preanalisis.tipoToken == TipoToken.STRING
                || preanalisis.tipoToken == TipoToken.IDENTIFIER
                || preanalisis.tipoToken == TipoToken.LEFT_PAREN
                || preanalisis.tipoToken == TipoToken.SUPER
                || preanalisis.tipoToken == TipoToken.FOR
                || preanalisis.tipoToken == TipoToken.IF
                || preanalisis.tipoToken == TipoToken.PRINT
                || preanalisis.tipoToken == TipoToken.RETURN
                || preanalisis.tipoToken == TipoToken.WHILE
                || preanalisis.tipoToken == TipoToken.LEFT_BRACE) {
            STATEMENT();
            DECLARATION();
        }
    }

    private void CLASS_DECL() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.CLASS) {
            coincidir(TipoToken.CLASS);
            coincidir(TipoToken.IDENTIFIER);
            CLASS_INHER();
            coincidir(TipoToken.LEFT_BRACE);
            FUNCTIONS();
            coincidir(TipoToken.RIGHT_BRACE);
        } else {
            hayErrores = true;
            System.out.println("Error en la linea: :" + preanalisis.linea + " se esperaba la palabra reservada clase");
        }

    }

    void CLASS_INHER() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.LESS) {
            coincidir(TipoToken.LESS);
            coincidir(TipoToken.IDENTIFIER);
        }
    }

    private void FUN_DECL() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.FUN) {
            coincidir(TipoToken.FUN);
            FUNCTION();
        } else {
            hayErrores = true;
            System.out
                    .println("Error en la linea: :" + preanalisis.linea + " se esperaba la palabra reservada funcion");
        }
    }

    private void VAR_DECL() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.VAR) {
            if (!coincidir(TipoToken.VAR)) {
                this.tokensToGroup.clear();
                return;
            }

            Token temp = preanalisis;
            if (!coincidir(TipoToken.IDENTIFIER)) {
                temp = null;
                this.tokensToGroup.clear();
                return;
            };

            this.tokensToGroup.add(temp);

            VAR_INIT();

            if (!coincidir(TipoToken.SEMICOLON)) {
                this.tokensToGroup.clear();
                return;
            }

            addNewVariableStatementToThree();
        } else {
            hayErrores = true;
            System.out.println("Error en la linea: :" + preanalisis.linea + " se esperaba la palabra reservada var");
        }
    }

    private Token VAR_INIT() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores) return null;
        if (preanalisis.tipoToken == TipoToken.EQUAL) {
            if (!coincidir(TipoToken.EQUAL)) this.tokensToGroup.clear();
            EXPRESSION();
        }
        return null;
    }

    private void STATEMENT() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.BANG
                || preanalisis.tipoToken == TipoToken.MINUS
                || preanalisis.tipoToken == TipoToken.TRUE
                || preanalisis.tipoToken == TipoToken.FALSE
                || preanalisis.tipoToken == TipoToken.NULL
                || preanalisis.tipoToken == TipoToken.THIS
                || preanalisis.tipoToken == TipoToken.NUMBER
                || preanalisis.tipoToken == TipoToken.STRING
                || preanalisis.tipoToken == TipoToken.IDENTIFIER
                || preanalisis.tipoToken == TipoToken.LEFT_PAREN
                || preanalisis.tipoToken == TipoToken.SUPER) {
            EXPR_STMT();
        } else if (preanalisis.tipoToken == TipoToken.FOR) {
            FOR_STMT();
        } else if (preanalisis.tipoToken == TipoToken.IF) {
            IF_STMT();
        } else if (preanalisis.tipoToken == TipoToken.PRINT) {
            PRINT_STMT();
        } else if (preanalisis.tipoToken == TipoToken.RETURN) {
            RETURN_STMT();
        } else if (preanalisis.tipoToken == TipoToken.WHILE) {
            WHILE_STMT();
        } else if (preanalisis.tipoToken == TipoToken.LEFT_BRACE) {
            BLOC();
        } else {
            hayErrores = true;
            System.out.println("Error en la linea: :" + preanalisis.linea
                    + " se esperaba alguna de las siguientes palabras/simbolos: " +
                    "!, -, verdadero, falso, nulo, este, numero, cadena, id, (, super, para, si, imprimir, retornar," +
                    "mientras, { ");
        }
    }

    private void EXPR_STMT() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.BANG
                || preanalisis.tipoToken == TipoToken.MINUS
                || preanalisis.tipoToken == TipoToken.TRUE
                || preanalisis.tipoToken == TipoToken.FALSE
                || preanalisis.tipoToken == TipoToken.NULL
                || preanalisis.tipoToken == TipoToken.THIS
                || preanalisis.tipoToken == TipoToken.NUMBER
                || preanalisis.tipoToken == TipoToken.STRING
                || preanalisis.tipoToken == TipoToken.IDENTIFIER
                || preanalisis.tipoToken == TipoToken.LEFT_PAREN
                || preanalisis.tipoToken == TipoToken.SUPER) {
            EXPRESSION();
            if (!coincidir(TipoToken.SEMICOLON)) return;
            if (this.newAssignment) {
                System.out.println("ALAVRGAAAAAAAAAAAAAAA ACABO EL PDO DE LA EXPRESION SI ES UNA ASIGNACION!    247");
                System.out.println(this.tokensToGroup);
                this.addNewAssignmentToThree();
                this.newAssignment = false;
            }
        } else {
            hayErrores = true;
            System.out.println("Error en la linea: :" + preanalisis.linea
                    + " se esperaba alguna de las siguientes palabras/simbolos: " +
                    "!, -, verdadero, falso, nulo, este, numero, cadena, id, (, super");
        }
    }

    private void FOR_STMT() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.FOR) {
            coincidir(TipoToken.FOR);
            coincidir(TipoToken.LEFT_PAREN);
            FOR_STMT_1();
            FOR_STMT_2();
            FOR_STMT_3();
            coincidir(TipoToken.RIGHT_PAREN);
            STATEMENT();
        } else {
            hayErrores = true;
            System.out.println("Error en la linea: :" + preanalisis.linea + " se esperaba la palabra reservada para");
        }
    }

    private void FOR_STMT_1() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.VAR) {
            VAR_DECL();
        } else if (preanalisis.tipoToken == TipoToken.BANG
                || preanalisis.tipoToken == TipoToken.MINUS
                || preanalisis.tipoToken == TipoToken.TRUE
                || preanalisis.tipoToken == TipoToken.FALSE
                || preanalisis.tipoToken == TipoToken.NULL
                || preanalisis.tipoToken == TipoToken.THIS
                || preanalisis.tipoToken == TipoToken.NUMBER
                || preanalisis.tipoToken == TipoToken.STRING
                || preanalisis.tipoToken == TipoToken.IDENTIFIER
                || preanalisis.tipoToken == TipoToken.LEFT_PAREN
                || preanalisis.tipoToken == TipoToken.SUPER) {
            EXPR_STMT();
        } else if (preanalisis.tipoToken == TipoToken.SEMICOLON) {
            coincidir(TipoToken.SEMICOLON);
        } else {
            hayErrores = true;
            System.out.println("Error en la linea: :" + preanalisis.linea
                    + " se esperaba alguna de las siguientes palabras/simbolos: " +
                    "var, !, -, verdadero, falso, nulo, este, numero, cadena, id, (, super, ;");
        }
    }

    private void FOR_STMT_2() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.BANG
                || preanalisis.tipoToken == TipoToken.MINUS
                || preanalisis.tipoToken == TipoToken.TRUE
                || preanalisis.tipoToken == TipoToken.FALSE
                || preanalisis.tipoToken == TipoToken.NULL
                || preanalisis.tipoToken == TipoToken.THIS
                || preanalisis.tipoToken == TipoToken.NUMBER
                || preanalisis.tipoToken == TipoToken.STRING
                || preanalisis.tipoToken == TipoToken.IDENTIFIER
                || preanalisis.tipoToken == TipoToken.LEFT_PAREN
                || preanalisis.tipoToken == TipoToken.SUPER) {
            EXPRESSION();
            coincidir(TipoToken.SEMICOLON);
        } else if (preanalisis.tipoToken == TipoToken.SEMICOLON) {
            coincidir(TipoToken.SEMICOLON);
        } else {
            hayErrores = true;
            System.out.println("Error en la linea: :" + preanalisis.linea
                    + " se esperaba alguna de las siguientes palabras/simbolos: " +
                    "!, -, verdadero, falso, nulo, este, numero, cadena, id, (, super, ;");
        }
    }

    private void FOR_STMT_3() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.BANG
                || preanalisis.tipoToken == TipoToken.MINUS
                || preanalisis.tipoToken == TipoToken.TRUE
                || preanalisis.tipoToken == TipoToken.FALSE
                || preanalisis.tipoToken == TipoToken.NULL
                || preanalisis.tipoToken == TipoToken.THIS
                || preanalisis.tipoToken == TipoToken.NUMBER
                || preanalisis.tipoToken == TipoToken.STRING
                || preanalisis.tipoToken == TipoToken.IDENTIFIER
                || preanalisis.tipoToken == TipoToken.LEFT_PAREN
                || preanalisis.tipoToken == TipoToken.SUPER) {
            EXPRESSION();
        }
    }

    private void IF_STMT() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.IF) {
            coincidir(TipoToken.IF);
            coincidir(TipoToken.LEFT_PAREN);
            EXPRESSION();
            coincidir(TipoToken.RIGHT_PAREN);
            STATEMENT();
            ELSE_STMT();
        } else {
            hayErrores = true;
            System.out.println("Error en la linea: :" + preanalisis.linea + " se esperaba la palabra reservada si");
        }
    }

    private void ELSE_STMT() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.ELSE) {
            coincidir(TipoToken.ELSE);
            STATEMENT();
        }
    }

    private void PRINT_STMT() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.PRINT) {
            coincidir(TipoToken.PRINT);
            EXPRESSION();
            coincidir(TipoToken.SEMICOLON);
        } else {
            hayErrores = true;
            System.out
                    .println("Error en la linea: :" + preanalisis.linea + " se esperaba la palabra reservada imprimir");
        }
    }

    private void RETURN_STMT() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.RETURN) {
            coincidir(TipoToken.RETURN);
            RETURN_EXP_OPC();
            coincidir(TipoToken.SEMICOLON);
        } else {
            hayErrores = true;
            System.out
                    .println("Error en la linea: :" + preanalisis.linea + " se esperaba la palabra reservada retornar");
        }
    }

    private void RETURN_EXP_OPC() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.BANG
                || preanalisis.tipoToken == TipoToken.MINUS
                || preanalisis.tipoToken == TipoToken.TRUE
                || preanalisis.tipoToken == TipoToken.FALSE
                || preanalisis.tipoToken == TipoToken.NULL
                || preanalisis.tipoToken == TipoToken.THIS
                || preanalisis.tipoToken == TipoToken.NUMBER
                || preanalisis.tipoToken == TipoToken.STRING
                || preanalisis.tipoToken == TipoToken.IDENTIFIER
                || preanalisis.tipoToken == TipoToken.LEFT_PAREN
                || preanalisis.tipoToken == TipoToken.SUPER) {
            EXPRESSION();
        }
    }

    private void WHILE_STMT() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.WHILE) {
            coincidir(TipoToken.WHILE);
            coincidir(TipoToken.LEFT_PAREN);
            EXPRESSION();
            coincidir(TipoToken.RIGHT_PAREN);
            STATEMENT();
        } else {
            hayErrores = true;
            System.out
                    .println("Error en la linea: :" + preanalisis.linea + " se esperaba la palabra reservada mientras");
        }
    }

    private void BLOC() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.LEFT_BRACE) {
            if (!coincidir(TipoToken.LEFT_BRACE)) this.tokensToGroup.clear();
            BLOCK_DECL();
            if(!coincidir(TipoToken.RIGHT_BRACE)) this.tokensToGroup.clear();   
        } else {
            hayErrores = true;
            System.out.println("Error en la linea: :" + preanalisis.linea + " se esperaba: { ");
        }
    }

    private void BLOCK_DECL() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.CLASS
                || preanalisis.tipoToken == TipoToken.FUN
                || preanalisis.tipoToken == TipoToken.VAR
                || preanalisis.tipoToken == TipoToken.BANG
                || preanalisis.tipoToken == TipoToken.MINUS
                || preanalisis.tipoToken == TipoToken.TRUE
                || preanalisis.tipoToken == TipoToken.FALSE
                || preanalisis.tipoToken == TipoToken.NULL
                || preanalisis.tipoToken == TipoToken.THIS
                || preanalisis.tipoToken == TipoToken.NUMBER
                || preanalisis.tipoToken == TipoToken.STRING
                || preanalisis.tipoToken == TipoToken.IDENTIFIER
                || preanalisis.tipoToken == TipoToken.LEFT_PAREN
                || preanalisis.tipoToken == TipoToken.SUPER
                || preanalisis.tipoToken == TipoToken.FOR
                || preanalisis.tipoToken == TipoToken.IF
                || preanalisis.tipoToken == TipoToken.PRINT
                || preanalisis.tipoToken == TipoToken.RETURN
                || preanalisis.tipoToken == TipoToken.WHILE
                || preanalisis.tipoToken == TipoToken.LEFT_BRACE) {
            DECLARATION();
            BLOCK_DECL();
        }
    }

    private void EXPRESSION() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.BANG
                || preanalisis.tipoToken == TipoToken.MINUS
                || preanalisis.tipoToken == TipoToken.TRUE
                || preanalisis.tipoToken == TipoToken.FALSE
                || preanalisis.tipoToken == TipoToken.NULL
                || preanalisis.tipoToken == TipoToken.THIS
                || preanalisis.tipoToken == TipoToken.NUMBER
                || preanalisis.tipoToken == TipoToken.STRING
                || preanalisis.tipoToken == TipoToken.IDENTIFIER
                || preanalisis.tipoToken == TipoToken.LEFT_PAREN
                || preanalisis.tipoToken == TipoToken.SUPER) {
            ASSIGNMENT();
        } else {
            hayErrores = true;
            System.out.println("Error en la linea: :" + preanalisis.linea
                    + " se esperaba alguna de las siguientes palabras/simbolos: " +
                    "!, -, verdadero, falso, nulo, este, numero, cadena, id, (, super");
        }
    }

    private void ASSIGNMENT() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.BANG
                || preanalisis.tipoToken == TipoToken.MINUS
                || preanalisis.tipoToken == TipoToken.TRUE
                || preanalisis.tipoToken == TipoToken.FALSE
                || preanalisis.tipoToken == TipoToken.NULL
                || preanalisis.tipoToken == TipoToken.THIS
                || preanalisis.tipoToken == TipoToken.NUMBER
                || preanalisis.tipoToken == TipoToken.STRING
                || preanalisis.tipoToken == TipoToken.IDENTIFIER
                || preanalisis.tipoToken == TipoToken.LEFT_PAREN
                || preanalisis.tipoToken == TipoToken.SUPER) {
            LOGIC_OR();
            ASSIGNMENT_OPC();
        } else {
            hayErrores = true;
            System.out.println("Error en la linea: :" + preanalisis.linea
                    + " se esperaba alguna de las siguientes palabras/simbolos: " +
                    "!, -, verdadero, falso, nulo, este, numero, cadena, id, (, super");
        }
    }

    private void ASSIGNMENT_OPC() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.EQUAL) {
            if (coincidir(TipoToken.EQUAL)) this.newAssignment = true;
            EXPRESSION();
        }
    }

    private void LOGIC_OR() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.BANG
                || preanalisis.tipoToken == TipoToken.MINUS
                || preanalisis.tipoToken == TipoToken.TRUE
                || preanalisis.tipoToken == TipoToken.FALSE
                || preanalisis.tipoToken == TipoToken.NULL
                || preanalisis.tipoToken == TipoToken.THIS
                || preanalisis.tipoToken == TipoToken.NUMBER
                || preanalisis.tipoToken == TipoToken.STRING
                || preanalisis.tipoToken == TipoToken.IDENTIFIER
                || preanalisis.tipoToken == TipoToken.LEFT_PAREN
                || preanalisis.tipoToken == TipoToken.SUPER) {
            LOGIC_AND();
            LOGIC_OR_2();
        } else {
            hayErrores = true;
            System.out.println("Error en la linea: :" + preanalisis.linea
                    + " se esperaba alguna de las siguientes palabras/simbolos: " +
                    "!, -, verdadero, falso, nulo, este, numero, cadena, id, (, super");
        }
    }

    private void LOGIC_OR_2() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.OR) {
            coincidir(TipoToken.OR);
            LOGIC_AND();
            LOGIC_OR_2();
        }
    }

    private void LOGIC_AND() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.BANG
                || preanalisis.tipoToken == TipoToken.MINUS
                || preanalisis.tipoToken == TipoToken.TRUE
                || preanalisis.tipoToken == TipoToken.FALSE
                || preanalisis.tipoToken == TipoToken.NULL
                || preanalisis.tipoToken == TipoToken.THIS
                || preanalisis.tipoToken == TipoToken.NUMBER
                || preanalisis.tipoToken == TipoToken.STRING
                || preanalisis.tipoToken == TipoToken.IDENTIFIER
                || preanalisis.tipoToken == TipoToken.LEFT_PAREN
                || preanalisis.tipoToken == TipoToken.SUPER) {
            EQUALITY();
            LOGIC_AND_2();
        } else {
            hayErrores = true;
            System.out.println("Error en la linea: :" + preanalisis.linea
                    + " se esperaba alguna de las siguientes palabras/simbolos: " +
                    "!, -, verdadero, falso, nulo, este, numero, cadena, id, (, super");
        }
    }

    private void LOGIC_AND_2() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.AND) {
            coincidir(TipoToken.AND);
            EQUALITY();
            LOGIC_AND();
        }
    }

    private void EQUALITY() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.BANG
                || preanalisis.tipoToken == TipoToken.MINUS
                || preanalisis.tipoToken == TipoToken.TRUE
                || preanalisis.tipoToken == TipoToken.FALSE
                || preanalisis.tipoToken == TipoToken.NULL
                || preanalisis.tipoToken == TipoToken.THIS
                || preanalisis.tipoToken == TipoToken.NUMBER
                || preanalisis.tipoToken == TipoToken.STRING
                || preanalisis.tipoToken == TipoToken.IDENTIFIER
                || preanalisis.tipoToken == TipoToken.LEFT_PAREN
                || preanalisis.tipoToken == TipoToken.SUPER) {
            COMPARISSON();
            EQUALITY_2();
        } else {
            hayErrores = true;
            System.out.println("Error en la linea: :" + preanalisis.linea
                    + " se esperaba alguna de las siguientes palabras/simbolos: " +
                    "!, -, verdadero, falso, nulo, este, numero, cadena, id, (, super");
        }
    }

    private void EQUALITY_2() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.BANG_EQUAL) {
            coincidir(TipoToken.BANG_EQUAL);
            COMPARISSON();
            EQUALITY_2();
        } else if (preanalisis.tipoToken == TipoToken.EQUAL_EQUAL) {
            coincidir(TipoToken.EQUAL_EQUAL);
            COMPARISSON();
            EQUALITY_2();

        }
    }

    private void COMPARISSON() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.BANG
                || preanalisis.tipoToken == TipoToken.MINUS
                || preanalisis.tipoToken == TipoToken.TRUE
                || preanalisis.tipoToken == TipoToken.FALSE
                || preanalisis.tipoToken == TipoToken.NULL
                || preanalisis.tipoToken == TipoToken.THIS
                || preanalisis.tipoToken == TipoToken.NUMBER
                || preanalisis.tipoToken == TipoToken.STRING
                || preanalisis.tipoToken == TipoToken.IDENTIFIER
                || preanalisis.tipoToken == TipoToken.LEFT_PAREN
                || preanalisis.tipoToken == TipoToken.SUPER) {
            TERM();
            COMPARISSON_2();
        }
    }

    private void COMPARISSON_2() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.GREATER) {
            coincidir(TipoToken.GREATER);
            TERM();
            COMPARISSON_2();
        } else if (preanalisis.tipoToken == TipoToken.GREATER_EQUAL) {
            coincidir(TipoToken.GREATER_EQUAL);
            TERM();
            COMPARISSON_2();
        } else if (preanalisis.tipoToken == TipoToken.LESS) {
            coincidir(TipoToken.LESS);
            TERM();
            COMPARISSON_2();
        } else if (preanalisis.tipoToken == TipoToken.LESS_EQUAL) {
            coincidir(TipoToken.LESS_EQUAL);
            TERM();
            COMPARISSON_2();
        }
    }

    private void TERM() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.BANG
                || preanalisis.tipoToken == TipoToken.MINUS
                || preanalisis.tipoToken == TipoToken.TRUE
                || preanalisis.tipoToken == TipoToken.FALSE
                || preanalisis.tipoToken == TipoToken.NULL
                || preanalisis.tipoToken == TipoToken.THIS
                || preanalisis.tipoToken == TipoToken.NUMBER
                || preanalisis.tipoToken == TipoToken.STRING
                || preanalisis.tipoToken == TipoToken.IDENTIFIER
                || preanalisis.tipoToken == TipoToken.LEFT_PAREN
                || preanalisis.tipoToken == TipoToken.SUPER) {
            FACTOR();
            TERM_2();
        }
    }

    private void TERM_2() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.MINUS) {
            Token temp = preanalisis;
            if (coincidir(TipoToken.MINUS)) this.tokensToGroup.add(temp);
            FACTOR();
            TERM_2();
        } else if (preanalisis.tipoToken == TipoToken.PLUS) {
            Token temp = preanalisis;
            if (coincidir(TipoToken.PLUS)) this.tokensToGroup.add(temp);
            FACTOR();
            TERM_2();
        }
    }

    private void FACTOR() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.BANG
                || preanalisis.tipoToken == TipoToken.MINUS
                || preanalisis.tipoToken == TipoToken.TRUE
                || preanalisis.tipoToken == TipoToken.FALSE
                || preanalisis.tipoToken == TipoToken.NULL
                || preanalisis.tipoToken == TipoToken.THIS
                || preanalisis.tipoToken == TipoToken.NUMBER
                || preanalisis.tipoToken == TipoToken.STRING
                || preanalisis.tipoToken == TipoToken.IDENTIFIER
                || preanalisis.tipoToken == TipoToken.LEFT_PAREN
                || preanalisis.tipoToken == TipoToken.SUPER) {
            UNARY();
            FACTOR_2();
        }
    }

    private void FACTOR_2() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.SLASH) {
            coincidir(TipoToken.SLASH);
            UNARY();
            FACTOR_2();
        } else if (preanalisis.tipoToken == TipoToken.STAR) {
            Token temp = preanalisis;
            if (coincidir(TipoToken.STAR)) {
                this.tokensToGroup.add(temp);
            } else {
                this.tokensToGroup.clear();
            }
            UNARY();
            FACTOR_2();

        }
    }

    private void UNARY() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.BANG) {
            coincidir(TipoToken.BANG);
            UNARY();
        } else if (preanalisis.tipoToken == TipoToken.MINUS) {
            Token temp = preanalisis;
            if (!coincidir(TipoToken.MINUS)) {
                this.tokensToGroup.clear();
            } else {
                this.tokensToGroup.add(temp);
            }
            UNARY();
        } else if (isPrimary(preanalisis.tipoToken)) {
            CALL();
        } else {
            hayErrores = true;
            System.out.println("Error en la linea: :" + preanalisis.linea
                    + " se esperaba alguna de las siguientes palabras/simbolos: " +
                    "!, -, verdadero, falso, nulo, este, numero, cadena, id, (, super");
        }
    }

    private void CALL() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (isPrimary(preanalisis.tipoToken)) {
            PRIMARY();
            CALL_2();
        } else {
            hayErrores = true;
            System.out.println("Error en la linea: :" + preanalisis.linea
                    + " se esperaba alguna de las siguientes palabras/simbolos: " +
                    " verdadero, falso, nulo, este, numero, cadena, id, (, super");
        }
    }

    private void CALL_2() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.LEFT_PAREN) {
            System.out.println("LEYENDO FUNCIOOOOOOOOON O CLASEEEEEEEEEEEEEEEEEEEEEEEEE 826");
            coincidir(TipoToken.LEFT_PAREN);
            ARGUMENTS_OPC();
            coincidir(TipoToken.RIGHT_PAREN);
            CALL_2();
        } else if (preanalisis.tipoToken == TipoToken.DOT) {
            coincidir(TipoToken.DOT);
            coincidir(TipoToken.IDENTIFIER);
            CALL_2();
        }
    }

    private void PRIMARY() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.TRUE) {
            coincidir(TipoToken.TRUE);
        } else if (preanalisis.tipoToken == TipoToken.FALSE) {
            coincidir(TipoToken.FALSE);
        } else if (preanalisis.tipoToken == TipoToken.NULL) {
            coincidir(TipoToken.NULL);
        } else if (preanalisis.tipoToken == TipoToken.THIS) {
            coincidir(TipoToken.THIS);
        } else if (preanalisis.tipoToken == TipoToken.NUMBER) {
            Token temp = preanalisis;
            if (!coincidir(TipoToken.NUMBER)) {
                this.tokensToGroup.clear();
                return;
            }
            this.tokensToGroup.add(temp);
        } else if (preanalisis.tipoToken == TipoToken.STRING) {
            Token temp = preanalisis;
            if (!coincidir(TipoToken.STRING)) {
                this.tokensToGroup.clear();
                return;
            }
            this.tokensToGroup.add(temp);
        } else if (preanalisis.tipoToken == TipoToken.IDENTIFIER) {
            Token temp = preanalisis;
            if (!coincidir(TipoToken.IDENTIFIER)) {
                this.tokensToGroup.clear();
                return;
            }
            this.tokensToGroup.add(temp);
        } else if (preanalisis.tipoToken == TipoToken.LEFT_PAREN) {
            Token temp = preanalisis;
            if (coincidir(TipoToken.LEFT_PAREN)) {
                this.tokensToGroup.add(temp);
            } else {
                this.tokensToGroup.clear();
            }
            EXPRESSION();
            temp = preanalisis;
            if (coincidir(TipoToken.RIGHT_PAREN)) {
                this.tokensToGroup.add(temp);
            } else {
                this.tokensToGroup.clear();
            }
        } else if (preanalisis.tipoToken == TipoToken.SUPER) {
            coincidir(TipoToken.SUPER);
            coincidir(TipoToken.DOT);
            coincidir(TipoToken.IDENTIFIER);
        } else {
            hayErrores = true;
            System.out.println("Error en la linea: :" + preanalisis.linea
                    + " se esperaba alguna de las siguientes palabras/simbolos: " +
                    " verdadero, falso, nulo, este, numero, cadena, id, (, super");
        }
    }

    private void FUNCTION() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.IDENTIFIER) {
            Token temp = preanalisis;
            this.blockRecursiveId += 1;

            if (coincidir(TipoToken.IDENTIFIER)) {
                this.tokensToGroup.add(temp);
            } else {
                this.tokensToGroup.clear();
            }

            temp = preanalisis;

            if (coincidir(TipoToken.LEFT_PAREN)) {
                this.tokensToGroup.add(temp);
            } else {
                this.tokensToGroup.clear();
            }

            PARAMETERS_OPC();
            temp = preanalisis;

            if (coincidir(TipoToken.RIGHT_PAREN)) {
                this.tokensToGroup.add(temp);
            } else {
                this.tokensToGroup.clear();
            }

            BLOC();

            if (this.blockRecursiveId == 1) {
                this.addNewFunctionToThree();
            }

            this.blockRecursiveId -= 1;
            
        } else {
            hayErrores = true;
            System.out.println("Error en la linea: :" + preanalisis.linea + " se esperaba : id");
        }
    }

    private void FUNCTIONS() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.IDENTIFIER) {
            FUNCTION();
            FUNCTIONS();
        }
    }

    private void PARAMETERS_OPC() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.IDENTIFIER) {
            PARAMETERS();
        }
    }

    private void PARAMETERS() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.IDENTIFIER) {
            coincidir(TipoToken.IDENTIFIER);
            PARAMETERS_2();
        } else {
            hayErrores = true;
            System.out.println("Error en la linea: :" + preanalisis.linea + " se esperaba: id");
        }
    }

    private void PARAMETERS_2() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.COMMA) {
            coincidir(TipoToken.COMMA);
            coincidir(TipoToken.IDENTIFIER);
            PARAMETERS_2();
        }
    }

    private void ARGUMENTS_OPC() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.BANG
                || preanalisis.tipoToken == TipoToken.MINUS
                || preanalisis.tipoToken == TipoToken.TRUE
                || preanalisis.tipoToken == TipoToken.FALSE
                || preanalisis.tipoToken == TipoToken.NULL
                || preanalisis.tipoToken == TipoToken.THIS
                || preanalisis.tipoToken == TipoToken.NUMBER
                || preanalisis.tipoToken == TipoToken.STRING
                || preanalisis.tipoToken == TipoToken.IDENTIFIER
                || preanalisis.tipoToken == TipoToken.LEFT_PAREN
                || preanalisis.tipoToken == TipoToken.SUPER) {
            ARGUMENTS();
        }
    }

    private void ARGUMENTS() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.BANG
                || preanalisis.tipoToken == TipoToken.MINUS
                || preanalisis.tipoToken == TipoToken.TRUE
                || preanalisis.tipoToken == TipoToken.FALSE
                || preanalisis.tipoToken == TipoToken.NULL
                || preanalisis.tipoToken == TipoToken.THIS
                || preanalisis.tipoToken == TipoToken.NUMBER
                || preanalisis.tipoToken == TipoToken.STRING
                || preanalisis.tipoToken == TipoToken.IDENTIFIER
                || preanalisis.tipoToken == TipoToken.LEFT_PAREN
                || preanalisis.tipoToken == TipoToken.SUPER) {
            EXPRESSION();
            ARGUMENTS_2();
        } else {
            hayErrores = true;
            System.out.println("Error en la linea: :" + preanalisis.linea
                    + " se esperaba alguna de las siguientes palabras/simbolos: " +
                    "!, -, verdadero, falso, nulo, este, numero, cadena, id, (, super");
        }
    }

    private void ARGUMENTS_2() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return;
        if (preanalisis.tipoToken == TipoToken.COMMA) {
            Token temp = preanalisis;
            if (coincidir(TipoToken.COMMA)) this.tokensToGroup.add(preanalisis);
            else this.tokensToGroup.clear();
            EXPRESSION();
            ARGUMENTS_2();
        }
    }

    private boolean coincidir(TipoToken t) {
        if (hayErrores)
            return false;
        System.out.println(preanalisis.tipoToken + " - " + preanalisis.lexema);
        if (preanalisis.tipoToken == t) {
            i++;
            preanalisis = tokens.get(i);
            return true;
        } else {
            hayErrores = true;
            System.out.println("Error en la linea: " + preanalisis.linea + " se esperaba un: " + t);
        }
        return false;
    }
    
    private boolean isPrimary(TipoToken token) {
        return     token == TipoToken.TRUE
                || token == TipoToken.FALSE
                || token == TipoToken.NULL
                || token == TipoToken.THIS
                || token == TipoToken.NUMBER
                || token == TipoToken.STRING
                || token == TipoToken.IDENTIFIER
                || token == TipoToken.LEFT_PAREN
                || token == TipoToken.SUPER;
    }

    private void addNewVariableStatementToThree() {
        Token variableName = this.tokensToGroup.remove();
        Expression initExpression = null;
        if (this.tokensToGroup.peek() != null) {
            Token unary = null;
            if (this.tokensToGroup.peek().tipoToken == TipoToken.MINUS) {
                unary = this.tokensToGroup.remove();
            }

            initExpression = new ExprLiteral(this.tokensToGroup.remove().literal);

            if (unary != null) {
                initExpression = new ExprUnary(unary, initExpression);
                initExpression = new ExprAssign(unary, initExpression);
            } else {
            }

            initExpression = new ExprGrouping(initExpression);
        }

        StmtVar statementVar = new StmtVar(variableName, initExpression);
        this.threeList.add(statementVar);
        this.tokensToGroup.clear();
    }

    private void addNewAssignmentToThree() {
        Token variableName = this.tokensToGroup.remove();

    }

    private void addNewFunctionToThree() {
        System.out.println(this.tokensToGroup);
        this.tokensToGroup.clear();
    }
}
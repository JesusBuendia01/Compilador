
import java.util.*;

public class AnalizadorSintactico {

    private List<Statement> threeList;
    private boolean insideBlockDeclaration = false;
    private List<Statement> statementsForBlock;

    private int i = 0;
    private boolean hayErrores = false;
    private Token preanalisis;
    private final List<Token> tokens;

    public AnalizadorSintactico(List<Token> tokens) {
        this.tokens = tokens;
        this.threeList = new ArrayList<Statement>();
        this.statementsForBlock = new ArrayList<>();
    }

    public void analizadorSintactico() {
        this.i = 0;
        preanalisis = tokens.get(this.i);

        PROGRAM();
        // lista de statements
        System.out.println("\n AST:");
        System.out.println(this.threeList);
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

    private Statement DECLARATION() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores) return null;
        if (preanalisis.tipoToken == TipoToken.FUN) {
            Statement functionDeclaration = FUN_DECL();
            if (this.insideBlockDeclaration) {
                this.statementsForBlock.add(functionDeclaration);
            } else {
                this.statementsForBlock.clear();
            }
            DECLARATION();
        } else if (preanalisis.tipoToken == TipoToken.VAR) {
            Statement varDeclaration = VAR_DECL();
            if (this.insideBlockDeclaration) {
                this.statementsForBlock.add(varDeclaration);
            } else {
                this.statementsForBlock.clear();
            }
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
            Statement statement = STATEMENT();

            if (this.insideBlockDeclaration) {
                this.statementsForBlock.add(statement);
            } else {
                this.statementsForBlock.clear();
            }

            DECLARATION();
        }

        return null;
    }

    private StmtFunction FUN_DECL() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores) return null;
        if (preanalisis.tipoToken == TipoToken.FUN) {
            if (!coincidir(TipoToken.FUN)) return null;
            return FUNCTION();
        } else {
            hayErrores = true;
            System.out.println("Error en la linea: :" + preanalisis.linea + " se esperaba la palabra reservada funcion");
        }

        return null;
    }

    private StmtVar VAR_DECL() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores) return null;
        if (preanalisis.tipoToken == TipoToken.VAR) {
            if (!coincidir(TipoToken.VAR)) return null;

            Token identificador = preanalisis;
            if (!coincidir(TipoToken.IDENTIFIER)) return null;

            Expression varInit = VAR_INIT();

            if (!coincidir(TipoToken.SEMICOLON)) return null;

            StmtVar newVariable = new StmtVar(identificador, varInit);

            if (!this.insideBlockDeclaration) this.threeList.add(newVariable);

            return newVariable;
        } else {
            hayErrores = true;
            System.out.println("Error en la linea: :" + preanalisis.linea + " se esperaba la palabra reservada var");
        }

        return null;
    }

    private Expression VAR_INIT() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores) return null;
        if (preanalisis.tipoToken == TipoToken.EQUAL) {
            if (!coincidir(TipoToken.EQUAL)) return null;
            return EXPRESSION();
        }
        return null;
    }

    private Statement STATEMENT() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores) return null;
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
            return EXPR_STMT();
        } else if (preanalisis.tipoToken == TipoToken.FOR) {
            return FOR_STMT();
        } else if (preanalisis.tipoToken == TipoToken.IF) {
            return IF_STMT();
        } else if (preanalisis.tipoToken == TipoToken.PRINT) {
            return PRINT_STMT();
        } else if (preanalisis.tipoToken == TipoToken.RETURN) {
            return RETURN_STMT();
        } else if (preanalisis.tipoToken == TipoToken.WHILE) {
            return WHILE_STMT();
        } else if (preanalisis.tipoToken == TipoToken.LEFT_BRACE) {
            return BLOC();
        } else {
            hayErrores = true;
            System.out.println("Error en la linea: :" + preanalisis.linea
                    + " se esperaba alguna de las siguientes palabras/simbolos: " +
                    "!, -, verdadero, falso, nulo, este, numero, cadena, id, (, super, para, si, imprimir, retornar," +
                    "mientras, { ");
        }

        return null;
    }

    private StmtExpression EXPR_STMT() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores) return null;
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
            Expression expression = EXPRESSION();
            StmtExpression statementExpression = new StmtExpression(expression);

            if (!coincidir(TipoToken.SEMICOLON)) return null;

            if (!this.insideBlockDeclaration) this.threeList.add(statementExpression);

            return statementExpression;
        } else {
            hayErrores = true;
            System.out.println("Error en la linea: :" + preanalisis.linea
                    + " se esperaba alguna de las siguientes palabras/simbolos: " +
                    "!, -, verdadero, falso, nulo, este, numero, cadena, id, (, super");
        }

        return null;
    }

    private StmtLoop FOR_STMT() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores) return null;
        if (preanalisis.tipoToken == TipoToken.FOR) {
            if (!coincidir(TipoToken.FOR)) return null;
            if (!coincidir(TipoToken.LEFT_PAREN)) return null;

            
            Statement stmt1;
            Expression stmt2;
            Expression stmt3;

            if (!this.insideBlockDeclaration) {
                this.insideBlockDeclaration = true;
                stmt1 = FOR_STMT_1();
                stmt2 = FOR_STMT_2();
                stmt3 = FOR_STMT_3();
                this.insideBlockDeclaration = false;
            } else {
                stmt1 = FOR_STMT_1();
                stmt2 = FOR_STMT_2();
                stmt3 = FOR_STMT_3();
            }

            if (!coincidir(TipoToken.RIGHT_PAREN)) return null;

            if (this.hayErrores) return null;

            Statement body;

            if (!this.insideBlockDeclaration) {
                this.insideBlockDeclaration = true;
                body = STATEMENT();
                this.insideBlockDeclaration = false;
            } else {
                body = STATEMENT();
            }

            List<Statement> forBodyList = new ArrayList<>();
            if (stmt1 == null && stmt2 == null && stmt3 == null) {
                forBodyList.add(body);
                stmt2 = null;
            } else {
                forBodyList.add(body);
                forBodyList.add(new StmtExpression(stmt3));
            }

            StmtBlock forBody = new StmtBlock(forBodyList);

            StmtLoop forLoop = new StmtLoop(stmt2, forBody);

            if (!this.insideBlockDeclaration) this.threeList.add(forLoop);

            return forLoop;
            
        } else {
            hayErrores = true;
            System.out.println("Error en la linea: :" + preanalisis.linea + " se esperaba la palabra reservada para");
        }

        return null;
    }

    private Statement FOR_STMT_1() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores) return null;
        if (preanalisis.tipoToken == TipoToken.VAR) {
            return VAR_DECL();
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
            StmtExpression statementExpression = EXPR_STMT();
            return statementExpression;
        } else if (preanalisis.tipoToken == TipoToken.SEMICOLON) {
            if (!coincidir(TipoToken.SEMICOLON)) return null;
        }
        // } else {
        //     hayErrores = true;
        //     System.out.println("Error en la linea: :" + preanalisis.linea
        //             + " se esperaba alguna de las siguientes palabras/simbolos: " +
        //             "var, !, -, verdadero, falso, nulo, este, numero, cadena, id, (, super, ;");
        // }
        return null;
    }

    private Expression FOR_STMT_2() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores) return null;
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
            Expression expression = EXPRESSION();
            if(!coincidir(TipoToken.SEMICOLON)) return null;
            return expression;
        } else if (preanalisis.tipoToken == TipoToken.SEMICOLON) {
            if (!coincidir(TipoToken.SEMICOLON)) return null;
        }
        // } else {
        //     hayErrores = true;
        //     System.out.println("Error en la linea: :" + preanalisis.linea
        //             + " se esperaba alguna de las siguientes palabras/simbolos: " +
        //             "!, -, verdadero, falso, nulo, este, numero, cadena, id, (, super, ;");
        // }

        return null;
    }

    private Expression FOR_STMT_3() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores) return null;
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
            return EXPRESSION();
        }

        return null;
    }

    private StmtIf IF_STMT() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores) return null;
        if (preanalisis.tipoToken == TipoToken.IF) {
            if (!coincidir(TipoToken.IF)) return null;
            if (!coincidir(TipoToken.LEFT_PAREN)) return null;

            Expression expression = EXPRESSION();

            if (!coincidir(TipoToken.RIGHT_PAREN)) return null;

            Statement ifStatement;

            Statement elseStatement;

            if (!this.insideBlockDeclaration) {
                this.insideBlockDeclaration = true;
                ifStatement = STATEMENT();
                elseStatement = ELSE_STMT();
                this.insideBlockDeclaration = false;
            } else {
                ifStatement = STATEMENT();
                elseStatement = ELSE_STMT();
            }

            StmtIf ifStmt = new StmtIf(expression, ifStatement, elseStatement);

            if (!this.insideBlockDeclaration) this.threeList.add(ifStmt);

            return ifStmt;
        } else {
            hayErrores = true;
            System.out.println("Error en la linea: :" + preanalisis.linea + " se esperaba la palabra reservada si");
        }

        return null;
    }

    private Statement ELSE_STMT() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores) return null;
        if (preanalisis.tipoToken == TipoToken.ELSE) {
            if (!coincidir(TipoToken.ELSE)) return null;
            return STATEMENT();
        }

        return null;
    }

    private StmtPrint PRINT_STMT() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores) return null;
        if (preanalisis.tipoToken == TipoToken.PRINT) {
            if (!coincidir(TipoToken.PRINT)) return null;
            Expression expression = EXPRESSION();
            if(!coincidir(TipoToken.SEMICOLON)) return null;

            StmtPrint primtStmt = new StmtPrint(expression);

            if (!this.insideBlockDeclaration) this.threeList.add(primtStmt);

            return primtStmt;
        } else {
            hayErrores = true;
            System.out.println("Error en la linea: :" + preanalisis.linea + " se esperaba la palabra reservada imprimir");
        }

        return null;
    }

    private StmtReturn RETURN_STMT() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores) return null;
        if (preanalisis.tipoToken == TipoToken.RETURN) {
            if (!coincidir(TipoToken.RETURN)) return null;
            Expression expression = RETURN_EXP_OPC();
            if (!coincidir(TipoToken.SEMICOLON)) return null;
            return new StmtReturn(expression);
        } else {
            hayErrores = true;
            System.out
                    .println("Error en la linea: :" + preanalisis.linea + " se esperaba la palabra reservada retornar");
        }

        return null;
    }

    private Expression RETURN_EXP_OPC() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores) return null;
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
            return EXPRESSION();
        }

        return null;
    }

    private StmtLoop WHILE_STMT() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores) return null;
        if (preanalisis.tipoToken == TipoToken.WHILE) {
            if (!coincidir(TipoToken.WHILE)) return null;
            if (!coincidir(TipoToken.LEFT_PAREN)) return null;
            Expression expression = EXPRESSION();
            if (!coincidir(TipoToken.RIGHT_PAREN)) return null;
            Statement whileBody;

            if (!this.insideBlockDeclaration) {
                this.insideBlockDeclaration = true;
                whileBody = STATEMENT();
                this.insideBlockDeclaration = false;
            } else {
                whileBody = STATEMENT();
            }

            StmtLoop whileLoop = new StmtLoop(expression, whileBody);

            if (!this.insideBlockDeclaration) this.threeList.add(whileLoop);

            return whileLoop;
        } else {
            hayErrores = true;
            System.out
                    .println("Error en la linea: :" + preanalisis.linea + " se esperaba la palabra reservada mientras");
        }

        return null;
    }

    private StmtBlock BLOC() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores) return null;
        if (preanalisis.tipoToken == TipoToken.LEFT_BRACE) {
            if (!coincidir(TipoToken.LEFT_BRACE)) return null;
            List<Statement> blockDeclaration = BLOCK_DECL();
            if(!coincidir(TipoToken.RIGHT_BRACE)) return null;
            return new StmtBlock(blockDeclaration);   
        } else {
            hayErrores = true;
            System.out.println("Error en la linea: :" + preanalisis.linea + " se esperaba: { ");
        }

        return null;
    }

    private List<Statement> BLOCK_DECL() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores) return null;
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
            List<Statement> statements = new ArrayList<>();
            DECLARATION();
            // Statement declaration = DECLARATION();
            statements.addAll(this.statementsForBlock);
            this.statementsForBlock.clear();
            List<Statement> moreStatements = BLOCK_DECL();

            if (moreStatements != null) {
                statements.addAll(moreStatements);
            }

            return statements;
        }

        return null;
    }

    private Expression EXPRESSION() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return null;
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
            return ASSIGNMENT();
        } else {
            hayErrores = true;
            System.out.println("Error en la linea: :" + preanalisis.linea
                    + " se esperaba alguna de las siguientes palabras/simbolos: " +
                    "!, -, verdadero, falso, nulo, este, numero, cadena, id, (, super");
        }

        return null;
    }

    private Expression ASSIGNMENT() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return null;
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
            Expression logicOr = LOGIC_OR();
            Expression assignmentOpc = ASSIGNMENT_OPC();
            if(this.hayErrores) return null;
            return new ExprAssign(logicOr.getToken(), assignmentOpc);
        } else {
            hayErrores = true;
            System.out.println("Error en la linea: :" + preanalisis.linea
                    + " se esperaba alguna de las siguientes palabras/simbolos: " +
                    "!, -, verdadero, falso, nulo, este, numero, cadena, id, (, super");
        }

        return null;
    }

    private Expression ASSIGNMENT_OPC() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores) return null;
        if (preanalisis.tipoToken == TipoToken.EQUAL) {
            if (!coincidir(TipoToken.EQUAL)) return null;
            return EXPRESSION();
        }
        return null;
    }

    private Expression LOGIC_OR() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return null;
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
            Expression logicAnd = LOGIC_AND();
            Expression logicOr2 = LOGIC_OR_2(logicAnd);

            return logicOr2;
        } else {
            hayErrores = true;
            System.out.println("Error en la linea: :" + preanalisis.linea
                    + " se esperaba alguna de las siguientes palabras/simbolos: " +
                    "!, -, verdadero, falso, nulo, este, numero, cadena, id, (, super");
        }

        return null;
    }

    private Expression LOGIC_OR_2(Expression initialExpression) {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores) return null;
        if (preanalisis.tipoToken == TipoToken.OR) {
            Token temp = preanalisis;
            if (!coincidir(TipoToken.OR)) return null;
            Expression logicAnd = LOGIC_AND();
            Expression logicOr2 = LOGIC_OR_2(logicAnd);

            return new ExprLogical(initialExpression, temp, logicOr2);
        }

        return initialExpression;
    }

    private Expression LOGIC_AND() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return null;
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
            Expression equality = EQUALITY();
            Expression logicAnd2 = LOGIC_AND_2(equality);

            return logicAnd2;
        } else {
            hayErrores = true;
            System.out.println("Error en la linea: :" + preanalisis.linea
                    + " se esperaba alguna de las siguientes palabras/simbolos: " +
                    "!, -, verdadero, falso, nulo, este, numero, cadena, id, (, super");
        }

        return null;
    }

    private Expression LOGIC_AND_2(Expression initialExpression) {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores) return null;
        if (preanalisis.tipoToken == TipoToken.AND) {
            Token temp = preanalisis;
            if (!coincidir(TipoToken.AND)) return null;
            Expression equality = EQUALITY();
            Expression logicAnd2 = LOGIC_AND_2(equality);

            return new ExprLogical(initialExpression, temp, logicAnd2);
        }

        return initialExpression;
    }

    private Expression EQUALITY() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return null;
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
            Expression comparisson = COMPARISSON();
            Expression equality2 = EQUALITY_2(comparisson);

            return equality2;
        } else {
            hayErrores = true;
            System.out.println("Error en la linea: :" + preanalisis.linea
                    + " se esperaba alguna de las siguientes palabras/simbolos: " +
                    "!, -, verdadero, falso, nulo, este, numero, cadena, id, (, super");
        }

        return null;
    }

    private Expression EQUALITY_2(Expression initialExpression) {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores) return null;

        Token temp = preanalisis;

        if (preanalisis.tipoToken == TipoToken.BANG_EQUAL) {
            if (!coincidir(TipoToken.BANG_EQUAL)) return null;
            Expression comparisson = COMPARISSON();
            Expression equality2 = EQUALITY_2(comparisson);

            return new ExprLogical(initialExpression, temp, equality2);
        } else if (preanalisis.tipoToken == TipoToken.EQUAL_EQUAL) {
            if (!coincidir(TipoToken.EQUAL_EQUAL)) return null;
            Expression comparisson = COMPARISSON();
            Expression equality2 = EQUALITY_2(comparisson);

            return new ExprLogical(initialExpression, temp, equality2);
        }

        return initialExpression;
    }

    private Expression COMPARISSON() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return null;
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
            Expression term = TERM();
            Expression comparisson2 = COMPARISSON_2(term);

            return comparisson2;
        }

        return null;
    }

    private Expression COMPARISSON_2(Expression initialExpression) {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores) return null;

        Token temp = preanalisis;
        
        if (preanalisis.tipoToken == TipoToken.GREATER) {
            if (!coincidir(TipoToken.GREATER)) return null;
            Expression term = TERM();
            Expression comparisson2 = COMPARISSON_2(term);

            return new ExprLogical(initialExpression, temp, comparisson2);
        } else if (preanalisis.tipoToken == TipoToken.GREATER_EQUAL) {
            if (!coincidir(TipoToken.GREATER_EQUAL)) return null;
            Expression term = TERM();
            Expression comparisson2 = COMPARISSON_2(term);

            return new ExprLogical(initialExpression, temp, comparisson2);
        } else if (preanalisis.tipoToken == TipoToken.LESS) {
            if (!coincidir(TipoToken.LESS)) return null;
            Expression term = TERM();
            Expression comparisson2 = COMPARISSON_2(term);

            return new ExprLogical(initialExpression, temp, comparisson2);
        } else if (preanalisis.tipoToken == TipoToken.LESS_EQUAL) {
            if (!coincidir(TipoToken.LESS_EQUAL)) return null;
            Expression term = TERM();
            Expression comparisson2 = COMPARISSON_2(term);

            return new ExprLogical(initialExpression, temp, comparisson2);
        }

        return initialExpression;
    }

    private Expression TERM() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return null;
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
            Expression factor = FACTOR();
            Expression term2 = TERM_2(factor);

            return term2;
        }

        return null;
    }

    private Expression TERM_2(Expression initialExpression) {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return null;
        if (preanalisis.tipoToken == TipoToken.MINUS) {
            Token temp = preanalisis;
            if (!coincidir(TipoToken.MINUS)) return null;

            Expression factor = FACTOR();
            Expression term2 = TERM_2(factor);

            return new ExprBinary(initialExpression, temp, term2);
        } else if (preanalisis.tipoToken == TipoToken.PLUS) {
            Token temp = preanalisis;
            if (!coincidir(TipoToken.PLUS)) return null;

            Expression factor = FACTOR();
            Expression term2 = TERM_2(factor);

            return new ExprBinary(initialExpression, temp, term2);
        }

        return initialExpression;
    }

    private Expression FACTOR() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return null;
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
            Expression unary = UNARY();
            Expression factor = FACTOR_2(unary);

            return factor;
        }

        return null;
    }

    private Expression FACTOR_2(Expression initialUnary) {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return null;
        if (preanalisis.tipoToken == TipoToken.SLASH) {
            Token temp = preanalisis;

            if (!coincidir(TipoToken.SLASH)) return null;
            
            Expression unary = UNARY();
            Expression factor2 = FACTOR_2(unary);

            return new ExprBinary(initialUnary, temp, factor2);
        } else if (preanalisis.tipoToken == TipoToken.STAR) {
            Token temp = preanalisis;
            if (!coincidir(TipoToken.STAR)) return null;
            
            Expression unary = UNARY();
            Expression factor2 = FACTOR_2(unary);

            return new ExprBinary(initialUnary, temp, factor2);
        }

        return initialUnary;    
    }

    private Expression UNARY() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return null;
        if (preanalisis.tipoToken == TipoToken.BANG) {
            Token temp = preanalisis;
            if (!coincidir(TipoToken.BANG)) {
                return null;
            }

            Expression expression = UNARY();

            return new ExprUnary(temp, expression);
           
        } else if (preanalisis.tipoToken == TipoToken.MINUS) {
            Token temp = preanalisis;
            if (!coincidir(TipoToken.MINUS)) {
                return null;
            }

            Expression expression = UNARY();

            return new ExprUnary(temp, expression);
        } else if (isPrimary(preanalisis.tipoToken)) {
            return CALL();
        } else {
            hayErrores = true;
            System.out.println("Error en la linea: :" + preanalisis.linea
                    + " se esperaba alguna de las siguientes palabras/simbolos: " +
                    "!, -, verdadero, falso, nulo, este, numero, cadena, id, (, super");
        }

        return null;
    }

    private Expression CALL() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return null;
        if (isPrimary(preanalisis.tipoToken)) {
            Expression identifier = PRIMARY();
            List<Expression> arguments = CALL_2();

            if (arguments != null) {
                return new ExprCallFunction(identifier, arguments);
            } else {
                return new ExprVariable(identifier.getToken());
            }
            
            
        } else {
            hayErrores = true;
            System.out.println("Error en la linea: :" + preanalisis.linea
                    + " se esperaba alguna de las siguientes palabras/simbolos: " +
                    " verdadero, falso, nulo, este, numero, cadena, id, (, super");
        }

        return null;
    }

    private List<Expression> CALL_2() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return null;
        if (preanalisis.tipoToken == TipoToken.LEFT_PAREN) {
            if (!coincidir(TipoToken.LEFT_PAREN)) return null;
            List<Expression> arguments = ARGUMENTS_OPC();

            System.out.println("ARGUMEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEENTS:");
            System.out.println(arguments);

            if (!coincidir(TipoToken.RIGHT_PAREN)) return null;
            List<Expression> moreArguments = CALL_2();

            if (moreArguments != null) {
                arguments.addAll(moreArguments);
            }

            return arguments;
        }
        // } else if (preanalisis.tipoToken == TipoToken.DOT) {
        //     coincidir(TipoToken.DOT);
        //     coincidir(TipoToken.IDENTIFIER);
        //     CALL_2();
        // }

        return null;
    }

    private Expression PRIMARY() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores) return null;
        Token temp = preanalisis;
        if (preanalisis.tipoToken == TipoToken.TRUE) {
            if (coincidir(TipoToken.TRUE)){
                return new ExprLiteral(temp.literal);
            }
        } else if (preanalisis.tipoToken == TipoToken.FALSE) {
            if (coincidir(TipoToken.FALSE)) {
                return new ExprLiteral(temp.literal);
            }
        } else if (preanalisis.tipoToken == TipoToken.NULL) {
            if(coincidir(TipoToken.NULL)) {
                return new ExprLiteral(temp.literal);
            }
        // } else if (preanalisis.tipoToken == TipoToken.THIS) {
        //     if (coincidir(TipoToken.THIS)) {
        //         return new ExprThis();
        //     }
        } else if (preanalisis.tipoToken == TipoToken.NUMBER) {
            if (coincidir(TipoToken.NUMBER)) {
                return new ExprLiteral(temp.literal);
            }
        } else if (preanalisis.tipoToken == TipoToken.STRING) {
            if (coincidir(TipoToken.STRING)) {
                return new ExprLiteral(temp.literal);
            }
        } else if (preanalisis.tipoToken == TipoToken.IDENTIFIER) {
            if (coincidir(TipoToken.IDENTIFIER)) {
                return new ExprVariable(temp);
            }
        } else if (preanalisis.tipoToken == TipoToken.LEFT_PAREN) {
            if (!coincidir(TipoToken.LEFT_PAREN)) {
                return null;
            }

            Expression expression = EXPRESSION();

            if (!coincidir(TipoToken.RIGHT_PAREN)) {
                return null;
            }

            return expression;
        // } else if (preanalisis.tipoToken == TipoToken.SUPER) {
        //     coincidir(TipoToken.SUPER);
        //     coincidir(TipoToken.DOT);
        //     coincidir(TipoToken.IDENTIFIER);
        } else {
            hayErrores = true;
            System.out.println("Error en la linea: :" + preanalisis.linea
                    + " se esperaba alguna de las siguientes palabras/simbolos: " +
                    " verdadero, falso, nulo, este, numero, cadena, id, (, super");
        }
        return null;
    }

    private StmtFunction FUNCTION() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores) return null;
        if (preanalisis.tipoToken == TipoToken.IDENTIFIER) {
            Token identificador = preanalisis;
            if (!coincidir(TipoToken.IDENTIFIER)) return null;

            if (!coincidir(TipoToken.LEFT_PAREN)) return null;

            List<Token> parameters = PARAMETERS_OPC();

            if (!coincidir(TipoToken.RIGHT_PAREN)) return null;

            StmtBlock block;
            if (!this.insideBlockDeclaration) {
                this.insideBlockDeclaration = true;
                block = BLOC();
                this.insideBlockDeclaration = false;
            } else {
                block = BLOC();
            }

            StmtFunction function = new StmtFunction(identificador, parameters, block);

            if (!this.insideBlockDeclaration) this.threeList.add(function);

            return function;
        } else {
            hayErrores = true;
            System.out.println("Error en la linea: :" + preanalisis.linea + " se esperaba : id");
        }

        return null;
    }

    private List<Statement> FUNCTIONS() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores) return null;
        if (preanalisis.tipoToken == TipoToken.IDENTIFIER) {
            List<Statement> functions = new ArrayList<>();
            StmtFunction function = FUNCTION();
            functions.add(function);
            List<Statement> moreFunctions = FUNCTIONS();

            if (moreFunctions != null) {
                functions.addAll(moreFunctions);
            }

            return functions;
        }

        return null;
    }

    private List<Token> PARAMETERS_OPC() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores) return null;
        return PARAMETERS();
        // if (preanalisis.tipoToken == TipoToken.IDENTIFIER) {
        //     PARAMETERS();
        // }
    }

    private List<Token> PARAMETERS() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores) return null;
        Token temp = preanalisis;
        if (!coincidir(TipoToken.IDENTIFIER)) return null;
        List<Token> parametersList = new ArrayList<>();
        parametersList.add(temp);
        List<Token> moreParameters = PARAMETERS_2();

        if (moreParameters != null) {
            parametersList.addAll(moreParameters);
        }

        return parametersList;

        // if (preanalisis.tipoToken == TipoToken.IDENTIFIER) {
        //     coincidir(TipoToken.IDENTIFIER);
        //     PARAMETERS_2();
        // } else {
        //     hayErrores = true;
        //     System.out.println("Error en la linea: :" + preanalisis.linea + " se esperaba: id");
        // }
    }

    private List<Token> PARAMETERS_2() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores) return null;
        if (preanalisis.tipoToken == TipoToken.COMMA) {
            if (!coincidir(TipoToken.COMMA)) return null;
            Token parameter = preanalisis;
            if (!coincidir(TipoToken.IDENTIFIER)) return null;
            List<Token> parametersList = new ArrayList<>();
            parametersList.add(parameter);
            List<Token> moreParameters = PARAMETERS_2();

            if (moreParameters != null) {
                parametersList.addAll(moreParameters);
            }

            return parametersList;
        }

        return null;
    }

    private List<Expression> ARGUMENTS_OPC() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return null;
        
        List<Expression> arguments = new ArrayList<>();
        arguments.add(EXPRESSION());
        List<Expression> moreArguments = ARGUMENTS();

        if (moreArguments != null) {
            arguments.addAll(moreArguments);
        }

        return arguments;
    }

    private List<Expression> ARGUMENTS() {
        System.out.printf("Class %s.%s\n", getClass().getName(), new Exception("is not thrown").getStackTrace()[0].getMethodName());
        if (hayErrores)
            return null;
        if (preanalisis.tipoToken == TipoToken.COMMA) {
            if (!coincidir(TipoToken.COMMA)) return null;
            List<Expression> arguments = new ArrayList<>();
            arguments.add(EXPRESSION());
            List<Expression> moreArguments = ARGUMENTS();
            
            if (moreArguments != null) {
                arguments.addAll(moreArguments);
            }

            return arguments;
        }

        return null;
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
}
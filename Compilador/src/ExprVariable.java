class ExprVariable extends Expression {
    final Token name;

    ExprVariable(Token name) {
        this.name = name;
    }

    @Override
    public Token getToken() {
        return name;
    }

    @Override
    public String toString() {
        return "Expresion: ExprVariable";
    }
}


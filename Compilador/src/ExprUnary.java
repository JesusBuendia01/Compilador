public class ExprUnary extends Expression{
    final Token operator;
    public final Expression right;

    ExprUnary(Token operator, Expression right) {
        this.operator = operator;
        this.right = right;
    }

    @Override
    public String toString() {
        return "Expresion: ExprUnary <---------------------------";
    }

    /*
    Object solve(){
        right.solve();

        return null;
    }
    */
}



public class Token {
    
    public final TipoToken tipoToken;
    public final String lexema;
    public final Object literal;
    public final int linea;

    public Token(TipoToken tipoToken,String lexema,int linea){
        this.tipoToken=tipoToken;
        this.lexema=lexema;
        this.literal=null;
        this.linea=linea;
    }
    
    public Token(TipoToken tipoToken, String lexema, Object literal, int linea){
        this.tipoToken=tipoToken;
        this.lexema=lexema;
        this.literal=literal;
        this.linea=linea;
    }

    public String toString(){
        return tipoToken+" "+lexema+" "+" linea: "+(linea)+" "+(literal==null?" " : literal.toString());
    }

}

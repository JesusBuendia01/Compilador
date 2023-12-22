// It does not contains code becouse is used as a
// "Global Object", like "Object" in java, this is
// the reason why is empty.

abstract class Expression {
    public Token getToken() {
        return null;
    }

    @Override
    public String toString() {
        return "General expression";
    }
}

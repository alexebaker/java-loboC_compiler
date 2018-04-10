package Errors;

import Tokenizer.Tokens.Token;

public abstract class Error extends Exception {
    private Token token;

    public Error() {
        token = null;
    }

    public Error(Token token) {
        super();
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    public abstract String getErrorMsg();

    @Override
    public String toString() {
        return getErrorMsg();
    }
}

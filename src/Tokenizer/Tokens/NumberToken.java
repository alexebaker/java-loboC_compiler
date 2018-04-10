package Tokenizer.Tokens;

import Compiler.Location;

public class NumberToken extends Token {
    public NumberToken(String value, Location loc) {
        super("$num", value, loc);
    }

    /**
     * Checks if ch is a valid Number Token
     *
     * @param str string to check if it is an Number Token
     * @return true if ch is a Number Token, false otherwise
     */
    public static boolean isToken(String str) {
        return str.matches("^[0-9]+\\.?[0-9]*");
    }

    /**
     * Checks if token is a valid Number Token
     *
     * @param token token to check if it is a Number Token
     * @return true if token is a Number Token, false otherwise
     */
    public static boolean isToken(Token token) {
        return token.getToken().equals("$num") && NumberToken.isToken(token.getValue());
    }

    public static boolean isDelim(String buf, int nextCh) {
        String nextStr = Character.toString((char) nextCh);
        return Token.isDelim(buf, nextCh) || !NumberToken.isToken(buf+nextStr);
    }
}

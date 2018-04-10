package Tokenizer.Tokens;

import Compiler.Location;

import java.security.Key;

public class IdentifierToken extends Token {
    public IdentifierToken(String value, Location loc) {
        super("$id", value, loc);
    }

    /**
     * Checks if ch is a valid Identifier Token
     *
     * @param str string to check if it is an Identifier Token
     * @return true if ch is an Identifier Token, false otherwise
     */
    public static boolean isToken(String str) {
        return !KeywordToken.isToken(str) && str.matches("^[a-zA-Z_]+[a-zA-Z0-9_]*");
    }

    /**
     * Checks if token is a valid Identifier Token
     *
     * @param token token to check if it is an Identifier Token
     * @return true if token is an Identifier Token, false otherwise
     */
    public static boolean isToken(Token token) {
        return token.getToken().equals("$id") && IdentifierToken.isToken(token.getValue());
    }

    public static boolean isDelim(String buf, int nextCh) {
        String nextStr = Character.toString((char) nextCh);
        return (Token.isDelim(buf, nextCh) || LiteralToken.isToken(nextStr)) && !KeywordToken.isToken(buf+nextStr);
    }
}

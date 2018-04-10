package Tokenizer.Tokens;

import Compiler.Location;

public class KeywordToken extends Token {
    public KeywordToken(String token, Location loc) {
        super(token, loc);
    }

    /**
     * Checks if ch is a valid Keyword Token
     *
     * @param str string to check if it is an Keyword Token
     * @return true if ch is an Keyword Token, false otherwise
     */
    public static boolean isToken(String str) {
        switch (str) {
            case "bool":
            case "break":
            case "case":
            case "continue":
            case "default":
            case "do":
            case "else":
            case "false":
            case "float":
            case "if":
            case "return":
            case "signed":
            case "static":
            case "struct":
            case "switch":
            case "true":
            case "unsigned":
            case "var":
            case "void":
            case "while":
                return true;
            default:
                return false;
        }
    }

    /**
     * Checks if token is a valid Keyword Token
     *
     * @param token token to check if it is a Keyword Token
     * @return true if token is a Keyword Token, false otherwise
     */
    public static boolean isToken(Token token) {
        return KeywordToken.isToken(token.getValue());
    }

    public static boolean isDelim(String buf, int nextCh) {
        String nextStr = Character.toString((char) nextCh);
        return (Token.isDelim(buf, nextCh) || LiteralToken.isToken(nextStr)) && !KeywordToken.isToken(buf+nextStr);
    }
}

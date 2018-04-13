package Tokenizer.Tokens;

import Compiler.Location;

/**
 * Base Token class for the compiler
 */
public class Token implements Comparable<Token> {
    private String token;
    private String value;
    private Location loc;

    public Token(String token, Location loc) {
        this(token, "", loc);
    }

    public Token(String token, String value, Location loc) {
        this.token = token;
        this.value = value;
        this.loc = loc;
    }

    public String getValue() {
        if (value.length() > 0) {
            return value;
        }
        return token;
    }

    public String getToken() {
        return token;
    }

    public Location getLoc() {
        return loc;
    }

    public String getInfoStr() {
        StringBuilder str = new StringBuilder();
        str.append(loc.getPath());
        str.append(":");
        str.append(loc.getLineCount());
        str.append(":");
        str.append(loc.getCharCount());
        str.append(":");
        str.append(token);

        if (value.length() > 0) {
            str.append(":");
            str.append(value);
        }
        return str.toString();
    }

    @Override
    public String toString() {
        return getValue();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Token) {
            return equals((Token) obj);
        }
        return false;
    }

    public boolean equals(Token token) {
        return getValue().equals(token.getValue());
    }

    @Override
    public int hashCode() {
        return getValue().hashCode();
    }

    @Override
    public int compareTo(Token o) {
        return getValue().compareTo(o.getValue());
    }

    public static boolean isDelim(String buf, int nextCh) {
        return EOFToken.isToken(nextCh) || Character.isWhitespace(nextCh) || IllChrToken.isToken(Character.toString((char) nextCh));
    }

    public static boolean isComment(String str) {
        return str.equals("//");
    }
}

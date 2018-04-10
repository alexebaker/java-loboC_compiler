package Compiler;

import Parser.TokenParser;

public class LCC {
    CompilerState cs;

    public LCC(CompilerState cs) {
        this.cs = cs;
    }

    public void compile() {
        TokenParser tp = new TokenParser(cs);
        cs.setAST(tp.parse());
    }
}

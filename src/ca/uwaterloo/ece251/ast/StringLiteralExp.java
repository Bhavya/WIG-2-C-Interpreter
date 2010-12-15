package ca.uwaterloo.ece251.ast;

import java.util.List;

/** Literal for Strings. */
public class StringLiteralExp extends Exp {
    String v;

    public StringLiteralExp(String v) { 
	this.v = v; 
    }

    public String toString() {
	return "\""+this.v+"\"";
    }

    public void accept(ASTVisitor v) {
	v.enter(this);
	v.leave(this);
    }
}

package ca.uwaterloo.ece251.ast;

/** Represents an attr which is a String. */
public class StringAttr extends Attr {
    String s;

    public StringAttr(String s) { this.s = s; }
    public String toString() { return s; }

    public void accept(ASTVisitor v) {
	v.enter(this);
	v.leave(this);
    }
}

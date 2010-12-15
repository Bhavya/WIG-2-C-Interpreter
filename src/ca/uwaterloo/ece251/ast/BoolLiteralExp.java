package ca.uwaterloo.ece251.ast;

/** Literal for booleans. */
public class BoolLiteralExp extends Exp {
    public boolean v;

    public BoolLiteralExp(boolean v) {
	this.v = v;
    }

    public String toString() {
	return v ? "true" : "false";
    }

    public void accept(ASTVisitor v) {
	v.enter(this);
	v.leave(this);
    }
}

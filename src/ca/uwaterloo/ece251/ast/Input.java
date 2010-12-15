package ca.uwaterloo.ece251.ast;

/** Represents an input to a document. */
public class Input implements ASTNode {
    public Lvalue lhs;
    public Id rhs;

    public Input(Lvalue lhs, Id rhs) {
	this.lhs = lhs; this.rhs = rhs;
    }

    public String toString() {
	return lhs.toString() + " = " + rhs.toString();
    }

    public void accept(ASTVisitor v) {
	v.enter(this);
	lhs.accept(v);
	v.leave(this);
    }
}

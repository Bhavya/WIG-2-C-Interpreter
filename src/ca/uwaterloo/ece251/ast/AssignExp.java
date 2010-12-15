package ca.uwaterloo.ece251.ast;

/** Assignment expression. */
public class AssignExp extends Exp {
    public Lvalue lhs;
    public Exp rhs;

    public AssignExp(Lvalue lhs, Exp rhs) {
	this.lhs = lhs; this.rhs = rhs;
    }

    public String toString() {
	return String.format("%s = %s", lhs, rhs);
    }

    public void accept(ASTVisitor v) {
	v.enter(this);
	lhs.accept(v);
	rhs.accept(v);
	v.leave(this);
    }
}

package ca.uwaterloo.ece251.ast;

/** Represents an eval statement, which evaluates an expression. */
public class EvalStm extends Stm {
    public Exp c;

    public EvalStm(Exp c) { this.c = c; }
    public String toString() { 
	return (c == null ? "<null>" : c.toString()) + ";";
    }

    public void accept(ASTVisitor v) {
	v.enter(this);
	c.accept(v);
	v.leave(this);
    }
}


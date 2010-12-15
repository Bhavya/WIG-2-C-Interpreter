package ca.uwaterloo.ece251.ast;

/** Represents a while statement. */
public class WhileStm extends Stm {
    public Exp c;
    public Stm body;

    public WhileStm(Exp c, Stm body) {
	this.c = c; this.body = body;
    }

    public String toString() {
	return String.format("while (%s) %s", 
			     c.toString(), body.toString());
    }

    public void accept(ASTVisitor v) {
	v.enter(this);
	c.accept(v);
	body.accept(v);
	v.leave(this);
    }
}


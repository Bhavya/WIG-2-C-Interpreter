package ca.uwaterloo.ece251.ast;

/** Represents the do-nothing statement. */
public class EmptyStm extends Stm {
    public EmptyStm() {}
    public String toString() { return ";"; }

    public void accept(ASTVisitor v) {
	v.enter(this);
	v.leave(this);
    }
}


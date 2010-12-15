package ca.uwaterloo.ece251.ast;

/** Represents a conditional statement. */
public class IfStm extends Stm {
    public Exp c;
    public Stm thenBranch;
    public Stm elseBranch;

    public IfStm(Exp c, Stm thenBranch, Stm elseBranch) {
	this.c = c;
	this.thenBranch = thenBranch;
	this.elseBranch = elseBranch;
    }

    public String toString() {
	String tb = (thenBranch instanceof CompoundStm) ?
	    thenBranch.toString() : "{ "+thenBranch.toString()+" }";
	String eb = elseBranch == null ? "" :
	    ("else " + ((elseBranch instanceof CompoundStm) ?
			elseBranch.toString() : "{ "+elseBranch.toString()+" }"));
	return 
	     String.format("if (%s) %s%s", c.toString(), tb, eb);
    }

    public void accept(ASTVisitor v) {
	v.enter(this);
	c.accept(v);
	thenBranch.accept(v);
	if (elseBranch != null) elseBranch.accept(v);
	v.leave(this);
    }
}


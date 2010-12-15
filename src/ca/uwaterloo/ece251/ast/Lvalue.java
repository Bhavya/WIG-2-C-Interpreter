package ca.uwaterloo.ece251.ast;

public class Lvalue extends Exp {
    public Id qualifier; /* optional */
    public Id id;

    public Lvalue(Id qualifier, Id id) {
	this.qualifier = qualifier;
	this.id = id;
    }

    public String toString() {
	StringBuffer sb = new StringBuffer();

	if (this.qualifier != null) {
	    sb.append(this.qualifier);
	    sb.append(".");
	}
	sb.append(this.id);
	return sb.toString();
    }

    public void accept(ASTVisitor v) {
	v.enter(this);
	v.leave(this);
    }
}

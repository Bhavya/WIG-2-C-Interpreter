package ca.uwaterloo.ece251.ast;

/** Represents a field. */
public class WigField implements ASTNode {
    public Type t;
    public Id id;

    public WigField(Type t, Id id) {
	this.t = t;
	this.id = id;
    }

    public String toString() {
	return t + " " + id + ";";
    }

    public void accept(ASTVisitor v) {
	v.enter(this);
	t.accept(v);
	v.leave(this);
    }

    public int hashCode() {
	return id.hashCode();
    }

    public boolean equals(Object o) {
	if (!(o instanceof WigField)) return false;
	WigField ff = (WigField)o;
	return t.equals(ff.t) && id.equals(ff.id);
    }
}

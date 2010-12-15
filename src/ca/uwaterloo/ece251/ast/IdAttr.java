package ca.uwaterloo.ece251.ast;

/** Represents an attr which is an ID. */
public class IdAttr extends Attr {
    Id id;

    public IdAttr(Id id) { this.id = id; }
    public String toString() {
	return id.toString();
    }

    public void accept(ASTVisitor v) {
	v.enter(this);
	v.leave(this);
    }
}

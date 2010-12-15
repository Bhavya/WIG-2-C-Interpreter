package ca.uwaterloo.ece251.ast;

/** Represents a document to send as-is. */
public class BaseDocument extends Document {
    public BaseDocument(Id id) {
	super(id);
    }
    public String toString() {
	return id.toString();
    }

    public void accept(ASTVisitor v) {
	v.enter(this);
	v.leave(this);
    }
}

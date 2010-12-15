package ca.uwaterloo.ece251.ast;

/** Class for closing a tag. */
public class CloseHTMLBody extends NamedHTMLBody {
    public CloseHTMLBody(Id id) { super(id); }
    public String toString() {
	return "</"+id.toString()+">";
    }

    public void accept(ASTVisitor v) {
	v.enter(this);
	super.accept(v);
	v.leave(this);
    }
}

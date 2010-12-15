package ca.uwaterloo.ece251.ast;

/* Class representing a plug (to be filled in with data). */
public class PlugHTMLBody extends NamedHTMLBody {
    public PlugHTMLBody(Id id) { super(id); }
    public String toString() {
	return String.format("<[%s]>", id.toString());
    }

    public void accept(ASTVisitor v) {
	v.enter(this);
	v.leave(this);
    }
}

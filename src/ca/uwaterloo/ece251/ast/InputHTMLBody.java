package ca.uwaterloo.ece251.ast;

import java.util.List;

/** Represents an input tag. */
public class InputHTMLBody extends HTMLBody {
    public InputHTMLBody(List<Attribute> attrs) {
	super(attrs);
    }

    public String toString() {
	return String.format("<input%s>",
			     Util.join(attrs, " "));
    }

    public void accept(ASTVisitor v) {
	v.enter(this);
	super.accept(v);
	v.leave(this);
    }
}

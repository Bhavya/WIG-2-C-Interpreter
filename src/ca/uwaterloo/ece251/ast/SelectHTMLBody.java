package ca.uwaterloo.ece251.ast;

import java.util.List;

/** Represents a select tag. */
public class SelectHTMLBody extends HTMLBody {
    List<HTMLBody> contents;

    public SelectHTMLBody(List<Attribute> attrs, List<HTMLBody> contents) {
	super(attrs);
	this.contents = contents;
    }

    public String toString() {
	return String.format("<select%s>%s</select>",
			     Util.join(attrs, " "),
			     Util.join(contents, " "));
    }

    public void accept(ASTVisitor v) {
	v.enter(this);
	super.accept(v);
	for (HTMLBody c : contents) c.accept(v);
	v.leave(this);
    }
}

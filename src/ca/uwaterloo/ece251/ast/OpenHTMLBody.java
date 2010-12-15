package ca.uwaterloo.ece251.ast;

import java.util.List;

/* Class for opening a tag, with its attributes. */
public class OpenHTMLBody extends NamedHTMLBody {
    List<Attribute> attributes;

    public OpenHTMLBody(Id id, List<Attribute> attributes) {
	super(id);
	this.attributes = attributes;
    }

    public String toString() {
	StringBuffer attrString = new StringBuffer();
	for (Attribute attr : attributes)
	    attrString.append(" "+attr.toString());
	return String.format("<%s%s>", id.toString(),
			     attrString);
    }

    public void accept(ASTVisitor v) {
	v.enter(this);
	super.accept(v);
	v.leave(this);
    }
}

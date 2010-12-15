package ca.uwaterloo.ece251.ast;

import java.util.List;
import java.util.Collections;

/** Represents a part of the contents of an HTML template. */
abstract public class HTMLBody implements ASTNode {
    public List<Attribute> attrs;

    public HTMLBody(List<Attribute> attrs) {
	this.attrs = attrs;
    }

    public HTMLBody() { this.attrs = Collections.emptyList(); }

    public void accept(ASTVisitor v) {
	for (Attribute attr : attrs)
	    attr.accept(v);
	// subclasses must include v.enter(this)/v.leave(this) explicitly!
    }
}





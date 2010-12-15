package ca.uwaterloo.ece251.ast;

import java.util.List;

/** Represents a document to send with variable substitutions. */
public class PlugDocument extends Document {
    public List<Id> plugIds;
    public List<Exp> plugContents;

    public PlugDocument(Id id, List<Id> plugIds, List<Exp> plugContents) {
	super(id);
	this.plugIds = plugIds;
	this.plugContents = plugContents;
    }

    public String toString() {
	return String.format("plug %s[%s]", id.toString(),
			     Util.interleave(plugIds, plugContents, "=", ", "));
    }

    public void accept(ASTVisitor v) {
	v.enter(this);
	for (Exp e : plugContents) e.accept(v);
	v.leave(this);
    }
}

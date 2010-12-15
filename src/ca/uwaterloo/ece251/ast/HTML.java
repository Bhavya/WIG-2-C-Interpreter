package ca.uwaterloo.ece251.ast;

import java.util.List;
import java.util.LinkedList;

/** Represents one HTML template. */
public class HTML implements ASTNode {
    public Id id;
    List<HTMLBody> htmlbody;

    public HTML(Id id, List<HTMLBody> htmlbody) {
	this.id = id;
	this.htmlbody = htmlbody;
    }

    public String toString() {
	return String.format("const html %s = <html>%s</html>;",
			     id.toString(),
			     Util.join(htmlbody, ""));
    }

    public List<Id> plugs() {
	final List<Id> ids = new LinkedList<Id>();
	this.accept(new DefaultVisitor() {
		public void leave(PlugHTMLBody b) {
		    ids.add(b.id);
		}
	    });
	return ids;
    }

    public void accept(ASTVisitor v) {
	v.enter(this);
	for (HTMLBody hb : htmlbody) hb.accept(v);
	v.leave(this);
    }
}

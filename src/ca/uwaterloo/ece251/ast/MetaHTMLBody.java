package ca.uwaterloo.ece251.ast;

/** Represents a meta tag in an HTML body. */
public class MetaHTMLBody extends HTMLBody {
    String contents;

    public MetaHTMLBody(String contents) {
	this.contents = contents;
    }

    public String toString() {
	return String.format("%s", contents);
    }

    public void accept(ASTVisitor v) {
	v.enter(this);
	super.accept(v);
	v.leave(this);
    }
}

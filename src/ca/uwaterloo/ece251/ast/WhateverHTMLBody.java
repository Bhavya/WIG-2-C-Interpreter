package ca.uwaterloo.ece251.ast;

/** Represents anything else in an HTML body. */
public class WhateverHTMLBody extends HTMLBody {
    public String contents;

    public WhateverHTMLBody(String contents) {
	this.contents = contents;
    }

    public String toString() {
	return contents;
    }

    public void accept(ASTVisitor v) {
	v.enter(this);
	v.leave(this);
    }
}

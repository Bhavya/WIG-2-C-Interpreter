package ca.uwaterloo.ece251.ast;

/** Abstract superclass for tags with names. */
abstract public class NamedHTMLBody extends HTMLBody {
    public Id id;
    NamedHTMLBody(Id id) { this.id = id; }
}

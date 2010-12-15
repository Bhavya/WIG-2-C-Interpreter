package ca.uwaterloo.ece251.ast;

/** Represents a document, either by name or with plugs. */
abstract public class Document implements ASTNode {
    public Id id;
    Document(Id id) { this.id = id; }
}

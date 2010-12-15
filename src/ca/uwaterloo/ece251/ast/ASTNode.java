package ca.uwaterloo.ece251.ast;

public interface ASTNode {
    void accept (ASTVisitor visitor);
}

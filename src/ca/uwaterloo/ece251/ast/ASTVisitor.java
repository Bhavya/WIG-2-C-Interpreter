package ca.uwaterloo.ece251.ast;

public interface ASTVisitor {
    void enter(Service s);
    void enter(HTML h);
    void enter(Schema s);
    void enter(Variable v);
    void enter(Function f);
    void enter(Session s);
    void enter(OpenHTMLBody s);
    void enter(InputHTMLBody s);
    void enter(CloseHTMLBody s);
    void enter(MetaHTMLBody s);
    void enter(SelectHTMLBody s);
    void enter(WhateverHTMLBody s);
    void enter(PlugHTMLBody s);
    void enter(KeyValueAttribute a);
    void enter(KeyOnlyAttribute a);
    void enter(WigField f);
    void enter(IdAttr a);
    void enter(StringAttr a);
    void enter(BaseDocument d);
    void enter(PlugDocument d);
    void enter(EmptyStm s);
    void enter(EvalStm s);
    void enter(CompoundStm s);
    void enter(ExitStm s);
    void enter(IfStm s);
    void enter(ReturnStm s);
    void enter(ReturnVoidStm s);
    void enter(ShowStm s);
    void enter(WhileStm s);
    void enter(Input i);
    void enter(AssignExp ae);
    void enter(BinopExp be);
    void enter(BoolLiteralExp be);
    void enter(CallExp ce);
    void enter(IntLiteralExp ie);
    void enter(StringLiteralExp se);
    void enter(TupleLiteralExp te);
    void enter(TupleopExp te);
    void enter(UnopExp ue);
    void enter(Lvalue lv);
    void enter(SimpleType st);
    void enter(TupleType tt);

    void leave(Service s);
    void leave(HTML h);
    void leave(Schema s);
    void leave(Variable v);
    void leave(Function f);
    void leave(Session s);
    void leave(OpenHTMLBody s);
    void leave(InputHTMLBody s);
    void leave(CloseHTMLBody s);
    void leave(MetaHTMLBody s);
    void leave(SelectHTMLBody s);
    void leave(WhateverHTMLBody s);
    void leave(PlugHTMLBody s);
    void leave(KeyValueAttribute a);
    void leave(KeyOnlyAttribute a);
    void leave(WigField f);
    void leave(IdAttr a);
    void leave(StringAttr a);
    void leave(BaseDocument d);
    void leave(PlugDocument d);
    void leave(EmptyStm s);
    void leave(EvalStm s);
    void leave(CompoundStm s);
    void leave(ExitStm s);
    void leave(IfStm s);
    void leave(ReturnStm s);
    void leave(ReturnVoidStm s);
    void leave(ShowStm s);
    void leave(WhileStm s);
    void leave(Input i);
    void leave(AssignExp ae);
    void leave(BinopExp be);
    void leave(BoolLiteralExp be);
    void leave(CallExp ce);
    void leave(IntLiteralExp ie);
    void leave(StringLiteralExp se);
    void leave(TupleLiteralExp te);
    void leave(TupleopExp te);
    void leave(UnopExp ue);
    void leave(Lvalue lv);
    void leave(SimpleType st);
    void leave(TupleType tt);
}
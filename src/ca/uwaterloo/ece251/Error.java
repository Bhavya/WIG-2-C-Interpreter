package ca.uwaterloo.ece251;

public class Error {
	public static final String S01 = "[S01]: undefined schema ";
	public static final String S02 = "[S02]: undefined HTML: ";
	public static final String S03 = "[S03]: undefined variable name: ";
	public static final String S04 = "[S04]: undefined function: ";
	public static final String S05 = "[S05]: multiply-defined schema: ";
	public static final String S06 = "[S06]: multiply-defined field name: ";
	public static final String S07 = "[S07]: multiply-defined variable: ";
	public static final String S08 = "[S08]: multiply-defined html: ";
	public static final String S09 = "[S09]: multiply-defined session: ";
	public static final String S10 = "[S10]: multiply-defined function: ";
	
	public static final String T01 = "[T01]: not on non-boolean expression ";
	public static final String T02 = "[T02]: negation on non-int expression ";
	public static final String T03 = "[T03]: arg type for arg N in call to F doesn't match: expected T1, got T2";
	public static final String T04 = "[T04]: wrong number of args";
	public static final String T05 = "[T05]: non-boolean types for ";
	public static final String T06 = "[T06]: non-int types for ";
	public static final String T07 = "[T07]: non-int, string types for ";
	public static final String T08 = "[T08]: plug doesn't have simple non-void type: ";
	public static final String T09 = "[T09]: non-equal types for assignment ";
	public static final String T10 = "[T10]: mismatched return type for expression ";
	public static final String T11 = "[T11]: returning void from non-void function";
	public static final String T12 = "[T12]: if statement has non-boolean condition ";
	public static final String T13 = "[T13]: while statement has non-boolean condition ";
	public static final String T14 = "[T14]: return statement outside function";
	public static final String T15 = "[T15]: undefined field F in schems S";
	public static final String T16 = "[T16]: attempt to use << on non tuple types ";
	public static final String T17 = "[T17]: no matching schema found for literal ";
	public static final String T18 = "[T18]: tuple operation OP gives nonexistent schema";
	
    public static boolean errors = false;
    public static void error(String s) {
	System.out.println("error: "+s);
	errors = true;
    }

    public static void fatalerror(String s) {
	System.out.println("fatal error: "+s);
	errors = true;
	System.exit(-1);
    }
}

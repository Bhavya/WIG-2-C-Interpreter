package ca.uwaterloo.ece251.ast;

import java.util.List;
import java.util.Iterator;
import java.util.regex.*;

/** Utility functions. */
public class Util {
    static int tabLevel = 0;
    public static void indent(int i) {
	tabLevel += i;
    }
    public static String tab() {
	char[] s = new char[tabLevel];
	for (int i = 0; i < tabLevel; i++)
	    s[i] = ' ';
	return new String(s);
    }

    public static String lines(List<?> l) {
	StringBuffer sb = new StringBuffer();
	for (Object o : l) {
	    sb.append(Util.tab());
	    sb.append(o == null ? "<null>" : o.toString());
	    sb.append("\n");
	}
	if (l.size() > 0) sb.append("\n");
	return sb.toString();
    }

    public static String join(List<?> l, String sep) {
	if (l == null) return "";
	StringBuffer sb = new StringBuffer();
	for (Object o : l) {
	    sb.append(sep + (o == null ? "<null>" : o.toString()));
	}
	return sb.toString();
    }

    public static String commaSeparated(List<?> l) {
	StringBuffer sb = new StringBuffer();
	Iterator<?> it = l.iterator();
	while (it.hasNext()) {
	    sb.append(it.next().toString());
	    if (it.hasNext()) sb.append(", ");
	}
	return sb.toString();
    }

    public static String interleave(List<?> a, List<?> b, String abSep, String pairSep) {
	Iterator it1 = a.iterator(), it2 = b.iterator();
	StringBuffer sb = new StringBuffer();
	while (it1.hasNext()) {
	    sb.append(it1.next().toString() + abSep + it2.next().toString());
	    if (it1.hasNext()) sb.append(pairSep);
	}
	return sb.toString();
    }

    public static String escape(String s) {
	String ss = s;
	ss = ss.replaceAll("\n", "\\\\n");
	ss = ss.replaceAll("\"", "\\\\\"");
	return ss;
    }

    static Pattern p = Pattern.compile("( |\n)*");
    public static boolean whitespaceOnly(String s) {
	Matcher m = p.matcher(s);
	return m.matches();
    }
}

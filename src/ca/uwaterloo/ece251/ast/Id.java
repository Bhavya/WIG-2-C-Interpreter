package ca.uwaterloo.ece251.ast;

/** Represents an identifier. These go into symbol tables. */
public class Id implements Comparable {
    String s;

    public Id(String s) { this.s = s; }
    public String toString() { return s; }

    public int hashCode() {
	return s.hashCode();
    }

    public boolean equals(Object o) { 
	return (o instanceof Id) && ((Id)o).s.equals(this.s);
    }

    public int compareTo(Object o) {
	return s.compareTo(((Id)o).s);
    }
}

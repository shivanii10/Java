package Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
public class SetExample {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		System.out.println("HashSet: ");
		Set<String> hs = new HashSet();
		hs.add("India");
		hs.add("Japan");
		hs.add("China");
		hs.add("India");
		System.out.println(hs);
		System.out.println();
		
		System.out.println("LinkedHashSet: ");
		LinkedHashSet lhs = new LinkedHashSet();
		lhs.add("India");
		lhs.add("Japan");
		lhs.add("China");
		lhs.add("India");
		System.out.println(lhs);
		System.out.println();
		
		System.out.println("TreeSet: ");
		TreeSet ts = new TreeSet();
		ts.add("India");
		ts.add("Japan");
		ts.add("China");
		ts.add("India");
		System.out.println(ts);
		System.out.println();

	}

}
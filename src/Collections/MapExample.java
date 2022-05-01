package Collections;
import java.util.*;
import java.util.Map.Entry;
public class MapExample {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		System.out.println("HashMap:");
		Map<Integer,String> m = new HashMap();
		m.put(1001, "India");
		m.put(4005, "Japan");
		m.put(2857, "Nepal");
		System.out.println(m);
		m.putIfAbsent(1001, "America"); //will check then insert
		System.out.println("HashMap after Updation:");
		m.put(1001, "America"); // will update value present on key
		System.out.println(m);
		
		System.out.println("HashMap after Incorrect value:");
		m.remove(2857,"Nepa");
		//m.remove(2857); will remove nepal
		System.out.println(m);
		
		System.out.println("HashMap after removal:");
		m.remove(2857,"Nepal");
		System.out.println(m);
		
		System.out.println("KEYS:");
		System.out.println(m.keySet());
		System.out.println("VALUES:");
		System.out.println(m.values());
		
		if(m.containsKey(2856))
			System.out.println("Key is found");
		else
			System.out.println("Key not found");
		
		if(m.containsValue("Japan"))
			System.out.println("value is found");
		else
			System.out.println("value not found");
		
		
		System.out.println("SET: ");
		//Key-Value pair is called as entry
		Set set=m.entrySet();//Converting to Set so that we can traverse  
		System.out.println(set);
		System.out.println("The iteration of map is ");
		Iterator itr=set.iterator();  
	    while(itr.hasNext()){  
	        //Converting to Map.Entry so that we can get key and value separately  
	        Map.Entry entry=(Map.Entry)itr.next();  
	        System.out.println(entry.getKey()+" "+entry.getValue());  
	    } 
		
		
		
	}

}
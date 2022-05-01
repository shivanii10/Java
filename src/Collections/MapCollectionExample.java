package Collections;
import java.util.*;
public class MapCollectionExample {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Map<String,List> dists = new HashMap();
			List<String> dist1 = new ArrayList();
			dist1.add("adl");
			dist1.add("arr");
			dist1.add("ngl");
			
			List<String> dist2 = new ArrayList();
			dist2.add("Mandya");
			dist2.add("kolar");
			dist2.add("Hubli");
			
			dists.put("TS", dist1);
			dists.put("KA", dist2);
			
			System.out.println(dists);
			
			System.out.println("KEYS:");
			System.out.println(dists.keySet());
			System.out.println("VALUES:");
			System.out.println(dists.values());
			
			Set set=dists.entrySet();//Converting to Set so that we can traverse  
		    Iterator itr=set.iterator();  
		    while(itr.hasNext()){  
		    //Converting to Map.Entry so that we can get key and value separately  
		    	Map.Entry entry=(Map.Entry)itr.next();   
		    	System.out.println("State: ");
		    	System.out.println(entry.getKey());
		    	System.out.println("The districts are: ");
		    	List<String> temps = (List<String>) entry.getValue();  
		    	temps.forEach(System.out::println);
		    
		    } 
		    
		    //Searching districts starting from "a"
		    Set set1=dists.entrySet();//Converting to Set so that we can traverse  
		    Iterator itre=set.iterator();  
		    List<String> Searchdist = new ArrayList();
		    while(itre.hasNext()){  
			    //Converting to Map.Entry so that we can get key and value separately  
			    	Map.Entry entry=(Map.Entry)itre.next();   
			    	List<String> temps = (List<String>) entry.getValue();
			    	for(String t: temps)
			    	{
			    		if (t.startsWith("a"))
			    			Searchdist.add(t);
			    	}
			    
			    } 
		    System.out.println("districts starting from 'a' :");
		    System.out.println(Searchdist);
	}
}
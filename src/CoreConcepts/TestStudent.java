package CoreConcepts;

public class TestStudent {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Student s= new Student();
		s.setName("shreya");
		s.setRoll_no(56);
		s.marks=89;
		s.displayData();
		//s.p_name="sadhaya"; // this will display error as p_name is private.
		System.out.println(s.getName()+" "+s.getRoll_no());
	}
	
}

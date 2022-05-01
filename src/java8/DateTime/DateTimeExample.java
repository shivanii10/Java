package java8.DateTime;

import java.time.*;

public class DateTimeExample {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LocalDate date = LocalDate.now();
		LocalTime time = LocalTime.now();
		System.out.println(date);
		System.out.println(time);
		
		System.out.println(date.getMonthValue());
		System.out.println(date.getDayOfMonth());
		System.out.println(date.getYear());
		System.out.println(date.plusDays(1));
		System.out.println(date.minusYears(1));
		
		System.out.println(time.getHour());
		System.out.println(time.getMinute());
		System.out.println(time.getSecond());
		
		ZoneId zone = ZoneId.systemDefault();
		System.out.println(zone); 
		
		ZoneId la = ZoneId.of("America/Los_Angeles");
		ZonedDateTime zt = ZonedDateTime.now(la);
		System.out.println(zt);
		
		LocalDate today = LocalDate.now();
		LocalDate birthday = LocalDate.of(2000,10,10);
		
		Period p = Period.between(birthday,today);
		System.out.printf("age is %d year %d months %d days",p.getYears(),p.getMonths(),p.getDays());

		

	}

}
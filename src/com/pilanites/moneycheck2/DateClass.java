package com.pilanites.moneycheck2;

import java.util.Calendar;
import java.util.Date;

public class DateClass {
	static long curr;
	static Calendar calendar;
	
	public static String getDate(long time) {
		Date d = new Date(time);
		return d.toLocaleString(); 
	}
	
	public static long getMonth() {
		curr = System.currentTimeMillis();
		
		calendar = Calendar.getInstance();
		calendar.setTimeInMillis(curr);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}
	
	public static long getYesterday() {
		curr = System.currentTimeMillis();
		
		calendar = Calendar.getInstance();
		calendar.setTimeInMillis(curr);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.MILLISECOND, -1);
		return calendar.getTimeInMillis();
	}
	
	public static long getToday() {
		curr = System.currentTimeMillis();
		
		calendar = Calendar.getInstance();
		calendar.setTimeInMillis(curr);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}
	
	public static long getBefore(int days){
		curr = System.currentTimeMillis();
		
		calendar = Calendar.getInstance();
		calendar.setTimeInMillis(curr);
		calendar.add(Calendar.DAY_OF_MONTH, days);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}
}

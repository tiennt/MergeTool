package com.hailv.mergetool.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Record8Data {
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss:SSS";
	private static SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
	
	long time;
	String data;
	
	public Record8Data(String source, long diff) {
		
		try {
			int firstCommaIndex = source.indexOf(",");
			String timeStr = source.substring(0, firstCommaIndex - 1);
			time = sdf.parse(timeStr).getTime() - diff;
			
			data = source.substring(firstCommaIndex + 1);
			
//			System.out.println("Time: " + sdf.format(time) + "   DAta: " + data);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public long getTime() {
		return time;
	}

	public String getData() {
		return data;
	}
}

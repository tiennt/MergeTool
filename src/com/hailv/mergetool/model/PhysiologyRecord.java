package com.hailv.mergetool.model;

public class PhysiologyRecord {
	long time; // Time from startTime of Physiology file.
	String data;
	
	public PhysiologyRecord(String raw) {
		int firstCommaIndex = raw.indexOf(",");
		String t = raw.substring(0, firstCommaIndex);
		time = (int)(Float.parseFloat(t) * 1000);
	}

	public long getTime() {
		return time;
	}

	public String getData() {
		return data;
	}
	
	
}

package com.hailv.mergetool.model;

public class PhysiologyRecord {
	long time; // Time from startTime of Physiology file.
	String rawTime;
	String data;
	
	public PhysiologyRecord(String raw) {
		int firstCommaIndex = raw.indexOf(",");
		rawTime = raw.substring(0, firstCommaIndex);
		data = raw.substring(firstCommaIndex + 1);
		time = (int)(Float.parseFloat(rawTime) * 1000);
	}

	public long getTime() {
		return time;
	}

	public String getRawTime() {
		return rawTime;
	}
	
	public String getData() {
		return data;
	}
}

package com.hailv.mergetool.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.dd.plist.NSDictionary;

public class Record {
	// 2015-05-18 17:24:23.5890
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	private static SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
	private long baselineStart;
	private long baselineEnd;
	private long trackStart;
	private long trackEnd;
	private String trackName;
	
	public Record(NSDictionary dict) {
		String bs = dict.objectForKey("Baseline Start").toString();
		String be = dict.objectForKey("Baseline End").toString();
		String ts = dict.objectForKey("Track Start").toString();
		String te = dict.objectForKey("Track End").toString();
		
		trackName = dict.objectForKey("Track Name").toString();
		try {
			baselineStart = sdf.parse(bs.substring(0, bs.length() - 1)).getTime();
			baselineEnd = sdf.parse(be.substring(0, be.length() - 1)).getTime();
			trackStart = sdf.parse(ts.substring(0, ts.length() - 1)).getTime();
			trackEnd = sdf.parse(te.substring(0, te.length() - 1)).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public long getBaselineStart() {
		return baselineStart;
	}

	public long getBaselineEnd() {
		return baselineEnd;
	}

	public long getTrackStart() {
		return trackStart;
	}

	public long getTrackEnd() {
		return trackEnd;
	}

	public String getTrackName() {
		return trackName;
	}
	
	
}

package com.hailv.mergetool.model;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import com.dd.plist.NSDictionary;

public class Record8 {
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	private static SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT,
			Locale.ENGLISH);

	private long trackStart;
	private long trackEnd;
	private String trackName;
	private long diffTime;
	private String rawData;
	ArrayList<Record8Data> data;

	public Record8(NSDictionary dict) {
		String ts = dict.objectForKey("Track Start").toString();
		String te = dict.objectForKey("Track End").toString();

		trackName = dict.objectForKey("Track Name").toString();
		rawData = dict.objectForKey("Rating Data").toString();

		data = new ArrayList<Record8Data>();
		try {
			trackStart = sdf.parse(ts.substring(0, ts.length() - 1)).getTime();
			trackEnd = sdf.parse(te.substring(0, te.length() - 1)).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}

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

	public long getDiffTime() {
		return diffTime;
	}

	public void setDiffTime(long diffTime) {
		this.diffTime = diffTime;
	}

	public String getRawData() {
		return rawData;
	}

	public ArrayList<Record8Data> getData() {
		return data;
	}

	public void parseRec8Data() throws Exception {
		BufferedReader br = createReader(rawData);

		String d;
		while ((d = br.readLine()) != null) {
			Record8Data r = new Record8Data(d, diffTime);
			data.add(r);
		}

	}

	private BufferedReader createReader(String source) throws Exception {
		InputStream is = new ByteArrayInputStream(source.getBytes());
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		return br;
	}
}

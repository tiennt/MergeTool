package com.hailv.mergetool.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;

import com.hailv.mergetool.Utils;

public class PhysiologyFile {
	long startTime;
	BufferedReader br;

	public PhysiologyFile(String filePath) {
		br = new BufferedReader(new InputStreamReader(Utils.openFile(filePath)));
		try {
			readHeader();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void readHeader() throws Exception {
		// Skip 3 first lines.
		br.readLine();
		br.readLine();
		br.readLine();

		String sessionInfoRaw = br.readLine();

		int sessionDateIndex = sessionInfoRaw.indexOf("Session Date:");
		int sessionTimeIndex = sessionInfoRaw.indexOf("Session Time:");

		String t1 = sessionInfoRaw.substring(sessionDateIndex,
				sessionTimeIndex - 1);
		String t2 = sessionInfoRaw.substring(sessionTimeIndex);

		String sessionDateRaw = t1.substring("Session Date:".length()).trim();
		String sessionTimeRaw = t2.substring("Session Time:".length()).trim();

		String sessionDateTime = sessionDateRaw + " " + sessionTimeRaw;

		// 5/18/2015 5:56:08 PM
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
		long endTime = sdf.parse(sessionDateTime).getTime();

		String sessionDurationRaw = br.readLine();
		String durationString = sessionDurationRaw.substring(
				sessionDurationRaw.indexOf("End Time:") + "End Time:".length()
						+ 1).trim();

		String[] token = durationString.split(":");
		String[] token2 = token[2].split("\\.");
		long duration = Integer.parseInt(token[0]) * 3600
				+ Integer.parseInt(token[1]) * 60 + Integer.parseInt(token2[0]);
		duration *= 1000;
		duration += Integer.parseInt(token2[1]);

		startTime = endTime - duration;
		
		// Skip next 3 lines
		br.readLine();
		br.readLine();
		br.readLine();
	}

	public void moveToFrameAt(long t) throws Exception {
		long numofMili = t - startTime;
		long line = numofMili * 256 / 1000;
		System.out.println("PHY: " + line);
		for (int i = 0; i < line; i++) {
			br.readLine();
		}
	}

	public long getStartTime() {
		return startTime;
	}

	public PhysiologyRecord nextLine() throws Exception {
		return new PhysiologyRecord(br.readLine());
	}

	public void close() throws IOException {
		if (br != null) {
			br.close();
		}
	}
}

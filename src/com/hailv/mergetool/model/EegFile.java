package com.hailv.mergetool.model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;

import com.hailv.mergetool.Utils;

public class EegFile {
	BufferedReader br;
	long startTime;
	
	public EegFile(String filePath) {
		br = new BufferedReader(new InputStreamReader(Utils.openFile(filePath)));
		try {
			parseHeader();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void parseHeader() throws Exception {
		String header = br.readLine();
		String[] tokens = header.split(",");
		String recordRaw = tokens[1].trim();
		String recorded = recordRaw.substring("recorded:".length()).trim();
		
		// 18.05.20 17.21.42
		
		tokens = recorded.split(" ");
		String date = tokens[0] + "15";
		String time = tokens[1];
		
		recorded = date.trim() + " " + time.trim();
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH.mm.ss");
		startTime = sdf.parse(recorded).getTime();
		
		String firstLine = br.readLine();
		String firstTime = firstLine.substring(0, firstLine.indexOf("."));
		int frame = Integer.parseInt(firstTime);
		startTime += frame * 1000 / 128;
	}

	public void moveToFrameAt(long t) throws Exception {
		long numofMili = t - startTime;
		long line = numofMili * 128 / 1000;
		for (int i = 0; i < line - 1; i++) {
			br.readLine();
		}
	}
	
	public String nextLine() throws Exception{
		return br.readLine();
	}
	
	public long getStartTime() {
		return startTime;
	}
}

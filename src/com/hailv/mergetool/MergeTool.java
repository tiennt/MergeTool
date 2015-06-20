package com.hailv.mergetool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListParser;
import com.hailv.mergetool.model.EegFile;
import com.hailv.mergetool.model.PhysiologyFile;
import com.hailv.mergetool.model.PhysiologyRecord;
import com.hailv.mergetool.model.Record;

public class MergeTool {
	private static final String BASE_PATH = "/Users/tiennt/Jobs/hailv/new/";

	private static final String EEG_FILE = "eeg.csv";
	private static final String PHYSIO_FILE = "physio.csv";
	private static final String PLIST_FILE = "recorded_7.plist";
	private static final String OUTPUT_FILE = "out.csv";
	

	public void merge() throws Exception {

		NSArray plist = (NSArray) PropertyListParser.parse(Utils
				.openFile(BASE_PATH + PLIST_FILE));
		if (plist == null) {
			return;
		}

		// Process plist file.
		NSDictionary dic = (NSDictionary) plist.objectAtIndex(0);
		Record rec = new Record(dic);

		// Process phy file.
		PhysiologyFile physiology = new PhysiologyFile(BASE_PATH + PHYSIO_FILE);

		// Eeg
		EegFile eeg = new EegFile(BASE_PATH + EEG_FILE);
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
		System.out.println("REC: " + rec.getBaselineStart() + " " + sdf.format(rec.getBaselineStart()));
		System.out.println("EEG: " + eeg.getStartTime() + " " + sdf.format(eeg.getStartTime()));
		System.out.println("PHY: " + physiology.getStartTime() + " " + sdf.format(1431944543308L));
		
		physiology.moveToFrameAt(rec.getBaselineStart());
		eeg.moveToFrameAt(rec.getBaselineStart());
		
		long start = rec.getBaselineStart();
		long end = rec.getBaselineEnd();
		
		PrintWriter pr = new PrintWriter(new File(BASE_PATH + OUTPUT_FILE));
		
		while (true) {
			PhysiologyRecord record = physiology.nextLine();
			long time = physiology.getStartTime() + record.getTime();
			if (time > end) {
				break;
			}
			pr.write(sdf.format(time) + "\n");
		}
		pr.close();
	}
}

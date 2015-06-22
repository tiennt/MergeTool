package com.hailv.mergetool;

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

		PrintWriter pr = new PrintWriter(new File(BASE_PATH + OUTPUT_FILE));

//		test();
		
		for (int i = 0; i < plist.count(); i++) {
			NSDictionary dic = (NSDictionary) plist.objectAtIndex(i);
			Record rec = new Record(dic);
			 _merge(rec, pr);
		}

		pr.close();
	}

	private void _merge(Record rec, PrintWriter pr) throws Exception {
		// Process phy file.
		PhysiologyFile physiology = new PhysiologyFile(BASE_PATH + PHYSIO_FILE);

		// Eeg
		EegFile eeg = new EegFile(BASE_PATH + EEG_FILE);

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
		System.out.println("REC: " + rec.getBaselineStart() + " "
				+ sdf.format(rec.getBaselineStart()));
		System.out.println("EEG: " + eeg.getStartTime() + " "
				+ sdf.format(eeg.getStartTime()));
		System.out.println("PHY: " + physiology.getStartTime() + " "
				+ sdf.format(physiology.getStartTime()));

		physiology.moveToFrameAt(rec.getBaselineStart());
		eeg.moveToFrameAt(rec.getBaselineStart());

		long end = rec.getBaselineEnd();

		while (true) {
			PhysiologyRecord record = physiology.nextLine();
			String eeg1 = eeg.nextLine();

			long time = physiology.getStartTime() + record.getTime();
			if (time > end) {
				break;
			}
			pr.write("Baseline " + record.getRawTime() + " " + eeg1 + " "
					+ record.getData() + "\n");

			record = physiology.nextLine();
			time = physiology.getStartTime() + record.getTime();
			if (time > end) {
				break;
			}
			pr.write("Baseline " + record.getRawTime() + " " + eeg1 + " "
					+ record.getData() + "\n");
		}
		physiology.close();
		eeg.close();

		physiology = new PhysiologyFile(BASE_PATH + PHYSIO_FILE);
		eeg = new EegFile(BASE_PATH + EEG_FILE);
		physiology.moveToFrameAt(rec.getTrackStart());
		eeg.moveToFrameAt(rec.getTrackStart());

		end = rec.getTrackEnd();
		while (true) {
			PhysiologyRecord record = physiology.nextLine();
			String eeg1 = eeg.nextLine();

			long time = physiology.getStartTime() + record.getTime();
			if (time > end) {
				break;
			}
			pr.write("Track " + record.getRawTime() + " " + eeg1 + " "
					+ record.getData() + "\n");

			record = physiology.nextLine();
			time = physiology.getStartTime() + record.getTime();
			if (time > end) {
				break;
			}
			pr.write("Track " + record.getRawTime() + " " + eeg1 + " "
					+ record.getData() + "\n");
		}
	}

	private void test() throws Exception {
		// Process phy file.
		PhysiologyFile physiology = new PhysiologyFile(BASE_PATH + PHYSIO_FILE);
		// Eeg
		EegFile eeg = new EegFile(BASE_PATH + EEG_FILE);

		physiology.moveToFrameAt(physiology.getStartTime() + 35 * 60000);
		eeg.moveToFrameAt(eeg.getStartTime() + 35 * 60000);
		
		PhysiologyRecord rec = physiology.nextLine();
		
		System.out.println("PHY: " + rec.getRawTime() + " " + rec.getData());
		System.out.println("EEG: " + eeg.nextLine());
	}
}

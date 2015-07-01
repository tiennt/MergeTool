package com.hailv.mergetool;

import java.awt.List;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListParser;
import com.hailv.mergetool.model.EegFile;
import com.hailv.mergetool.model.PhysiologyFile;
import com.hailv.mergetool.model.PhysiologyRecord;
import com.hailv.mergetool.model.Record;
import com.hailv.mergetool.model.Record8;
import com.hailv.mergetool.model.Record8Data;

public class MergeTool {
	private static final String BASE_PATH = "/Users/tiennt/Jobs/hailv/new/";

	private static final String EEG_FILE = "eeg.csv";
	private static final String PHYSIO_FILE = "physio.csv";
	private static final String PLIST_FILE = "recorded_7.plist";
	private static final String PLIST8_FILE = "recorded_8.plist";
	private static final String OUTPUT_FILE = "out.csv";
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	private static final SimpleDateFormat SDF = new SimpleDateFormat(
			DATE_FORMAT, Locale.ENGLISH);

	public void merge() throws Exception {

		NSArray plist = (NSArray) PropertyListParser.parse(Utils
				.openFile(BASE_PATH + PLIST_FILE));
		if (plist == null) {
			return;
		}

		NSArray plist8 = (NSArray) PropertyListParser.parse(Utils
				.openFile(BASE_PATH + PLIST8_FILE));
		// System.out.println(data);
		ArrayList<Record8> r8 = new ArrayList<Record8>();
		for (int j = 0; j < plist8.count(); j++) {
			NSDictionary d = (NSDictionary) plist8.objectAtIndex(j);
			r8.add(new Record8(d));
		}

		// Process plist file.
		test2();
		PrintWriter pr = new PrintWriter(new File(BASE_PATH + OUTPUT_FILE));
		//
		// // test();
		//
		for (int i = 0; i < 1; i++) {
			NSDictionary dic = (NSDictionary) plist.objectAtIndex(i);
			Record rec = new Record(dic);
			String trackName = rec.getTrackName();
			Record8 rec8 = findPlist8Data(r8, trackName);
			System.out.println("Track name: " + trackName);
			long diff = rec8.getTrackStart() - rec.getTrackStart();
			rec8.setDiffTime(diff);
			rec8.parseRec8Data();

			System.out.println("Diff: " + diff);
			_merge(rec, rec8, pr);
		}
		//
		pr.close();
	}

	private Record8 findPlist8Data(ArrayList<Record8> source, String trackName) {
		Record8 result = null;
		for (int j = 0; j < source.size(); j++) {
			Record8 d = source.get(j);
			if (trackName.equalsIgnoreCase(d.getTrackName())) {
				result = d;
				break;
			}
		}
		return result;
	}

	private void _merge(Record rec, Record8 rec8, PrintWriter pr)
			throws Exception {
		_internalMerge("Baseline", rec.getBaselineStart(),
				rec.getBaselineEnd(), rec8, pr, false);
		_internalMerge("Track", rec.getTrackStart(), rec.getTrackEnd(), rec8,
				pr, true);
	}

	private void _internalMerge(String tag, long start, long end, Record8 rec8,
			PrintWriter pr, boolean isTrack) throws Exception {
		PhysiologyFile physiology = new PhysiologyFile(BASE_PATH + PHYSIO_FILE);
		EegFile eeg = new EegFile(BASE_PATH + EEG_FILE);
		physiology.moveToFrameAt(start);
		eeg.moveToFrameAt(start);

		int currentIndex = 0;
		int total = rec8.getData().size();
		while (true) {
//			Thread.sleep(10);
			PhysiologyRecord record = physiology.nextLine();
			String eeg1 = eeg.nextLine();

			long time = physiology.getStartTime() + record.getTime();

			if (time > end) {
				break;
			}

			Record8Data res = null;

			long maxTime = 1000000L;
			long delta = 0;

			if (isTrack) {
				for (int i = currentIndex; i < total; i++) {
					Record8Data d = rec8.getData().get(i);
					delta = d.getTime() - time;
					// System.out.println("Time: " + SDF.format(time));
					// System.out.println("Time 8: " + SDF.format(d.getTime()));
					// System.out.println("Delta: " + delta);
					if (delta > 0) {
						if (delta < maxTime) {
							maxTime = delta;
							currentIndex = i;
							res = d;
						} else {
							break;
						}
					} else {
						continue;
					}
				}
			}
			System.out.println("Current index: " + currentIndex);
			// System.out.println("Res: " + res.getData());
			// System.out.println("Delta: " + delta);

			if (res != null) {
				pr.write(String.format("%s %s %s %s %s\n", tag,
						SDF.format(time), res.getData(), eeg1, record.getData()));
			} else {
				pr.write(String.format("%s %s %s %s\n", tag, SDF.format(time),
						eeg1, record.getData()));
			}
			record = physiology.nextLine();
			time = physiology.getStartTime() + record.getTime();
			if (time > end) {
				break;
			}

			maxTime = 1000000L;
			res = null;
			delta = 0;

			if (isTrack) {

				for (int i = currentIndex; i < total; i++) {
					Record8Data d = rec8.getData().get(i);
					delta = d.getTime() - time;
					// System.out.println("Time: " + SDF.format(time));
					// System.out.println("Time 8: " + SDF.format(d.getTime()));
					// System.out.println("Delta: " + delta);
					if (delta > 0) {
						if (delta < maxTime) {
							maxTime = delta;
							currentIndex = i;
							res = d;
						} else {
							break;
						}
					} else {
						continue;
					}
				}
			}

			if (res != null) {
				pr.write(String.format("%s %s %s %s %s\n", tag,
						SDF.format(time), res.getData(), eeg1, record.getData()));
			} else {
				pr.write(String.format("%s %s %s %s\n", tag, SDF.format(time),
						eeg1, record.getData()));
			}
		}
		physiology.close();
		eeg.close();
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

	private void test2() throws ParseException {
		long te = SDF.parse("2015-05-18 22:48:26.994").getTime();
		long ts = SDF.parse("2015-05-18 22:44:46.868").getTime();
		System.out.println("Total: " + (te - ts));

		te = SDF.parse("2015-05-18 17:40:32.361").getTime();
		ts = SDF.parse("2015-05-18 17:36:51.923").getTime();
		System.out.println("Total: " + (te - ts));
	}
}

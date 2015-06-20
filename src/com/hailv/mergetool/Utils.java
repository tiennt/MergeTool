package com.hailv.mergetool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Utils {
	public static FileInputStream openFile(String file) {
		try {
			return new FileInputStream(new File(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}

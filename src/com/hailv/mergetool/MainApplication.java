package com.hailv.mergetool;


public class MainApplication {
	public static void main(String[] args) {
		MergeTool tool = new MergeTool();
		try {
			tool.merge();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

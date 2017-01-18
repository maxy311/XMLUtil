package com.wutian.maxy.standard;

import java.io.File;

import com.wutian.maxy.FrameStarter;

public class StringOperate {
	private static String resPath = "/Users/maxy/Android/workspace/App/res";

	public static void main(String[] args) {
		addTranslateMethod();
	}

	private static void addTranslateMethod() {
		String transPath = "/Users/maxy/Downloads/LXQ-0047_Clean_36L_0118";
		FrameStarter listener = new FrameStarter();
		listener.addTranslateToValues(resPath, transPath, true);
	}

	/*
	 * values values-ar compare
	 */
	private static void getTranslateMethod() {
		String valuePath = resPath + File.pathSeparator + "values";
		String valuePath_Ar = resPath + File.pathSeparator + "values-ar";
		String savePath = "/Users/maxy/Desktop/res";
		FrameStarter listener = new FrameStarter();
		listener.compareFile(valuePath, valuePath_Ar, savePath, true);
	}

}

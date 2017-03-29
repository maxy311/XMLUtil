package com.wutian.maxy.standard;

import com.wutian.maxy.FrameStarter;

public class StringOperate {
	private static String resPath = "/Users/maxy/Android/workspace/App/res";

	public static void main(String[] args) {
		addTranslateMethod();
//	    getTranslateMethod();
	}

	private static void addTranslateMethod() {
		String transPath = "/Users/maxy/Downloads/LXQ-0051_Clean_Batch1_36L_0328";
		FrameStarter listener = new FrameStarter();
		listener.addTranslateToValues(resPath, transPath, true);
	}

	/*
	 * values values-ar compare
	 */
	private static void getTranslateMethod() {
	    resPath = "/Users/maxy/Desktop/res";
		String valuePath = resPath + "/" + "values";
		String valuePath_Ar = resPath + "/" + "values-zh";
		String savePath = "/Users/maxy/Desktop/res2";
		FrameStarter listener = new FrameStarter();
		listener.compareFile(valuePath, valuePath_Ar, savePath, false);
	}

}

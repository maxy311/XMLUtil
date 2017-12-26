package com.wutian.maxy.standard;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.wutian.maxy.FrameStarter;

public class StringOperate {
	private static String resPath = "/Users/maxy/Android/workspace/App/res";

	public static void main(String[] args) {
//		addTranslateMethod();
	    getTranslateMethod();
		
	}

	private static void addTranslateMethod() {
		String transPath = "/Users/maxy/Downloads/RE_20170627-SHAREit/LXQ-0062_Clean_0704";
		FrameStarter listener = new FrameStarter();
		listener.addTranslateToValues(resPath, transPath, true);
	}

	/*
	 * values values-ar compare
	 */
	private static void getTranslateMethod() {
		String valuePath = resPath + "/" + "values";
		String valuePath_Ar = resPath + "/" + "values-ar";
		String savePath = "/Users/maxy/Desktop/res1";
		FrameStarter listener = new FrameStarter();
		listener.compareFile(valuePath, valuePath_Ar, savePath, true);
	}

}

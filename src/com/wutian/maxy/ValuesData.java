package com.wutian.maxy;

import java.util.HashMap;
import java.util.Map;

public class ValuesData {
	private static Map<String, String> mData = new HashMap<String, String>();

	static {
		mData.put("values-ar", "Arabic");
		mData.put("values-bg", "Bulgarian");
		mData.put("values-bn", "Bengali");
		mData.put("values-cs", "Czech");
		mData.put("values-de", "German");
		mData.put("values-el", "Greek");
		mData.put("values-es", "Spanish");
		mData.put("values-et", "Estonian");
		mData.put("values-fa", "Farsi(Persian)");
		mData.put("values-fi", "Finnish");
		mData.put("values-fr", "French");
		mData.put("values-hi", "Hindi");
		mData.put("values-hr", "Croatian");
		mData.put("values-hu", "Hungarian");
		mData.put("values-in", "Indonesian");
		mData.put("values-it", "Italian");
		mData.put("values-iw", "Hebrew");
		mData.put("values-ja", "Japanese");
		mData.put("values-ko", "Korean");
		mData.put("values-lt", "Lithuanian");
		mData.put("values-lv", "Latvian");
		mData.put("values-ms", "Malay");
		mData.put("values-pl", "Polish");
		mData.put("values-pt-rBR", "Brazilian Portuguese");
		mData.put(" values-pt-rPT", "Portugal Portuguese");
		mData.put("values-ro", "Romanian");
		mData.put("values-ru", "Russian");
		mData.put("values-sk", "Slovakian");
		mData.put("values-sl", "Slovenian");
		mData.put("values-sr", "Serbian");
		mData.put("values-th", "Thai");
		mData.put("values-tr", "Turkish");
		mData.put("values-uk", "Ukrainian");
		mData.put("values-vi", "Vietnamese language");
		mData.put("values-zh-rHK", "Traditional Chinese (Hong Kong)");
		mData.put("values-zh-rTW", "Traditional Chinese (Taiwan)");
	}

	public static Map<String, String> getAllValueData() {
		return mData;
	}

	public static Map<String, String> getAllData() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("Arabic", "values-ar");
		map.put("Bulgarian", "values-bg");
		map.put("Bengali", "values-bn");
		map.put("Czech", "values-cs");
		map.put("German", "values-de");
		map.put("Greek", "values-el");
		map.put("Spanish", "values-es");
		map.put("Estonian", "values-et");
		map.put("Farsi(Persian)", "values-fa");
		map.put("Finnish", "values-fi");
		map.put("French", "values-fr");
		map.put("Hindi", "values-hi");
		map.put("Croatian", "values-hr");
		map.put("Hungarian", "values-hu");
		map.put("Indonesian", "values-in");
		map.put("Italian", "values-it");
		map.put("Hebrew", "values-iw");
		map.put("Japanese", "values-ja");
		map.put("Korean", "values-ko");
		map.put("Lithuanian", "values-lt");
		map.put("Latvian", "values-lv");
		map.put("Malay", "values-ms");
		map.put("Polish", "values-pl");
		map.put("Brazilian Portuguese", "values-pt-rBR");
		map.put("Portugal Portuguese", "values-pt-rPT");
		map.put("Romanian", "values-ro");
		map.put("Russian", "values-ru");
		map.put("Slovakian", "values-sk");
		map.put("Slovenian", "values-sl");
		map.put("Serbian", "values-sr");
		map.put("Thai", "values-th");
		map.put("Turkish", "values-tr");
		map.put("Ukrainian", "values-uk");
		map.put("Vietnamese language", "values-vi");
		map.put("Traditional Chinese (Hong Kong)", "values-zh-rHK");
		map.put("Traditional Chinese (Taiwan)", "values-zh-rTW");
		return map;
	}
}

package com.wutian.maxy.xml.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.wutian.maxy.FileUtils;

public class DimenUtils {

    private static final String srcPath = "/Users/maxy/Android/workspace/App/res";
    private static final String outPath = "/Users/maxy/Desktop";

    private static final String SP_COMMON = "<dimen name=\"common_text_sp_size_%1s\">%2ssp</dimen>";

    private static final String SP_COMMON_KEY = "common_text_sp_size_%s";

    public static void main(String[] args) {
        dealSPDimen();
    }

    public static void dealSPDimen() {
        Map<String, List<String>> map = readDimenValue(srcPath + "/values");
        String outfilePath = outPath + "/sp_dimen.txt";
        File file = new File(outfilePath);
        try {
            if (!file.exists())
                file.createNewFile();
        } catch (IOException e) {}
        writeToFile(file, map);

        Map<String, String> valueMap = getCommonValueMap(map);

        replaceSPString(valueMap);
    }

    private static void replaceSPString(Map<String, String> valueMap) {
        File file = new File(srcPath + "/layout");
        if (!file.exists())
            return;

        for (File f : file.listFiles()) {
            List<String> datas = FileUtils.readStringToList(f);
            dealSpString(f,valueMap,datas);
        }

    }

    private static void dealSpString(File f, Map<String, String> valueMap, List<String> datas) {

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(f));
            for (String key : datas) {
                if (key.contains("android:textSize=")) {
                    String[] strs = key.split("\"");
                    if (strs[1].contains("@dimen/"))
                        strs[1] = strs[1].replace("@dimen/", "");
//                    System.out.println(valueMap.get(strs[1]));
                    if (valueMap.containsKey(strs[1])) {
                        key = key.replace(strs[1], valueMap.get(strs[1]));
//                        System.out.println(valueMap.get(strs[1]));
                    }
                }
                  writer.write(key);
                  writer.newLine();
                  writer.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null)
                    writer.close();
            } catch (IOException e) {}
        }
    
    }

    private static Map<String, String> getCommonValueMap(Map<String, List<String>> map) {
        Map<String, String> valueMap = new HashMap<>();
        Set<String> keys = map.keySet();
        for (String key : keys) {
            List<String> strs = map.get(key);
            for (String str : strs) {
                valueMap.put(str, String.format(SP_COMMON_KEY, key));
//                System.out.println(str + "========" + String.format(SP_COMMON_KEY, key));
            }
        }
        return valueMap;
    }

    private static Map<String, List<String>> readDimenValue(String path) {
        Map<String, List<String>> dimenValues = new TreeMap<String, List<String>>(mKeyComparator);
        File file = new File(path);
        if (!file.exists())
            return dimenValues;

        for (File f : file.listFiles()) {
            if (f.isDirectory())
                continue;
            if (!f.getName().contains("dimen"))
                continue;
            readStringToMap(f, dimenValues);
        }
        return dimenValues;
    }

    public static void readStringToMap(File file, Map<String, List<String>> map) {
        if (file == null || !file.exists())
            return;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (!line.contains("dimen"))
                    continue;

                String[] str = line.split("\">");
                if (str.length != 2)
                    continue;

                if (!str[1].contains("sp"))
                    continue;

                if (str[1].contains("sp</dimen>"))
                    str[1] = str[1].replace("sp</dimen>", "");

                if (str[0].contains("<dimen name=\""))
                    str[0] = str[0].replace("<dimen name=\"", "").trim();

                List<String> values;
                if (map.containsKey(str[1])) {
                    values = map.get(str[1]);
                    values.add(str[0]);
                    map.put(str[1], values);
                    continue;
                }
                values = new ArrayList<>();
                values.add(str[0]);
                map.put(str[1], values);
            }
        } catch (FileNotFoundException e) {} catch (IOException e) {} finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeToFile(File file, Map<String, List<String>> map) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            Set<String> keys = map.keySet();
            for (String key : keys) {
                String str = String.format(SP_COMMON, key, key);
                writer.write(str);
                writer.newLine();
                writer.flush();
            }

            writer.newLine();
            writer.flush();
            writer.newLine();
            writer.flush();

            for (String key : keys) {
                String str = String.format(SP_COMMON, key, ((int)(Integer.parseInt(key) * 1.5)));
                writer.write(str);
                writer.newLine();
                writer.flush();
            }

            writer.newLine();
            writer.flush();
            writer.newLine();
            writer.flush();

            for (String key : keys) {
                writer.write(key + "--------------------------Start-----------------      ");
                writer.newLine();
                writer.flush();

                List<String> strs = map.get(key);
                for (String str : strs) {
                    writer.write(str);
                    writer.newLine();
                    writer.flush();
                }

                writer.write("--------------------------End-------------------      ");
                writer.newLine();
                writer.flush();
                writer.newLine();
                writer.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null)
                    writer.close();
            } catch (IOException e) {}
        }
    }

    public static Comparator<String> mKeyComparator = new Comparator<String>() {

        @Override
        public int compare(String str1, String str2) {
            return str1.compareTo(str2);
        }
    };
}

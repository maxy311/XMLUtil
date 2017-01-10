package com.wutian.maxy.xml.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.wutian.maxy.FileUtils;

public class GetSpecialString {
    private static ExecutorService mScheduler = Executors.newCachedThreadPool();
    private static Map<String, List<String>> mMaps = new LinkedHashMap<>();
    private static List<String> mDatas = new ArrayList<>();

    static {
        
        mDatas.add("<string name=\"lockit_enter_title");
        
//        mDatas.add("<string name=\"cleanit_home_phone_boost");
//        mDatas.add("<string name=\"cleanit_home_battery_saver");
//        mDatas.add("<string name=\"cleanit_home_app_manager");
    }

    public static void main(String[] args) {
        String path = "/Users/maxy/Android/workspace/App/res";
//        String fileName = "feed_strings.xml";
        String fileName= "lock_string.xml";
        readStringToMap(path, fileName);
        System.out.println(mMaps.toString());
//        System.out.println(mMaps.toString());
        File targetFile = new File("/Users/maxy/Desktop/res");
        if (!targetFile.exists())
        	targetFile.mkdir();
        writeMapToXML(mMaps, targetFile, "lock_strings.xml");
    }

    private static void readStringToMap(String path, String fileName) {
        File file = new File(path);
        if (!file.isDirectory())
            return;
        for (File f : file.listFiles()) {
            File tarFile = new File(f, fileName);
            List<String> data = readStringToList(tarFile);

            if (data.isEmpty())
                continue;
            mMaps.put(f.getName(), data);
        }
    }

    private static void writeMapToXML(Map<String, List<String>> map, File dirs,String fileName) {
        Set<String> keys = map.keySet();
        int i = 0;
        for (String dir : keys) {
            File tarFile = new File(dirs, dir);
            if (!tarFile.exists())
            	tarFile.mkdir();
            File f = new File(tarFile, fileName);
            if (!f.exists())
				try {
					f.createNewFile();
				} catch (IOException e1) {
				}
            System.out.println(f.getAbsolutePath() + "       '" + map.size() + "    " + i++);
            if (!f.exists())
                continue;
            BufferedWriter writer = null;

            try {
				writer = new BufferedWriter(new FileWriter(f));
				System.out.println(map.get(dir).get(0));
				writer.write(map.get(dir).get(0));
				writer.flush();
				writer.newLine();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (writer != null)
                        writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        
        }
//        if (!file.exists())
//            try {
//                file.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        if (map.isEmpty())
//            return;

//        BufferedWriter writer = null;
//        try {
//            writer = new BufferedWriter(new FileWriter(file));
//            for (String key : map.keySet()) {
//                writer.write(key);
//                writer.flush();
//                writer.newLine();
//                for (String str : map.get(key)) {
//                    writer.write(str);
//                    writer.flush();
//                    writer.newLine();
//                }
//                writer.flush();
//                writer.newLine();
//                writer.flush();
//                writer.newLine();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//
//            try {
//                if (writer != null)
//                    writer.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

    }

    private static List<String> readStringToList(File file) {
        ArrayList<String> lines = new ArrayList<>();
        if (file == null || !file.exists())
            return lines;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = reader.readLine()) != null) {
                if (line.contains("resources") || line.contains("<?xml") || line.endsWith("-->"))
                    continue;
                String strs[] = line.split("\">");
                if (strs.length != 2)
                    continue;
                if (mDatas.contains(strs[0].trim()))
                    lines.add(line);
            }
        } catch (FileNotFoundException e) {
            return lines;
        } catch (IOException e) {
            return lines;
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return lines;
    }
}
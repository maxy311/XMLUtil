package com.wutian.maxy.xml.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Test2 {

    private static Map<String, String> datas = new LinkedHashMap();

    public static void main(String[] args) {
        redateText();

        writeStrs();
    }

    private static void writeStrs() {

        BufferedWriter writer = null;
        String path = "/Users/maxy/Desktop/aaaaa/";
        Set<String> set = datas.keySet();
        for (String key : set) {
            try {
                String dirPath = path + key;
                System.out.println(dirPath);

                File dir = new File(dirPath);
                if (!dir.exists())
                    dir.mkdirs();

                File valueFile = new File(dir, "strings.xml");
                if (!valueFile.exists())
                    valueFile.createNewFile();

                writer = new BufferedWriter(new FileWriter(valueFile));
                writer.write(datas.get(key));
                writer.flush();
                writer.newLine();
            } catch (Exception e) {
                System.out.println(e.toString());
            } finally {

                try {
                    if (writer != null)
                        writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static Map<String, String> redateText() {
        BufferedReader reader = null;
        String path = "/Users/maxy/Desktop/aaaaaaa.txt";

        try {
            reader = new BufferedReader(new FileReader(path));
            String line = "";
            List<String> list = new ArrayList();
            while ((line = reader.readLine()) != null) {
                if (null == line || "".equals(line))
                    continue;
                list.add(line);
            }

            System.out.println(list.size());
            for (int i = 0; i < list.size(); i += 2) {
                datas.put(list.get(i), list.get(i + 1));
            }
            System.out.println(list.size());
            System.out.println(datas.size());
            Set<String> set = datas.keySet();
            for (String key : set) {
                String value = datas.get(key);
                System.out.println(key);
                System.out.println(value);
                System.out.println("");
            }
        } catch (Exception e) {} finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (Exception e) {}
        }
        return null;
    }
}

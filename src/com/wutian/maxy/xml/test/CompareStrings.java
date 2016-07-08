package com.wutian.maxy.xml.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wutian.maxy.FileUtils;

public class CompareStrings {

    private static final String srcPath = "/Users/maxy/Android/workspace/App/res/";

    private static final String outPath = "/Users/maxy/Desktop";

    public static void main(String[] args) {
         theFirstStart();

//        File file = new File(srcPath);
//        Map<String, String> map = readValueToMap(file, "settings_strings.xml", "setting_password_version_info");
//
//        witerMaptoFile(file, map, "common_strings.xml", "common_operate_skip", "common_operate_close");
    }

    private static void theFirstStart() {
        Map<String, String> allDatas_en;
        Map<String, String> allDatas_zh;

        // ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
        File enDir = new File(srcPath + "/values");
        File zhDir = new File(srcPath + "/values-zh-rCN");
        allDatas_en = getEnDatas(enDir);
        allDatas_zh = getEnDatas(zhDir);

        Map<String, String> map = new HashMap<>();
        for (String key : allDatas_zh.keySet()) {
            String value = allDatas_zh.get(key);

            for (String k : allDatas_zh.keySet()) {
                if (!allDatas_zh.get(k).equals(value))
                    continue;

                if (k.equals(key))
                    continue;

                if (!(allDatas_en.containsKey(key) && allDatas_en.containsKey(k)))
                    continue;

                if (!(allDatas_en.get(key).equals(allDatas_en.get(k))))
                    continue;

                map.put(key, value);
            }
        }

        wirteToFile(outPath + "/zh_log.txt", getSelectMap(getEnDatas(enDir)));

        wirteToFile(outPath + "/en_log.txt", getSelectMap(getEnDatas(zhDir)));

        wirteToFile(outPath + "/zh_en_log.txt", map);

        map = new HashMap<>();
        for (String key : allDatas_en.keySet()) {
            String value = allDatas_en.get(key);

            for (String k : allDatas_en.keySet()) {
                if (!allDatas_en.get(k).equals(value))
                    continue;

                if (k.equals(key))
                    continue;

                if (!(allDatas_zh.containsKey(key) && allDatas_zh.containsKey(k)))
                    continue;

                if (!(allDatas_zh.get(key).equals(allDatas_zh.get(k))))
                    continue;

                map.put(key, k);
            }
        }
        wirteToFile(outPath + "/en_zh_log.txt", map);
    }

    private static Map<String, String> getSelectMap(Map<String, String> allData) {
        Map<String, String> map = new HashMap<>();
        for (String key : allData.keySet()) {
            String value = allData.get(key);

            for (String k : allData.keySet()) {
                if (!allData.get(k).equals(value))
                    continue;

                if (k.equals(key))
                    continue;
                map.put(k, key);
            }
        }
        return map;
    }

    private static void wirteToFile(String filePath, Map<String, String> map) {
        File file = new File(filePath);
        try {
            if (!file.exists())
                file.createNewFile();
        } catch (IOException e) {}

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            Set<String> keys = map.keySet();
            for (String key : keys) {
                writer.write(key + "      " + map.get(key));
                writer.newLine();
                writer.write("");
                writer.newLine();
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

    private static Map<String, String> getEnDatas(File file) {
        Map<String, String> allDatas = new HashMap<>();
        for (File f : file.listFiles()) {
            if (f.isDirectory())
                continue;
            if (!f.getName().contains("string")
                    || file.getName().contains("filter")
                    || file.getName().contains("account")
                    || file.getName().contains("product_setting"))
                continue;

            // System.out.println(fileName);
            readStringToMap(f, allDatas);
        }
        System.out.println(allDatas.size() + "");
        return allDatas;
    }

    public static Map<String, String> readStringToMap(File file, Map<String, String> map) {
        if (file == null || !file.exists())
            return map;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            StringBuffer sb = new StringBuffer();
            String line = null;
            String str = "";
            while ((line = reader.readLine()) != null) {
                if (line.contains("resources") || line.contains("<?xml") || line.endsWith("-->"))
                    continue;

                if (line.contains("plurals") || line.contains("<item quantity")) {
                    continue;
                    // if (line.contains("plurals")) {
                    // if (line.trim().startsWith("<plurals")) {
                    // str = line.trim().split("\">")[0];
                    // sb.append(line.trim());
                    // } else if (line.trim().startsWith("</plurals>")) {
                    // sb.append("\n" + " " + line.trim());
                    // map.put(str, sb.toString());
                    // if ("".equals(str))
                    // System.out.println("errorerrorerrorerrorerrorerrorerrorerrorerrorerrorerrorerrorerrorerrorerrorerror");
                    // str = "";
                    // sb.setLength(0);
                    // }
                    // } else if (line.contains("<item quantity")) {
                    // sb.append("\n" + " " + line.trim());
                    // }
                } else {
                    String strs[] = line.split("\">");
                    if (strs.length != 2)
                        continue;

                    String value = line.substring(line.indexOf("\">") + 2, line.indexOf("</")).trim();
                    map.put(strs[0].trim(), value);
                }

            }
            return map;
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static Map<String, String> readValueToMap(File file, String fileName, String value) {
        Map<String, String> map = new LinkedHashMap<>();
        if (file == null || !file.exists())
            return map;

        for (File fileDir : file.listFiles()) {
            if (!fileDir.getName().contains("value"))
                continue;
            File f = new File(fileDir, fileName);

            if (!f.exists())
                continue;
            readValueToMap(f, map, value);
        }
        return map;

    }

    public static Map<String, String> readValueToMap(File file, Map<String, String> map, String str) {
        if (file == null || !file.exists())
            return map;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            StringBuffer sb = new StringBuffer();
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.contains("resources") || line.contains("<?xml") || line.endsWith("-->"))
                    continue;

                if (line.contains("plurals") || line.contains("<item quantity")) {
                    continue;

                } else {
                    String strs[] = line.split("\">");
                    if (strs.length != 2)
                        continue;
                    if (!strs[0].endsWith(str))
                        continue;
                    String value = line.substring(line.indexOf("\">") + 2, line.indexOf("</")).trim();
                    map.put(file.getParentFile().getName(), value);
                    System.out.println(file.getParentFile().getName() + "     " + value);
                    break;
                }

            }
            return map;
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static void witerMaptoFile(File file, Map<String, String> map, String fileName, String value, String proValue) {
        if (file == null || !file.exists())
            return;

        for (File fileDir : file.listFiles()) {
            if (!fileDir.getName().contains("value"))
                continue;
            File f = new File(fileDir, fileName);

            if (!f.exists())
                continue;
            writeValueToMap(f, map, value, proValue);
        }
        return;
    }

    private static void writeValueToMap(File file, Map<String, String> map, String value, String proValue) {
        if (file == null || !file.exists())
            return;
        List<String> lines = FileUtils.readXml(file);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            for (String line : lines) {
                
                writer.write(line);
                writer.flush();
                writer.newLine();
                String[] strs = line.trim().split("\">");
                if (strs.length == 2) {
                    if (strs[0].endsWith(proValue)) {
                        String str = "    <string name=\"" + value + "\">" + map.get(file.getParentFile().getName()) + "</string>";
                        
                        System.out.println(str);
                        if (lines.contains(str))
                            continue;
                        writer.write(str);
                        writer.flush();
                        writer.newLine();
                    }
                }
            }
        } catch (IOException e) {
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
}

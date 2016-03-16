package com.wutian.maxy.standard;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import com.wutian.maxy.FileUtils;
import com.wutian.maxy.ValuesData;

/*
 * 英文为标准，读取到一个map 中，
 * 读取其他文件到map
 * 写文件，按钮英文的顺序写到文件，如果没有用应为代替。
 * 
 * */
public class StandardXML {

    public static void main(String[] args) {

        String path = "C:\\workspaces\\App\\res\\values\\share_strings.xml";
        // String path2 = "C:\\workspaces\\App\\res\\values-zh-rHK\\share_strings.xml";

        path = "C:\\workspaces\\App\\res\\values";
        dealPaht(path);

    }

    /*
     * path = "C:\\workspaces\\App\\res\\values\\";
     * path2 = "C:\\workspaces\\App\\res\\values-zh-rHK\\";
     */
    private static void dealPaht(String valuePath) {
        File valueDir = new File(valuePath);
        if (!valueDir.exists())
            return;
        dealPaht(valueDir);
    }

    /*
     * 
     * 
     * */
    private static void dealPaht(File valueDir) {
        String valuePath = "";
        String otherPath = "";
        HashMap<String, String> valuesMap = null;
        HashMap<String, String> otherMap;
        for (File file : valueDir.listFiles()) {
            if (file.isDirectory())
                continue;
            if (!file.getName().contains("string"))
                continue;
            Set<String> dirs = ValuesData.getAllValueData().keySet();
            for (String dir : dirs) {
                valuePath = valueDir.getAbsolutePath() + "\\" + file.getName();
                otherPath = valueDir.getParentFile().getAbsolutePath() + "\\" + dir + "\\" + file.getName();
                if (valuesMap == null || valuesMap.size() == 0)
                    valuesMap = (HashMap<String, String>)FileUtils.readAllStringToMap(valuePath);
                otherMap = (HashMap<String, String>)FileUtils.readAllStringToMap(otherPath);
                if (otherMap == null || otherMap.size() == 0)
                    continue;
                writerStandardXML(valuesMap, otherMap, otherPath);
                valuesMap.clear();
                otherMap.clear();

                if (file.getName().contains("help_string")) {
                    File f = new File(otherPath);
                    if (f.exists())
                        f.delete();
                }
                    
                
            }
        }
    }

    private static void writerStandardXML(HashMap<String, String> valuesMap, HashMap<String, String> otherMap, String path2) {
        if (valuesMap.size() == 0)
            return;
        File file = new File(path2);

        try {
            if (!file.exists())
                file.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            // writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            // writer.newLine();
            // writer.write("<resources>");
            // writer.flush();
            // writer.newLine();

            Set<String> valuesKey = valuesMap.keySet();

            for (String key : valuesKey) {
                String ss = "";
                if (otherMap.containsKey(key))
                    ss = otherMap.get(key);
                else
                    ss = valuesMap.get(key);
                writer.write(ss);
                writer.flush();
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        } finally {
            try {
                if (writer != null)
                    writer.close();
            } catch (IOException e) {
                System.out.println(e.toString());
            }
        }

    }

}

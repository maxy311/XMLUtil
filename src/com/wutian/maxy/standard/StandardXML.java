package com.wutian.maxy.standard;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.wutian.maxy.xml.change.XMLUtils;

/*
 * 英文为标准，读取到一个map 中，
 * 读取其他文件到map
 * 写文件，按钮英文的顺序写到文件，如果没有用应为代替。
 * 
 * 1.res 目录保留除英文外37国语言
 * 2.res2 目录存放 英文values
 * 
 * 
 * 
 * 
 * */
public class StandardXML {

    private static int count = 0;

    public static void main(String[] args) {
        // res2 values 目录
        String enPath = "C:\\workspaces\\App\\res2\\values";
        // res 目录
        String valuePaht = "C:\\workspaces\\App\\res";
        dealPath(enPath, valuePaht);
    }

    /*
     * //res2 values 目录
     * // res 目录
     */
    private static void dealPath(String enPath, String valuePaht) {
        File enFiles = new File(enPath);

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);

        for (File file : enFiles.listFiles()) {
            fixedThreadPool.execute(
                    new Runnable() {

                        @Override
                        public void run() {
                            File valueFile = new File(valuePaht);
                            String fileName = file.getName();
                            for (File dirFile : valueFile.listFiles()) {
                                if (!dirFile.isDirectory())
                                    continue;
                                else {
                                    File f = new File(dirFile, fileName);

                                    if (!f.exists())
                                        continue;
                                    startStandardXML(file, f);
                                }
                            }
                        }
                    });
        }
    }

    /*
     * file values/xx
     * 
     * targetFile values-xx/xx
     */
    protected static void startStandardXML(File file, File targetFile) {
        System.out.println(file.getName() + "----" + (count++) + "----" + targetFile.getPath());
        Map<String, String> map = XMLUtils.readStringToMap(targetFile);
        Set<String> keys = map.keySet();

        boolean ignoreNotTrans = false;
        if (targetFile.getParent().contains("values-zh-"))
            ignoreNotTrans = false;
        else
            ignoreNotTrans = true;
        BufferedReader reader = null;
        BufferedWriter writer = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            writer = new BufferedWriter(new FileWriter(targetFile));
            String line = null;
            String str = "";
            while ((line = reader.readLine()) != null) {
                if (line.contains("resources") || line.contains("<?xml") || line.endsWith("-->")) {
                    writer.write(line);
                    writer.flush();
                    writer.newLine();
                } else if (line.contains("plurals") || line.contains("<item quantity")) {
                    if (line.contains("<item quantity"))
                        continue;
                    else {
                        if (line.trim().startsWith("<plurals")) {
                            str = line.trim();
                        } else if (line.trim().startsWith("</plurals>")) {
                            writer.write("    " + map.get(str));
                            writer.flush();
                            writer.newLine();
                            if ("".equals(str))
                                System.out.println("errorerrorerrorerrorerrorerrorerrorerrorerrorerrorerrorerrorerrorerrorerrorerror");
                            str = "";
                        }
                    }
                } else if (ignoreNotTrans && (line.contains("translate=\"false\"") || line.contains("translatable=\"false\""))) {
                    continue;
                } else {
                    String strs[] = line.trim().split("\">");
                    if (keys.contains(strs[0])) {
                        writer.write("    " + map.get(strs[0]));
                        writer.flush();
                        writer.newLine();
                    } else {
                        writer.write(line);
                        writer.flush();
                        writer.newLine();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

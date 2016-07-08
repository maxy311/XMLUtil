package com.wutian.maxy.standard;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.wutian.maxy.FileUtils;

/*
 * 英文为标准，读取到一个map 中，
 * 读取其他文件到map
 * 写文件，按钮英文的顺序写到文件，如果没有用应为代替。
 * 
 * 1.res 目录保留除英文外37国语言
 * 2.res2 目录存放 英文values
 * 
 * 
 * */
public class StandardXML {

    public static void main(String[] args) {
        // res2 values 目录
        String enPath = "/Users/maxy/Android/workspace/App/res/values";
        // res 目录
        String valuePaht = "/Users/maxy/Android/workspace/App/res";
        dealPath(enPath, valuePaht);
    }

    /*
     * //res2 values 目录
     * // res 目录
     */
    private static void dealPath(String enPath, String valuePaht) {
        File enFiles = new File(enPath);

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
        List<Future<?>> futures = new ArrayList<>();
        for (File file : enFiles.listFiles()) {
            String name = file.getName();
//            if (name.contains("dimen") || name.contains("style") || name.contains("attr") || name.contains("color") || name.contains("id"))
//                continue;

            if (!name.contains("dimen"))
                continue;
            Future<?> future = fixedThreadPool.submit(
                    new Runnable() {

                        @Override
                        public void run() {
                            File valueFile = new File(valuePaht);
                            String fileName = file.getName();
                            List<String> lines = FileUtils.readXml(file);
                            for (File dirFile : valueFile.listFiles()) {
                                if (!dirFile.getName().contains("value") || dirFile.getName().equals("values"))
                                    continue;

                                if (!dirFile.isDirectory())
                                    continue;
                                else {
                                    File f = new File(dirFile, fileName);

                                    if (!f.exists())
                                        continue;
                                    startStandardXML(lines, f);
                                }
                            }
                        }
                    });
            futures.add(future);
        }
        FileUtils.endThreadPool(fixedThreadPool, futures);
    }

    /*
     * file values/xx
     * 
     * targetFile values-xx/xx
     */
    protected static void startStandardXML(List<String> lines, File targetFile) {
        Map<String, String> map = FileUtils.readStringToMap(targetFile);
        Set<String> keys = map.keySet();

        boolean ignoreNotTrans = false;
        if (targetFile.getParent().contains("values-zh-"))
            ignoreNotTrans = false;
        else
            ignoreNotTrans = true;
        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(targetFile));
            for (String line : lines) {
                if (ignoreNotTrans && (line.contains("translate") || line.contains("translatable") || line.contains("<!-- Only show in CN version ,don't translation-->")))
                    continue;

                String strs[] = line.trim().split("\">");
                if (strs.length >= 2) {
                    if (keys.contains(strs[0])) {
                        writer.write("    " + map.get(strs[0]));
                        writer.flush();
                        writer.newLine();
                        continue;
                    }
                }
                writer.write(line);
                writer.flush();
                writer.newLine();

            }
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

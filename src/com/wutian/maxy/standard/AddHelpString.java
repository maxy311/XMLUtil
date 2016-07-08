package com.wutian.maxy.standard;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.wutian.maxy.xml.test.XMLUtils;

/*
 * solve
 * <string name = "help_title
 * <string name="help_title
 * */
public class AddHelpString {
    public static String NAME = "share_session_message_item_text_bg";
    private static int COUNT = 0;

    public static void main(String[] args) {
        String res = "C:\\workspaces\\App\\res\\drawable";
        File file = new File(res);

        File res2File = new File(file, NAME + ".xml");
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
        /*for (File f : file.listFiles()) {
            COUNT++;
            File resFile = new File(file, NAME + "_" + COUNT + ".xml");

            f.renameTo(resFile);
             File res3File = new File(res3 + "\\" + f.getName() + "\\" + "help_strings.xml");
            try {
                if (!resFile.exists())
                    resFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            startStandardXML(res2File, resFile, null);
        }*/
        for (int i = 1; i <= 500; i++) {
            COUNT ++;
//            fixedThreadPool.execute(new Runnable() {
//
//                @Override
//                public void run() {
                    File resFile = new File(file, NAME + "_" + COUNT + ".xml");
                    
                    // File res3File = new File(res3 + "\\" + f.getName() + "\\" + "help_strings.xml");
                    try {
                        if (!resFile.exists())
                            resFile.createNewFile();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    startStandardXML(res2File, resFile, null);
                }
//            });
//        }

        /*
         * ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
         * for (File f : file.listFiles()) {
         * fixedThreadPool.execute(new Runnable() {
         * 
         * @Override
         * public void run() {
         * if (!f.isDirectory())
         * return;
         * if (!f.getName().contains("value"))
         * return;
         * File res2File = new File(f, NAME + ".xml");
         * File resFile = new File(f.getParentFile(), NAME + "_" + COUNT + ".xml");
         * COUNT++;
         * // File res3File = new File(res3 + "\\" + f.getName() + "\\" + "help_strings.xml");
         * try {
         * if (!resFile.exists())
         * resFile.createNewFile();
         * } catch (IOException e) {
         * // TODO Auto-generated catch block
         * e.printStackTrace();
         * }
         * startStandardXML(res2File, resFile, null);
         * }
         * });
         * }
         */
    }

    protected static void startStandardXML(File res2File, File resFile, File res3File) {
        Map<String, String> map = XMLUtils.readStringToMap(res2File);

        Set<String> keys = map.keySet();

        BufferedReader reader = null;
        BufferedWriter writer = null;
        boolean ignoreNotTrans = false;

        if (resFile.getParent().contains("values-zh-"))
            ignoreNotTrans = false;
        else
            ignoreNotTrans = true;

        try {
            reader = new BufferedReader(new FileReader(res2File));
            writer = new BufferedWriter(new FileWriter(resFile));
            String line = null;
            String str = "";
            while ((line = reader.readLine()) != null) {
                // if (!(line.contains("<string name=\"help_feedback\">") || line.contains("<string name=\"help_title\">")))
                // {
                // String[] strs = line.split("\">");
                // if (strs.length == 2)
                // line = line.replace(strs[0], strs[0] + "\" translate=\"false");
                //
                // }
                // writer.write(line);
                // writer.flush();
                // writer.newLine();
                if (line.contains("resources") || line.contains("<?xml") || line.endsWith("-->")) {
                    writer.write(line);
                    writer.flush();
                    writer.newLine();
                } else if (line.contains("plurals") || line.contains("<item quantity")) {
                    continue;
                    /*
                     * if (line.contains("<item quantity"))
                     * continue;
                     * else {
                     * if (line.trim().startsWith("<plurals")) {
                     * str = line.trim();
                     * } else if (line.trim().startsWith("</plurals>")) {
                     * writer.write("    " + map.get(str));
                     * writer.flush();
                     * writer.newLine();
                     * if ("".equals(str))
                     * System.out.println("errorerrorerrorerrorerrorerrorerrorerrorerrorerrorerrorerrorerrorerrorerrorerror");
                     * str = "";
                     * }
                     * }
                     */
                } else if (ignoreNotTrans && (line.contains("translate=\"false\"") || line.contains("translatable=\"false\""))) {
                    continue;
                } else {
                    String strs[] = line.trim().split("\">");
                    if (strs.length == 2) {
                        // if (map.get(strs[0]).contains("<string name=\"help_title"))
                        // map.put(strs[0], map.get(strs[0]).replace("<string name=\"help_title", "<string name = \"help_title"));
                        // else if (map.get(strs[0]).contains("<string name=\"help_feedback"))
                        // map.put(strs[0], map.get(strs[0]).replace("<string name=\"help_feedback", "<string name = \"help_feedback"));
                        if ((line.contains("<string name=\"help_feedback\">") || line.contains("<string name=\"help_title\">"))) {

                            writer.write("    " + map.get(strs[0]));
                            writer.flush();
                            writer.newLine();
                        }
                    } else {
                        writer.write(line);
                        writer.flush();
                        writer.newLine();
                    }

                    // if (strs.length == 2){
                    // if ((line.contains("<string name=\"help_feedback\">") || line.contains("<string name=\"help_title\">"))){
                    // writer.write(line);
                    // writer.flush();
                    // writer.newLine();
                    // }
                    // }
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

        System.out.println(resFile.getParent());
    }
}

package com.wutian.maxy.standard;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.wutian.maxy.FileUtils;

public class ReplaceString {
    private static final String SPLIT = "\">";

    private static int COUNT = 0;
    private static final String FILE_NAME = "";
    private static String DELETE_ID = "";
    private static final Map<String, List<String>> DELETE_IDS = new HashMap<>();
    private static final String res = "/Users/maxy/Android/workspace/App/res";

    static {
        List<String> ids = new ArrayList<>();
        ids = new ArrayList<>();
        ids.add("<string name=\"pc_entry_download_pc");
        DELETE_IDS.put("pc_strings.xml", ids);
    }

    public static void main(String[] args) {
        if (DELETE_ID != null && !DELETE_ID.equals("") && !FILE_NAME.equals("") && FILE_NAME != null)
            deleteStringInFile(DELETE_ID, FILE_NAME);
        else if (DELETE_IDS != null && DELETE_IDS.size() > 0) {
            deleteString(DELETE_IDS, FILE_NAME);
        }
    }

    private static void deleteString(Map<String, List<String>> deleteIds, String fileName) {
        if (deleteIds == null || deleteIds.size() == 0)
            return;
        Set<String> keys = deleteIds.keySet();
        for (String key : keys) {
            List<String> ids = deleteIds.get(key);
            if (ids.size() == 1)
                deleteStringInFile(ids.get(0), key);
            else if (ids.size() > 1) {
                deleteStringsInFile(ids, key);
            } else {
                return;
            }
        }
    }

    /*
     * del ： 要删除的字符串id
     * fileName:要删除的字符串所在的文件名
     * resDir:res目录
     */
    private static void deleteStringInFile(String del, String fileName) {
        File file = new File(res);
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);

        List<Future<?>> futures = new ArrayList<>();
        for (File f : file.listFiles()) {
            Future<?> futrue = fixedThreadPool.submit(new Runnable() {
                @Override
                public void run() {
                    if (!f.isDirectory())
                        return;
                    if (!f.getName().contains("value"))
                        return;
                    File deleteFile = new File(f, fileName);
                    startDelete(deleteFile, del);
                }
            });
            futures.add(futrue);
        }
        endThreadPool(fixedThreadPool, futures);
    }

    private static void deleteStringsInFile(List<String> ids, String fileName) {

        File file = new File(res);
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);

        List<Future<?>> futures = new ArrayList<>();
        for (File f : file.listFiles()) {
            Future<?> futrue = fixedThreadPool.submit(new Runnable() {
                @Override
                public void run() {
                    if (!f.isDirectory())
                        return;
                    if (!f.getName().contains("value"))
                        return;
                    File deleteFile = new File(f, fileName);
                    startDelete(deleteFile, ids);
                }
            });
            futures.add(futrue);
        }
        endThreadPool(fixedThreadPool, futures);

    }

    protected static void startDelete(File deleteFile, List<String> ids) {
        if (deleteFile == null || !deleteFile.exists())
            return;
        List<String> lines = FileUtils.readXml(deleteFile);
        if (lines == null || lines.isEmpty())
            return;
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(deleteFile));
            for (String line : lines) {
                String[] strs = line.split(SPLIT);
                if (strs.length >= 2) {
                    if (ids.contains(strs[0].trim()))
                        continue;
                }
                writer.write(line);
                writer.flush();
                writer.newLine();
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
        COUNT++;

    }

    public static void endThreadPool(ExecutorService threadPool, List<Future<?>> futures) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    int i = 0;
                    try {
                        for (Future<?> future : futures) {
                            if (future.isDone()) {
                                i++;
                                continue;
                            } else
                                break;
                        }

                        if (i == futures.size()) {
                            System.out.println(COUNT);
                            threadPool.shutdownNow();
                            break;
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    /*
     * deleteFile :要删除的文件
     * del :要删除的字符串id:
     */

    protected static void startDelete(String deleteFilePath, String del) {
        startDelete(new File(deleteFilePath), del);
    }

    protected static void startDelete(File deleteFile, String del) {
        if (deleteFile == null || !deleteFile.exists())
            return;
        List<String> lines = FileUtils.readXml(deleteFile);
        if (lines == null || lines.isEmpty())
            return;
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(deleteFile));
            for (String line : lines) {
                String[] strs = line.split(SPLIT);
                if (strs.length >= 2) {
                    if (strs[0].trim().equals(del)) {
                    	line = line.replace("www.ushareit.com/device/", "www.ushareit.com");
                    }
                }
                writer.write(line);
                writer.flush();
                writer.newLine();
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
        COUNT++;
    }
}

package com.wutian.maxy;

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
import java.util.concurrent.Future;

public class FileUtils {
    private static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);

    public static void copyFile(File originFile, File targetFile) {
        BufferedReader reader = null;
        BufferedWriter writer = null;

        try {
            reader = new BufferedReader(new FileReader(originFile));
            writer = new BufferedWriter(new FileWriter(targetFile));
            String line = null;
            while (true) {
                line = reader.readLine();
                if (line == null)
                    break;
                writer.write(line);
                writer.flush();
                writer.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (writer != null)
                    writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // read string to a Map
    public static Map<String, String> readStringToMap(String filePath) {
        return readStringToMap(new File(filePath));
    }

    public static Map<String, String> readStringToMap(File file) {
        Map<String, String> map = new LinkedHashMap<String, String>();
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
                    if (line.contains("plurals")) {
                        if (line.trim().startsWith("<plurals")) {
                            str = line.trim().split("\">")[0];
                            sb.append(line.trim());
                        } else if (line.trim().startsWith("</plurals>")) {
                            sb.append("\n" + "    " + line.trim());
                            map.put(str, sb.toString());
                            if ("".equals(str))
                                System.out.println("errorerrorerrorerrorerrorerrorerrorerrorerrorerrorerrorerrorerrorerrorerrorerror");
                            str = "";
                            sb.setLength(0);
                        }
                    } else if (line.contains("<item quantity")) {
                        sb.append("\n" + "        " + line.trim());
                    }
                } else {
                    String strs[] = line.split("\">");
                    if (strs.length != 2)
                        continue;

                    map.put(strs[0].trim(), line.trim());
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

    public static void compareFile(File file, File f, File saveFile) {
        boolean flag = false;
        if (!saveFile.isDirectory())
            return;
        if (!"values".equals(saveFile.getName())) {
            saveFile = new File(saveFile, "values");
            if (!saveFile.exists())
                saveFile.mkdirs();
        }

        try {
            saveFile = new File(saveFile, file.getName());
            if (!saveFile.exists())
                saveFile.createNewFile();
        } catch (IOException e) {}

        if (!f.exists()) {
            try {
                copyFile(file, saveFile);
                String valuePaht = file.getParentFile().getParentFile().getAbsolutePath();
                File zhFile = new File(valuePaht + "\\" + "values-zh-rCN" + "\\" + file.getName());
                if (zhFile.exists()) {
                    valuePaht = saveFile.getParentFile().getParentFile().getAbsolutePath();
                    File zhTargetFile = new File(valuePaht + "\\" + "values-zh" + "\\" + file.getName());
                    if (!zhTargetFile.exists())
                        zhTargetFile.createNewFile();
                    copyFile(zhFile, zhTargetFile);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return;
        }
        Map<String, String> originMap = FileUtils.readStringToMap(file);
        Map<String, String> targetMap = FileUtils.readStringToMap(f);
        List<String> selectKeys = new ArrayList<String>();
        Set<String> originKeys = originMap.keySet();
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(saveFile));
            writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            writer.newLine();
            writer.write("<resources>");
            writer.flush();
            writer.newLine();
            for (String key : originKeys) {
                if (key.contains("translate") || key.contains("translatable"))
                    continue;
                if (targetMap.containsKey(key)) {
                    //TODO 注释需要测试。
//                    if (targetMap.get(key).equals(originMap.get(key)))
                        continue;
//                        flag = true;
                }
                writer.write("    " + originMap.get(key));
                writer.flush();
                writer.newLine();
                selectKeys.add(key);
            }
            writer.write("</resources>");
            writer.flush();
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
        //TODO need to test
//        if (flag)
//            tryToAddZhString(file, saveFile.getParentFile().getParentFile(), selectKeys);
//        else
//            saveFile.delete();
    }

    private static void tryToAddZhString(File file, File saveSrcDir, List<String> selectKeysList) {

        if (!"values".equals(file.getParentFile().getName()))
            return;
        File zhFile = new File(file.getParentFile().getParentFile().getAbsolutePath() + "\\" + "values-zh" + "\\" + file.getName());
        if (!zhFile.exists()) {
            zhFile = new File(file.getParentFile().getParentFile().getAbsolutePath() + "\\" + "values-zh-rCN" + "\\" + file.getName());
            if (!zhFile.exists())
                return;
        }

        File saveDir = new File(saveSrcDir, "values-zh");
        if (!saveDir.exists())
            saveDir.mkdirs();

        File saveFile = new File(saveDir, file.getName());
        try {
            if (!saveFile.exists())
                saveFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, String> map = readStringToMap(zhFile);

        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(saveFile));
            writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            writer.newLine();
            writer.write("<resources>");
            writer.flush();
            writer.newLine();
            for (String str : selectKeysList) {
                if (map.containsKey(str)) {
                    writer.write(map.get(str));
                    writer.flush();
                    writer.newLine();
                } else
                    System.out.println(str);
            }
            writer.write("</resources>");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null)
                    writer.close();
            } catch (IOException e) {
                System.out.println(e.toString());
            }
        }
    }

    public static List<String> readXml(String path) {
        return readXml(new File(path));
    }

    public static List<String> readXml(File file) {
        ArrayList<String> lines = new ArrayList<>();
        if (file == null || !file.exists())
            return lines;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = "";
            StringBuffer sb = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                if (line.contains("plurals") || line.contains("<item quantity")) {
                    if (line.contains("plurals")) {
                        if (line.trim().startsWith("<plurals"))
                            sb.append(line);
                        else if (line.trim().startsWith("</plurals>")) {
                            sb.append("\n" + line);
                            lines.add(sb.toString());
                            sb.setLength(0);
                        }
                    } else if (line.contains("<item quantity")) {
                        sb.append("\n" + line);
                    }
                } else
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

    public static void addTransValuesToRes(File resFile, File translateFile) {
        fixedThreadPool.execute(new Runnable() {

            @Override
            public void run() {
                List<String> lines = FileUtils.readXml(resFile);
                Map<String, String> map = FileUtils.readStringToMap(translateFile);
                Set<String> keys = map.keySet();
                ArrayList<String> temp = new ArrayList<String>();
                BufferedWriter writer = null;
                try {
                    writer = new BufferedWriter(new FileWriter(resFile));
                    for (String line : lines) {
                        if ("</resources>".equals(line.trim())) {
                            if (temp.size() != map.size())
                                writeMapLeftToXml(writer, temp, map);
                        }

                        String[] strs = line.trim().split("\">");
                        if (strs.length >= 2 && keys.contains(strs[0])) {
                            writer.write("    " + map.get(strs[0]));
                            writer.flush();
                            writer.newLine();
                            temp.add(strs[0]);
                        } else {
                            writer.write(line);
                            writer.flush();
                            writer.newLine();
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
        });
    }

    private static void writeMapLeftToXml(BufferedWriter writer, ArrayList<String> temp, Map<String, String> map) throws IOException {
        Set<String> keys = map.keySet();
        // 添加空行区分。
        writer.write("");
        writer.flush();
        writer.newLine();
        for (String key : keys) {
            if (!temp.contains(key)) {
                writer.write("    " + map.get(key));
                writer.flush();
                writer.newLine();
            }
        }
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
}

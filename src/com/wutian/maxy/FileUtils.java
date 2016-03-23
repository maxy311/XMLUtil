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

public class FileUtils {

    public static void copyFile(File f1, File f2) {
        BufferedReader reader = null;
        BufferedWriter writer = null;

        try {
            reader = new BufferedReader(new FileReader(f1));
            writer = new BufferedWriter(new FileWriter(f2));
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
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            try {
                if (writer != null)
                    writer.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    // read string to a Map
    public static Map<String, String> readStringToMap(String mapPath) {
        if (mapPath == null || mapPath.length() == 0)
            return null;

        Map<String, String> map = new LinkedHashMap<String, String>();
        BufferedReader reader = null;
        try {
            File file = new File(mapPath);
            if (!file.exists())
                return map;
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.contains("resources") || line.contains("<?xml") || line.endsWith("-->"))
                    continue;
                String strs[] = line.split("\">");
                if (strs.length != 2)
                    continue;

                map.put(strs[0].trim(), line.trim());
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

    // read all string to a Map
    public static Map<String, String> readAllStringToMap(String mapPath) {
        if (mapPath == null || mapPath.length() == 0)
            return null;

        Map<String, String> map = new LinkedHashMap<String, String>();
        BufferedReader reader = null;
        try {
            File file = new File(mapPath);
            if (!file.exists())
                return map;
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            StringBuffer buffer = new StringBuffer();
            boolean flag = true;
            while ((line = reader.readLine()) != null) {

                if (line.contains("translatable = \"false\""))
                    continue;
                if (line.trim().startsWith("<plurals"))
                    flag = false;

                if (line.trim().startsWith("</plurals>")) {
                    buffer.append("\n" + line);
                    String strs[] = buffer.toString().split(">");
                    map.put(strs[0].trim(), buffer.toString());
                    flag = true;
                    buffer.setLength(0);
                    continue;
                }
                if (flag) {
                    String strs[] = line.split(">");
                    map.put(strs[0].trim(), line);
                } else {
                    buffer.append("\n" + line);
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

    public static Map<String, String> readStringToMap(File file) {

        Map<String, String> map = new LinkedHashMap<String, String>();
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
                            str = line.trim();
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
        Map<String, String> originMap = FileUtils.readStringToMap(file);
        Map<String, String> targetMap = FileUtils.readStringToMap(f);
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

                if (targetMap.containsKey(key)) {
                    if (targetMap.get(key).equals(originMap.get(key)))
                        continue;
                    flag = true;
                    // else {
                    // writer.write(targetMap.get(key));
                    // writer.flush();
                    // writer.newLine();
                    // }
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
        if (flag)
            tryToAddZhString(file, saveFile.getParentFile().getParentFile(), selectKeys);
        else
            saveFile.delete();
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

    /*
     * 
     * originFile:代码文件 copy
     * targetFile:代码文件
     * map:要添加的字符串。
     */
    public static void writeMapToXML(File originFile, File targetFile, Map<String, String> map) {
        BufferedReader reader = null;
        BufferedWriter writer = null;
        ArrayList<String> temp = new ArrayList<String>();
        try {
            reader = new BufferedReader(new FileReader(originFile));
            writer = new BufferedWriter(new FileWriter(targetFile));
            String line = null;
            while ((line = reader.readLine()) != null) {
                // do this more fast.
                if (line.contains("resources") || line.contains("<?xml") || line.endsWith("-->") || line.equals("")) {
                    if ("</resources>".equals(line.trim())) {
                        if (temp.size() != map.size())
                            writeMapLeftToXml(writer, temp, map);
                    }

                    writer.write(line);
                    writer.flush();
                    writer.newLine();
                    continue;
                }

                if (map.size() != 0) {
                    if (temp.size() < map.size()) {
                        String[] strs = line.split("\">");
                        if (strs.length == 2) {
                            if (map.containsKey(strs[0].trim())) {
                                writer.write("    " + map.get(strs[0].trim()));
                                writer.flush();
                                writer.newLine();
                                temp.add(strs[0].trim());
                                continue;
                            }
                        }
                    }
                }

                writer.write(line);
                writer.flush();
                writer.newLine();
            }
        } catch (Exception e) {
            return;
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                return;
            }
            try {
                if (writer != null)
                    writer.close();
            } catch (IOException e) {
                return;
            }
        }
        System.out.println("OVER--->" + targetFile.getParent() +"\\"+ targetFile.getName());
    }

    private static void writeMapLeftToXml(BufferedWriter writer, ArrayList<String> temp, Map<String, String> map) throws IOException {
        Set<String> keys = map.keySet();
        //添加空行区分。
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

    public static void deleteOriginFile(String path) {
        Map<String, String> map = ValuesData.getAllData();
        File file = new File(path);
        if (!file.exists() || !file.isDirectory())
            return;
        for (File f : file.listFiles()) {
            if (f.isDirectory()) {
                deleteOriginFile(f.getAbsolutePath());

            } else {
                if (map.containsKey(file.getName()))
                    f.delete();
            }

        }

        if (file.listFiles().length == 0)
            file.delete();
    }

    public static void deleteSpecialFile(String path, String sepcialName) {
        File f = new File(path);
        if (!f.exists())
            return;
        for (File file : f.listFiles()) {
            if (file.isDirectory())
                deleteSpecialFile(file.getAbsolutePath(), sepcialName);
            else {
                if (sepcialName.equals(file.getName()))
                    file.delete();
            }

        }
    }

    public static void deleteFileExcept(String path, String exceptFile) {
        File file = new File(path);
        if (!file.exists())
            return;
        for (File f : file.listFiles()) {
            if (f.isDirectory())
                deleteFileExcept(f.getAbsolutePath(), exceptFile);
            else {
                if (!exceptFile.equals(f.getName()))
                    f.delete();
            }

        }
    }

}

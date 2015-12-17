package com.wutian.maxy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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

        Map<String, String> map = new HashMap<String, String>();
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

    public static Map<String, String> readStringToMap(File file) {

        Map<String, String> map = new HashMap<String, String>();
        BufferedReader reader = null;
        try {
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

    public static void compareFile(File file, File f, File saveFile) {
        Map<String, String> originMap = FileUtils.readStringToMap(file);
        Map<String, String> targetMap = FileUtils.readStringToMap(f);

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
                    else {
                        writer.write(targetMap.get(key));
                        writer.flush();
                        writer.newLine();
                    }
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
        tryToAddZhString(file, saveFile.getParentFile().getParentFile(), selectKeys);
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
                                writer.write(map.get(strs[0].trim()));
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
        System.out.println("OVER--->" + targetFile.getName());
    }

    private static void writeMapLeftToXml(BufferedWriter writer, ArrayList<String> temp, Map<String, String> map) throws IOException {
        for (String str : temp) {
            if (!map.containsKey(str.trim())) {
                writer.write(str);
                writer.flush();
                writer.newLine();
            }
        }
    }
}
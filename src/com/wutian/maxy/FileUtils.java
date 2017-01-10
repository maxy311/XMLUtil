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
    private static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);

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
        return readStringToMap(file, map);
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

    // values 与 values-ar 比较
    /*
     * file : values
     * f: values-ar
     * 
     */
    public static void compareFile(File originFile, File targetFile, File saveFile, boolean isCompareAr) {
        if (!saveFile.isDirectory())
            return;
        if (!"values".equals(saveFile.getName())) {
            saveFile = new File(saveFile, "values");
            if (!saveFile.exists())
                saveFile.mkdirs();
        }

        try {
            saveFile = new File(saveFile, originFile.getName());
            if (!saveFile.exists())
                saveFile.createNewFile();
        } catch (IOException e) {}

        if (!targetFile.exists()) {
            try {
                copyFile(originFile, saveFile);
                String valuePaht = originFile.getParentFile().getParentFile().getAbsolutePath();
                File zhFile = new File(valuePaht + File.separator + "values-zh-rCN" + File.separator + originFile.getName());
                if (zhFile.exists()) {
                    valuePaht = saveFile.getParentFile().getParentFile().getAbsolutePath();
                    File zhTargetFile = new File(valuePaht + File.separator + "values-zh" + File.separator + originFile.getName());
                    if (!zhTargetFile.exists())
                        zhTargetFile.createNewFile();
                    copyFile(zhFile, zhTargetFile);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        Map<String, String> originMap = FileUtils.readStringToMap(originFile);
        Map<String, String> targetMap = FileUtils.readStringToMap(targetFile);
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
                if (key.contains("translate") || key.contains("translatable") || key.contains("<plurals name="))
                    continue;
                if (isCompareAr) {
                    // values 与 values-ar 比较
                    if (targetMap.containsKey(key))
                        continue;
                } else {
                    // values 与 values 比较
                    if (targetMap.containsKey(key) && targetMap.get(key).equals(originMap.get(key)))
                        continue;
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

        if (!selectKeys.isEmpty())
            tryToAddZhString(originFile, saveFile.getParentFile().getParentFile(), selectKeys);
        else
            saveFile.delete();
    }

    private static void tryToAddZhString(File file, File valueSaveDir, List<String> selectKeysList) {
        File zhDir = new File(file.getParentFile().getParentFile(), "values-zh-rCN");
        File zhFile = new File(zhDir, file.getName());
        if (!zhFile.exists())
            return;

        File saveDir = new File(valueSaveDir, "values-zh");
        if (!saveDir.exists())
            saveDir.mkdir();

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
                    System.out.println("values--zh        " + file.getName() + "     didn't contains :      " + str);
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

    public static void addTransValuesToRes(File valuesFile, File valueXXFile, File translateFile) {
    	fixedThreadPool.execute(new Runnable() {
			
			@Override
			public void run() {
                List<String> lines = FileUtils.readXml(valuesFile);

                Map<String, String> transMap = FileUtils.readStringToMap(translateFile);
                Map<String, String> valueXXMap = FileUtils.readStringToMap(valueXXFile);
                Map<String, String> copyValueMap = new LinkedHashMap<>(valueXXMap);
                BufferedWriter writer = null;
                try{
                    writer = new BufferedWriter(new FileWriter(valueXXFile));
                    String defaultValue;
                    for (String line : lines) {
                    	 String[] strs = line.trim().split("\">");
                    	 if (strs.length >=2 && !line.trim().startsWith("<plurals ")) {
                             String key = strs[0];
                             defaultValue = null;
                             if (transMap.containsKey(key)) {
                            	 defaultValue = transMap.get(key);
                             } else if (valueXXMap.containsKey(key)){
                            	 defaultValue = valueXXMap.get(key);
                             }
                             
                             if (copyValueMap.containsKey(key))
                            	 copyValueMap.remove(key);
                             if (null == defaultValue && valueXXMap.size() != 0) {
                                 if (key.contains("translate") || key.contains("translatable") || key.contains("<plurals name="))
                                	 continue;

                            	 System.out.println(valueXXFile.getName() +  "   don't contains " + key);
                            	 continue;
                             }
                             
                             writer.write("    " + defaultValue);
                             writer.flush();
                             writer.newLine();
                    	 } else {
                             writer.write(line);
                             writer.flush();
                             writer.newLine();
                         }

                    }
                    if (copyValueMap.size() != 0) {
                        System.out.println("-----------------the below could delete---  -------------------" + valueXXFile.getParentFile().getName());

                    	for (Map.Entry<String,String> entry : copyValueMap.entrySet()){
                    		if (entry.getKey().contains("<plurals "))
                    			continue;
                    		System.out.println("    contains  but values didn't contains   " + entry.getKey()); 
                    	}
                    } 
                    System.out.println();
                    System.out.println();

                }catch(Exception e) {
                	
                }finally{
                	if (writer != null) {
                		try {
							writer.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
                	}
                }
			}
		});
    }

    public static void addTransValuesToRes(File resFile, File translateFile) {
        fixedThreadPool.execute(new Runnable() {

            @Override
            public void run() {
                List<String> lines = FileUtils.readXml(resFile);
                if (lines.isEmpty()) {
                    File file = new File(resFile.getParentFile().getParentFile().getAbsolutePath() + File.separator +"values"+ File.separator + resFile.getName());
                    if (file.exists())
                        lines = FileUtils.readXml(file);
                }

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

    public static List<String> readStringToList(File file) {
        ArrayList<String> lines = new ArrayList<>();
        if (file == null || !file.exists())
            return lines;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = reader.readLine()) != null)
                lines.add(line);
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

                    }
                    continue;
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

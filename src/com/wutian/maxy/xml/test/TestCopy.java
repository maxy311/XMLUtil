package com.wutian.maxy.xml.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wutian.maxy.FileUtils;

public class TestCopy {

    public static void main(String[] args) {
        String resPath = "/Users/maxy/Downloads/LXQ-0041_Clean_37L_1108";

       File path = new File(resPath);
       
       standXml(path);
    }

    private static int i = 0;
    private static void standXml(File path) {
		for (File file : path.listFiles()) {
			
			if (file.isDirectory()){ 
				standXml(file);
			} else {
				if (".DS_Store".equals(file.getName())) {
					file.delete();
					continue;
				}

				if (file.getParentFile().getName().startsWith("values-"))
					continue;
				
				
				File targetFile = createTargetFile(file);
				FileUtils.copyFile(file, targetFile);
				System.out.println(file.getAbsolutePath() + "         "  + (i++));
				File p1 = file.getParentFile();
				File p2 = p1.getParentFile();
				File p3 = p2.getParentFile();
				System.out.println(p1.getAbsolutePath());
				System.out.println(p2.getAbsolutePath());
				System.out.println(p3.getAbsolutePath());

				file.delete();
				p1.delete();
				p2.delete();
				p3.delete();
				
			}
		}
	}

	private static File createTargetFile(File file) {
		File parentFile = file.getParentFile().getParentFile().getParentFile().getParentFile();

		File valueFile = new File(parentFile, file.getName());

		try {
			if (!valueFile.exists())
				valueFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(valueFile.getAbsolutePath());
		return valueFile;
	}

	public static List<String> readXml(File file) {
        List<String> list = new ArrayList<>();
        if (file == null || !file.exists())
            return list;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = reader.readLine()) != null) {
                if (line.contains("resources") || line.contains("<?xml") || line.endsWith("-->"))
                    continue;

                String strs[] = line.split("\">");
                if (strs.length != 2)
                    continue;
                list.add(strs[0].trim());
            }
        } catch (FileNotFoundException e) {
            return list;
        } catch (IOException e) {
            return list;
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static List<String> readStringToMap(File file, List<String> lines) {
        List<String> list = new ArrayList<>();
        if (file == null || !file.exists())
            return list;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = reader.readLine()) != null) {
                if (line.contains("resources") || line.contains("<?xml") || line.endsWith("-->"))
                    continue;

                String strs[] = line.split("\">");
                if (strs.length != 2)
                    continue;
                if (!lines.contains(strs[0].trim()))
                    continue;
                System.out.println(line);
                lines.add(line);
            }
        } catch (FileNotFoundException e) {
            return list;
        } catch (IOException e) {
            return list;
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}

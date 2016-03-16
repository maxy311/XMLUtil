package com.wutian.maxy.xml.change;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test {
    public static void main(String[] args) {
        String path = "D:\\res2";
        String target = "D:\\res";
        deal(path, target);
    }

    public static void deal(String path,String targetPath){
        File file = new File(path);
        for (File f : file.listFiles()) {
            if (f.isDirectory()){
                File ff = new File(targetPath+f.getName());
                if(!ff.exists())
                    ff.mkdirs();
                deal(path+"\\"+f.getName(), targetPath+"\\"+f.getName());
            }else{
                dealStyleString(path, targetPath);
            }
               
        }
    }
    public static void dealStyleString(String path, String targetPath) {
        File dir = new File(path);
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
        for (File f : dir.listFiles()) {
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        File targetFile = new File(targetPath + "\\" + f.getName());
                        if (!targetFile.exists())
                            targetFile.createNewFile();
                            readAndWrite(f, targetFile, true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // System.out.println(f.getName() + "----------OVER");;
                }
            });
        }
    }

    // 一行一行插入，可能重复。
    public static void readAndWrite(File f, File targetFile, boolean isStyle) {
        BufferedReader reader = null;
        BufferedWriter writer = null;

        try {
            reader = new BufferedReader(new FileReader(f));
            writer = new BufferedWriter(new FileWriter(targetFile));
            String line = null;
            while ((line = reader.readLine()) != null) {
//                if (line.contains("pc_playto_save_ok") || line.contains("pc_ppt_guide_exit") || line.contains("pc_remote_view_save_ok"))
//                    continue;
//                if(line.contains("pc_connect_timeout_ok"))
//                  line = line.replace("pc_connect_timeout_ok", "pc_known_ok");
                writer.write(line);
                writer.flush();
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (writer != null)
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
}

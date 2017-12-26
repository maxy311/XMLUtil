package com.wutian.maxy.xml.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.wutian.maxy.FileUtils;
import com.wutian.maxy.FrameStarter;

public class Test {
    public static void main(String[] args) {
        replaceStrig();
    }

    private static void replaceStrig(){
        String originPath = "/Users/maxy/Downloads/bbbbb/sdcard/logs/logs/stats0_60f0950fddeaea8c1bd8/aplog_boot_BOOT_20170816162703";
        String resPath = "/Users/maxy/Desktop/sender3.txt";
        
       BufferedReader reader = null;
       BufferedWriter writer = null;

       try {
           reader = new BufferedReader(new FileReader(originPath));
           writer = new BufferedWriter(new FileWriter(resPath));
           String line = null;
          while(reader.readLine() != null) {
              line = reader.readLine();
              if (null == line)
                  continue;
              if (line.contains("V AS")
                      || line.contains("D AS")
                      || line.contains("I AS")
                      || line.contains("W AS")
                      || line.contains("E AS")) {
                  writer.write(line);
                  writer.flush();
                  writer.newLine();
              }
          }

       } catch (Exception e) {
           e.printStackTrace();
       } finally {

           try{
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
    
    private static void rename() {
       String path = "/Users/maxy/Android/workspace/App/res";
       File file = new File(path);
       
        for (File f : file.listFiles()) {
            File f1 = new File(f, "share_zone_strigns.xml");
            File f2 = new File(f,"share_zone_strings.xml");
            f1.renameTo(f2);
        }
    }
    private static void writeStrs(File file, List<String> strs) {

        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(file));
            for (String str : strs) {
                if ("whereTemp.append(\" ( sms.type != \");".equals(str) || "whereTemp.append(\" ( sms.type != \");" == str)
                    str = "whereTemp.append(\" ( type != \");";
                writer.write(str);
                writer.flush();
                writer.newLine();
            }
           /* while (true) {
                if (line == null)
                    break;
                writer.write(line);
                writer.flush();
                writer.newLine();
            }*/
        } catch (Exception e) {
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
    
    
    private static void outTime(String path) {
        File file = new File(path);
        if (file.isDirectory()){
            for (File f:file.listFiles()){
                outTime(f.getAbsolutePath());
            }
        } else {
            SimpleDateFormat formt = new SimpleDateFormat("yyyy/MM/dd");
            String data = formt.format(new Date(file.lastModified()));
            if (data.equals("2014/11/14") || data == "2014/11/14")
                return;
            System.out.println(data + file.getAbsolutePath());
        }
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

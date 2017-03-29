package com.wutian.maxy.xml.test;

import java.io.File;

public class RenameUtils {

    private static final String PATH = "/Users/maxy/Downloads/LXQ-0051_Clean_batch2_37L_0328";
    public static void main(String[] args) {
        renameFile("share_strings.xml","shared_strings.xml");
    }
    
    private static void renameFile(String originName, String reName) {
        File file = new File(PATH);
        
       for(File dir : file.listFiles()) {
           if (!dir.isDirectory())
               continue;
           File f = new File (dir, originName);
           if (f.exists()) {
               File reNameFiel = new File(dir, reName);
               f.renameTo(reNameFiel);
           }
       }
    }
}

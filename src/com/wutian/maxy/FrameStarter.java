package com.wutian.maxy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FrameStarter implements ButtonClickInterface {
    private static XmlStringFrame frame = null;
    ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
    public static void main(String[] args) {
        frame = new XmlStringFrame();
        frame.setStartClickListener(new FrameStarter());
    }

    // 原理： 将原目录下的文件放到布标目录下。
    @Override
    public void reNameFile(String path) {
        Map<String, String> map = ValuesData.getAllData();
        File file = new File(path);
        List<Future<?>> futures = new ArrayList<>();
        for (File f : file.listFiles()) {
            Future<?> future = fixedThreadPool.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (!f.isDirectory())
                            return;
                        String name = f.getName();
                        if (map.containsKey(name)) {
                            File targetFile = new File(file, map.get(name));
                            f.renameTo(targetFile);
                        }
                    } catch (Exception e) {}

                }
            });
            futures.add(future);
        }
    }

    /*
     * originpath:resPath
     * targetPaht:翻译路径
     * 
     * */
    @Override
    public void addTranslateToValues(String resPath, String translatePath) {

        File originFile = new File(resPath);
        if (!originFile.exists())
            return;
        File translateFile = new File(translatePath);
        if (!translateFile.exists())
            return;
        for (File file : translateFile.listFiles()) {
            if (file.isDirectory()){
                File f = new File(originFile ,file.getName());
                if (!f.exists())
                    continue;
                addTranslateToValues(f.getAbsolutePath(), file.getAbsolutePath());
            } else {
                try {
                    File f = new File(originFile, file.getName());
                    if (!f.exists())
                        f.createNewFile();
                    FileUtils.addTransValuesToRes(f, file);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /*
     * 比较values 目录。
     * 
     * */
    @Override
    public void compareFile(String originPath, String targetPath, String savePath) {
        File originFile = new File(originPath);
        if (!originFile.exists() || !originFile.isDirectory())
            return;

        File targetFile = new File(targetPath);
        if (!targetFile.exists() || !targetFile.isDirectory())
            return;

        File saveFile = new File(savePath);
        if (!saveFile.exists())
            saveFile.mkdirs();

        for (File file : originFile.listFiles()) {
            if (!file.getName().contains("string")
                    || file.getName().contains("filter")
                    || file.getName().contains("account")
                    || file.getName().contains("product_setting"))
                continue;
            fixedThreadPool.execute(new Runnable() {

                @Override
                public void run() {
                    if (file.isDirectory()) {
                        compareFile(originFile + "//" + file.getName(), targetPath + "//" + file.getName(), savePath);
                    } else {
                        File f = new File(targetFile, file.getName());
                        FileUtils.compareFile(file, f, saveFile);
                    }
                }
            });
        }
    }
}

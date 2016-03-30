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

    public static void main(String[] args) {
        frame = new XmlStringFrame();
        frame.setStartClickListener(new FrameStarter());
    }

    // 原理： 将原目录下的文件放到布标目录下。
    @Override
    public void reNameFile(String path) {
        Map<String, String> map = ValuesData.getAllData();
        File file = new File(path);
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
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
        endThreadPool(fixedThreadPool, futures);

    }

    /*
     * originpath:代码copy路径
     * targetPaht:代码文件路径，存放翻译资源
     * 
     * */
    @Override
    public void startCopyValue(String originPath, String targetPath) {
        File originFile = new File(originPath);
        if (!originFile.exists())
            return;

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);

        for (File file : originFile.listFiles()) {
            fixedThreadPool.execute(
                    new Runnable() {

                        @Override
                        public void run() {
                            if (file.isDirectory()) {
                                File f = new File(targetPath + "\\" + file.getName());
                                if (!f.exists())
                                    f.mkdir();
                                startCopyValue(file.getAbsolutePath(), f.getAbsolutePath());
                            } else {
                                if (targetPath.endsWith(originFile.getName()))
                                    startCopy(targetPath, file);
                            }

                        }
                    });
        }
    }

    @Override
    public void deleteOriginFile(String path) {
        FileUtils.deleteOriginFile(path);
    }

    /*
     * targetPath:代码文件路径
     * file:代码文件copy
     * 
     * */
    private static void startCopy(String targetPath, File file) {
        try {
            File tarFile = new File(targetPath + "\\" + file.getName());
            if (!tarFile.exists())
                tarFile.createNewFile();
            Map<String, String> map = FileUtils.readStringToMap(tarFile);
            if (map.size() == 0)
                FileUtils.copyFile(file, tarFile);
            else
                FileUtils.writeMapToXML(file, tarFile, map);
        } catch (IOException e) {}
    }

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

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);

        for (File file : originFile.listFiles()) {
            if (file.getName().contains("style") || file.getName().contains("dimen") || file.getName().contains("color"))
                continue;
            fixedThreadPool.execute(new Runnable() {

                @Override
                public void run() {
                    if (file.isDirectory()) {
                        compareFile(originFile + "//" + file.getName(), targetPath + "//" + file.getName(), savePath);

                    } else {
                        File f = new File(targetFile, file.getName());
                        if (!f.exists())
                            return;

                        FileUtils.compareFile(file, f, saveFile);
                    }
                }
            });
        }

        // fixedThreadPool.shutdown();
        // while (true) {
        // if (fixedThreadPool.isTerminated()) {
        // frame.showDialog("SUCCESS OVER");
        // break;
        // }
        // }
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

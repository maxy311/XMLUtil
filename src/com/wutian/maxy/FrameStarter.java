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
     */
    @Override
    public void addTranslateToValues(String resPath, String translatePath, boolean isNeedSort) {
        File originFile = new File(resPath);
        if (!originFile.exists())
            return;
        
        File translateFile = new File(translatePath);
        if (!translateFile.exists())
            return;
        
        for (File file : translateFile.listFiles()) {
            if (file.isDirectory()) {
                File f = new File(originFile, file.getName());
                if (!f.exists()) {
                	System.out.println(f.getName());
                	continue;
                }
                addTranslateToValues(f.getAbsolutePath(), file.getAbsolutePath(),isNeedSort);
            } else {
                try {
					File f = new File(originFile, file.getName());
					if (!f.exists()) {
						f.createNewFile();
						FileUtils.copyFile(file, f);
					} else {
						if (isNeedSort) {
							String valuePath = originFile.getParentFile().getAbsolutePath() + File.separator + "values";
							File valueFile = new File(valuePath, f.getName());
//							System.out.println(valueFile.getAbsolutePath());
							if (valueFile == null || !valueFile.exists())
								FileUtils.addTransValuesToRes(f, file);
							else
								FileUtils.addTransValuesToRes(valueFile, f, file);
						} else {
							FileUtils.addTransValuesToRes(f, file);
						}
					}
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
     */
    @Override
    public void compareFile(String originPath, String targetPath, String savePath, boolean isCompareAr) {
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
                        compareFile(originFile + "//" + file.getName(), targetPath + "//" + file.getName(), savePath, isCompareAr);
                    } else {
                        File f = new File(targetFile, file.getName());
                        FileUtils.compareFile(file, f, saveFile, isCompareAr);
                    }
                }
            });
        }
    }

    @Override
    public void standardFile(String srcPath) {
        File srdDir = new File(srcPath);
        File enDir = new File(srdDir, "values");

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
        List<Future<?>> futures = new ArrayList<>();

        for (File file : enDir.listFiles()) {
            String name = file.getName();
            if (name.contains("dimen") || name.contains("style") || name.contains("attr") || name.contains("color") || name.contains("id"))
                continue;

            if (!name.contains("string"))
                continue;
            Future<?> future = fixedThreadPool.submit(
                    new Runnable() {
                        @Override
                        public void run() {
                            List<String> lines = FileUtils.readXml(file);
                            for (File dirFile : srdDir.listFiles()) {
                                if (!dirFile.getName().contains("value") || dirFile.getName().equals("values"))
                                    continue;

                                if (!dirFile.isDirectory())
                                    continue;
                                else {
                                    File f = new File(dirFile, file.getName());

                                    if (!f.exists())
                                        continue;
                                    FileUtils.startStandardXML(lines, f);
                                }
                            }
                        }
                    });
            futures.add(future);
        }
        System.out.println(futures.size());
        FileUtils.endThreadPool(fixedThreadPool, futures);
    }
}

package com.wutian.maxy;

public interface ButtonClickInterface {
    public void reNameFile(String path);

    public void startCopyValue(String originPath, String targetPath);

    public void deleteOriginFile(String path);

    public void compareFile(String originPath, String targetPath, String savePath);
}

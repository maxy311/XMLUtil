package com.wutian.maxy;

public interface ButtonClickInterface {
	public void reNameFile(String path);

	public void addTranslateToValues(String originPath, String targetPath, boolean isNeedSort);

	public void compareFile(String originPath, String targetPath, String savePath, boolean isCompareAR);

	public void standardFile(String srcPath);
}

package com.wutian.maxy.xml.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.wutian.maxy.FileUtils;

public class XMLChange extends FileUtils {

    private static String MARGIN_LEFT = "android:layout_marginLeft";
    private static String MARGIN_RIGHT = "android:layout_marginRight";
    private static String PADDINGLEFT = "android:paddingLeft";
    private static String PADDINGRIGHT = "android:paddingRight";
    private static String GRAVITY = "android:gravity";
    private static String LAYOUTGRACTY = "android:layout_gravity";
    private static String ALIGN_PARENT_LEFT = "android:layout_alignParentLeft";
    private static String ALIGN_PARENT_RIGHT = "android:layout_alignParentRight";
    private static String DRAWABLE_LEFT = "android:drawableLeft";
    private static String DrAWABLE_RIGHT = "android:drawableRight";
    private static String TORIGHTOF = "android:layout_toRightOf";
    private static String TOLEFTOF = "android:layout_toLeftOf";

    private static String TOSTARTOF = "android:layout_toStartOf";
    private static String TOENDOF = "android:layout_toEndOf";
    private static String MARGIN_START = "android:layout_marginStart";
    private static String MARGIN_END = "android:layout_marginEnd";
    private static String PADDINGSTART = "android:paddingStart";
    private static String PADDINGEND = "android:paddingEnd";
    private static String ALIGN_PARENT_START = "android:layout_alignParentStart";
    private static String ALIGN_PARENT_END = "android:layout_alignParentEnd";
    private static String DRAWABLE_START = "android:drawableStart";
    private static String DrAWABLE_END = "android:drawableEnd";

    private static String LEFT = "left";
    private static String RIGHT = "right";
    private static String START = "start";
    private static String END = "end";

    public static void main(String[] args) {

        String path = "C:\\workspaces\\App\\res\\layout2";
        String targetPath = "C:\\workspaces\\App\\res\\layout";

        changeString(path, targetPath);

        // path = "C:\\workspaces\\App\\res\\values2";
        // targetPath = "C:\\workspaces\\App\\res\\values";
        // dealStyleString(path, targetPath);
    }

    public static void dealStyleString(String path, String targetPath) {
        File dir = new File(path);
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
        for (File f : dir.listFiles()) {
            if (!f.getName().contains("style"))
                continue;
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

    public static void changeString(String path, String targetPath) {
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
                        readAndWrite2(f, targetFile, false);

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
                if (line.contains(MARGIN_LEFT) || line.contains(MARGIN_RIGHT)
                        || line.contains(PADDINGLEFT) || line.contains(PADDINGRIGHT)
                        || line.contains(ALIGN_PARENT_LEFT) || line.contains(ALIGN_PARENT_RIGHT)) {
                    String str = line;
                    if (line.contains(MARGIN_LEFT)) {
                        str = line.replace(MARGIN_LEFT, MARGIN_START);
                    } else if (line.contains(MARGIN_RIGHT)) {
                        str = line.replace(MARGIN_RIGHT, MARGIN_END);
                    } else if (line.contains(PADDINGLEFT)) {
                        str = line.replace(PADDINGLEFT, PADDINGSTART);
                    } else if (line.contains(PADDINGRIGHT)) {
                        str = line.replace(PADDINGRIGHT, PADDINGEND);
                    } else if (line.contains(ALIGN_PARENT_LEFT)) {
                        str = line.replace(ALIGN_PARENT_LEFT, ALIGN_PARENT_START);
                    } else if (line.contains(ALIGN_PARENT_RIGHT)) {
                        str = line.replace(ALIGN_PARENT_RIGHT, ALIGN_PARENT_END);
                    } else if (line.contains(DRAWABLE_LEFT)) {
                        str = line.replace(DRAWABLE_LEFT, DRAWABLE_START);
                    } else if (line.contains(DrAWABLE_RIGHT)) {
                        str = line.replace(DrAWABLE_RIGHT, DrAWABLE_END);
                    } else if (line.contains(TORIGHTOF)) {
                        str = line.replace(TORIGHTOF, TOENDOF);
                    } else if (line.contains(TOLEFTOF)) {
                        str = line.replace(TOLEFTOF, TOSTARTOF);
                    }

                    if (!isStyle) {
                        if (str.endsWith(" />"))
                            str = str.replace(" />", "");
                        else if (str.endsWith("/>"))
                            str = str.replace("/>", "");
                        else if (str.endsWith(" >"))
                            str = str.replace(" >", "");
                        else if (str.endsWith(">"))
                            str = str.replace(">", "");
                        else {}
                    }

                    writer.write(str);
                    writer.flush();
                    writer.newLine();
                } else if (line.contains(GRAVITY) || line.contains(LAYOUTGRACTY)) {
                    String str = line;
                    if (line.contains(LEFT))
                        str = line.replace(LEFT, START);
                    else if (line.contains(RIGHT))
                        str = str.replace(RIGHT, END);
                    else
                        str = line;

                    writer.write(str);
                    writer.flush();
                    writer.newLine();
                    continue;
                }

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

    // 一段一段插入，不会产生重复。
    public static void readAndWrite2(File f, File targetFile, boolean isStyle) {
        BufferedReader reader = null;
        BufferedWriter writer = null;

        try {
            reader = new BufferedReader(new FileReader(f));
            writer = new BufferedWriter(new FileWriter(targetFile));
            String line = null;
            List<String> list = new ArrayList<String>();
            while ((line = reader.readLine()) != null) {
                list.add(line);
                if (line.endsWith(" >") || line.endsWith(">")) {
                    String str = line;

                    for (String s : list) {
                        str = s;
                        if (line.contains(MARGIN_LEFT)) {
                            str = line.replace(MARGIN_LEFT, MARGIN_START);
                        } else if (line.contains(MARGIN_RIGHT)) {
                            str = line.replace(MARGIN_RIGHT, MARGIN_END);
                        } else if (line.contains(PADDINGLEFT)) {
                            str = line.replace(PADDINGLEFT, PADDINGSTART);
                        } else if (line.contains(PADDINGRIGHT)) {
                            str = line.replace(PADDINGRIGHT, PADDINGEND);
                        } else if (line.contains(ALIGN_PARENT_LEFT)) {
                            str = line.replace(ALIGN_PARENT_LEFT, ALIGN_PARENT_START);
                        } else if (line.contains(ALIGN_PARENT_RIGHT)) {
                            str = line.replace(ALIGN_PARENT_RIGHT, ALIGN_PARENT_END);
                        } else if (line.contains(DRAWABLE_LEFT)) {
                            str = line.replace(DRAWABLE_LEFT, DRAWABLE_START);
                        } else if (line.contains(DrAWABLE_RIGHT)) {
                            str = line.replace(DrAWABLE_RIGHT, DrAWABLE_END);
                        } else if (line.contains(TORIGHTOF)) {
                            str = line.replace(TORIGHTOF, TOENDOF);
                        } else if (line.contains(TOLEFTOF)) {
                            str = line.replace(TOLEFTOF, TOSTARTOF);
                        } else if (line.contains(GRAVITY) || line.contains(LAYOUTGRACTY)) {
                            str = line;
                            if (line.contains(LEFT))
                                str = line.replace(LEFT, START);
                            else if (line.contains(RIGHT))
                                str = str.replace(RIGHT, END);
                            else
                                str = line;

                            writer.write(str);
                            writer.flush();
                            writer.newLine();
                            continue;
                        }

                        if (!str.equals(s)) {

                            if (str.endsWith(" />"))
                                str = str.replace(" />", "");
                            else if (str.endsWith("/>"))
                                str = str.replace("/>", "");
                            else if (str.endsWith(" >"))
                                str = str.replace(" >", "");
                            else if (str.endsWith(">"))
                                str = str.replace(">", "");
                            else {}

                            if (!list.contains(str) && !list.contains(str + " />") && !list.contains(str + " >")) {
                                writer.write(str);
                                writer.flush();
                                writer.newLine();
                            }

                        }
                        writer.write(s);
                        writer.flush();
                        writer.newLine();
                    }
                    list.clear();
                }
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
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
    }
}

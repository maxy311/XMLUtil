package com.wutian.maxy;

import java.awt.Color;
import java.awt.Container;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class XmlStringFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextField originFile;  // 代码中的文件
    private JTextField targetFile;
    private JTextField renameFile;

    private JTextField compareOrigin;  // 代码中的文件
    private JTextField compareTarget;  // 要比较的文件
    private JTextField saveTarget;
    // private JTextArea logArea;

    private static final String title = "XML字符串操作";

    private static final String RENAME_SELECT = "rename_select";
    private static final String ORIGIN_SELECT = "origin_select";
    private static final String TARGET_SELECT = "target_select";
    private static final String COMPARE_ORIGIN_SELECT = "compare_origin_select";
    private static final String COMPARE_TARGET_SELECT = "compare_target_select";
    private static final String COMPARE_SAVE_SELECT = "compare_save_select";

    private ButtonClickInterface listener;

    public XmlStringFrame() {
        super(title);
        createFrame();
    }

    private void createFrame() {
        setSize(500, 500);
        Container container = getContentPane();
        container.setBackground(Color.white);
        JTabbedPane tabbedPane = new JTabbedPane();
        createRenamePannel(tabbedPane);

        createSelectPannel(tabbedPane);

        createComparePannel(tabbedPane);

//        createStandardXMl(tabbedPane);
        container.add(tabbedPane);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void createStandardXMl(JTabbedPane tabbedPane) {
        // 创建面板
        Panel panel = new Panel();
        panel.setBackground(Color.ORANGE);
        panel.setLayout(null);

        // 创建标签
        JLabel label = new JLabel("标准化代码", SwingConstants.CENTER);
        label.setBounds(150, 0, 200, 40);
        panel.add(label);

        // add desc
        JLabel editorPane = new JLabel();
        editorPane.setText("描述：所有代码与values 保持一致");
        editorPane.setBounds(150, 50, 200, 40);
        panel.add(editorPane);

        // add origin
        JLabel originLabel = new JLabel();
        originLabel.setText("src目录");
        originLabel.setBounds(20, 100, 100, 40);
        panel.add(originLabel);

        compareOrigin = new JTextField();
        compareOrigin.setBounds(80, 100, 300, 40);
        panel.add(compareOrigin);

        JButton fileButton = new JButton("选择目录");
        fileButton.setBounds(390, 100, 80, 40);
        fileButton.addActionListener(selectListener);
        fileButton.setActionCommand(COMPARE_ORIGIN_SELECT);
        panel.add(fileButton);

        JButton startButton = new JButton("Start");
        startButton.setBounds(200, 150, 100, 40);

        startButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String originPath = compareOrigin.getText().trim();
                if ("".equals(originPath))
                    showDirNull();
                listener.standardFile(originPath);
            }
        });
        panel.add(startButton);

        // 将标签面板加入到选项卡面板对象上
        tabbedPane.addTab("Standard", null, panel, "Standard values");

    }

    private void createComparePannel(JTabbedPane tabbedPane) {
        // 创建面板
        Panel panel = new Panel();
        panel.setBackground(Color.ORANGE);
        panel.setLayout(null);
        // 创建标签
        JLabel label = new JLabel("比较面板", SwingConstants.CENTER);
        label.setBounds(150, 0, 200, 40);
        panel.add(label);

        // add origin
        JLabel originLabel = new JLabel();
        originLabel.setText("原始目录");
        originLabel.setBounds(20, 50, 100, 40);
        panel.add(originLabel);

        compareOrigin = new JTextField();
        compareOrigin.setBounds(80, 50, 300, 40);
        panel.add(compareOrigin);

        JButton fileButton = new JButton("选择目录");
        fileButton.setBounds(390, 50, 80, 40);
        fileButton.addActionListener(selectListener);
        fileButton.setActionCommand(COMPARE_ORIGIN_SELECT);
        panel.add(fileButton);

        // add target
        JLabel targetLabel = new JLabel();
        targetLabel.setText("比较目录");
        targetLabel.setBounds(20, 100, 100, 40);
        panel.add(targetLabel);

        compareTarget = new JTextField();
        compareTarget.setBounds(80, 100, 300, 40);
        panel.add(compareTarget);

        fileButton = new JButton("选择目录");
        fileButton.setBounds(390, 100, 80, 40);
        fileButton.addActionListener(selectListener);
        fileButton.setActionCommand(COMPARE_TARGET_SELECT);
        panel.add(fileButton);

        // add target
        JLabel saveLabel = new JLabel();
        saveLabel.setText("保存目录");
        saveLabel.setBounds(20, 150, 100, 40);
        panel.add(saveLabel);

        saveTarget = new JTextField();
        saveTarget.setBounds(80, 150, 300, 40);
        panel.add(saveTarget);

        fileButton = new JButton("选择目录");
        fileButton.setBounds(390, 150, 80, 40);
        fileButton.addActionListener(selectListener);
        fileButton.setActionCommand(COMPARE_SAVE_SELECT);
        panel.add(fileButton);

        // add JCheckBox
        JCheckBox checkBox = new JCheckBox("values/values-ar Compare");
        checkBox.setBounds(20, 200, 300, 40);
        panel.add(checkBox);

        // add start Button
        JButton startButton = new JButton("Start");
        startButton.setBounds(200, 250, 100, 40);

        startButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String originPath = compareOrigin.getText().trim();
                String targetPath = compareTarget.getText().trim();
                String savePath = saveTarget.getText().trim();
                if ("".equals(originPath) || "".equals(targetPath) || "".equals(savePath))
                    showDirNull();
                listener.compareFile(originPath, targetPath, savePath, checkBox.isSelected());
            }
        });
        panel.add(startButton);

        // 将标签面板加入到选项卡面板对象上
        tabbedPane.addTab("Compare", null, panel, "Compare panel");

    }

    private void createRenamePannel(JTabbedPane tabbedPane) {
        Panel panel = new Panel();
        panel.setBackground(Color.green);
        panel.setLayout(null);
        // 创建标签
        JLabel label = new JLabel("重命名", SwingConstants.CENTER);
        label.setBounds(150, 0, 200, 40);
        panel.add(label);

        // add origin
        JLabel originLabel = new JLabel();
        originLabel.setText("原始目录");
        originLabel.setBounds(20, 50, 100, 40);
        panel.add(originLabel);

        renameFile = new JTextField();
        renameFile.setBounds(80, 50, 300, 40);
        panel.add(renameFile);

        JButton fileButton = new JButton("选择目录");
        fileButton.setBounds(390, 50, 80, 40);
        fileButton.addActionListener(selectListener);
        fileButton.setActionCommand(RENAME_SELECT);
        panel.add(fileButton);

        // add Rename Button
        JButton renameButton = new JButton("Rename");
        renameButton.setBounds(200, 150, 100, 40);
        renameButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String renamePath = renameFile.getText().trim();
                if ("".equals(renamePath))
                    showDirNull();
                if (listener != null)
                    listener.reNameFile(renamePath);
            }
        });
        panel.add(renameButton);

        // 将标签面板加入到选项卡面板对象上
        tabbedPane.addTab("Rename", null, panel, "First panel");
    }

    private void createSelectPannel(JTabbedPane tabbedPane) {
        // 创建面板
        Panel panel = new Panel();
        panel.setBackground(Color.green);
        panel.setLayout(null);
        // 创建标签
        JLabel label = new JLabel("选择面板", SwingConstants.CENTER);
        label.setBounds(150, 0, 200, 40);
        panel.add(label);

        // add origin
        JLabel originLabel = new JLabel();
        originLabel.setText("Res路径");
        originLabel.setBounds(20, 50, 100, 40);
        panel.add(originLabel);

        originFile = new JTextField();
        originFile.setBounds(80, 50, 300, 40);
        panel.add(originFile);

        JButton fileButton = new JButton("选择目录");
        fileButton.setBounds(390, 50, 80, 40);
        fileButton.addActionListener(selectListener);
        fileButton.setActionCommand(ORIGIN_SELECT);
        panel.add(fileButton);

        // add target
        JLabel targetLabel = new JLabel();
        targetLabel.setText("翻译资源");
        targetLabel.setBounds(20, 100, 100, 40);
        panel.add(targetLabel);

        targetFile = new JTextField();
        targetFile.setBounds(80, 100, 300, 40);
        panel.add(targetFile);

        fileButton = new JButton("选择目录");
        fileButton.setBounds(390, 100, 80, 40);
        fileButton.addActionListener(selectListener);
        fileButton.setActionCommand(TARGET_SELECT);
        panel.add(fileButton);

        // add start Button
        JButton startButton = new JButton("Start");
        startButton.setBounds(200, 150, 100, 40);

        startButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String originPath = originFile.getText().trim();
                String targetPath = targetFile.getText().trim();
                if ("".equals(originPath) || "".equals(targetPath))
                    showDirNull();
				listener.addTranslateToValues(originPath, targetPath, true);
            }
        });
        panel.add(startButton);

        // 将标签面板加入到选项卡面板对象上
        tabbedPane.addTab("Select", null, panel, "Select panel");
    }

    private ActionListener selectListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser jfc = new JFileChooser();
            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            jfc.showDialog(new JLabel(), "选择");
            File file = jfc.getSelectedFile();

            if (file != null) {
                String command = e.getActionCommand();
                if (RENAME_SELECT.equals(command))
                    renameFile.setText(file.getAbsolutePath());
                else if (ORIGIN_SELECT.equals(command))
                    originFile.setText(file.getAbsolutePath());
                else if (TARGET_SELECT.equals(command))
                    targetFile.setText(file.getAbsolutePath());
                else if (COMPARE_ORIGIN_SELECT.equals(command))
                    compareOrigin.setText(file.getAbsolutePath());
                else if (COMPARE_TARGET_SELECT.equals(command))
                    compareTarget.setText(file.getAbsolutePath());
                else if (COMPARE_SAVE_SELECT.equals(command))
                    saveTarget.setText(file.getAbsolutePath());
                else
                    return;
            }
        }
    };

    public void setStartClickListener(ButtonClickInterface clickInterface) {
        if (clickInterface != null)
            listener = clickInterface;
    }

    public void showDialog(String msg) {
        JOptionPane.showMessageDialog(null, msg, msg, JOptionPane.INFORMATION_MESSAGE);
    }

    private void showDirNull() {
        String msg = "文件目录不能为空";
        JOptionPane.showMessageDialog(null, msg, msg, JOptionPane.ERROR_MESSAGE);
    }
}

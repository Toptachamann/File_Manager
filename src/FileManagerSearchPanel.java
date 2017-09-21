import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.regex.*;

/**
 * Created by Timofey on 9/17/2017.
 */
class FileManagerSearchPanel extends SearchPanel {
    private JPopupMenu popupMenu;

    private MyTreeNode cutFromNode;
    private File fileToCopy;
    private File fileToCut;


    public FileManagerSearchPanel(JFrame frame) {
        this.frame = frame;

        addComponents();

        popupMenu = new MyPopupMenu();

        setComponentPopupMenu(popupMenu);
        MouseListener mouseListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showPopupMenu(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                showPopupMenu(e);
            }

            private void showPopupMenu(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        };
        fileListScrollPane.addMouseListener(mouseListener);
        fileTreeScrollPane.addMouseListener(mouseListener);
        fileList.addMouseListener(mouseListener);
        tree.addMouseListener(mouseListener);
        fileListScrollPane.setComponentPopupMenu(popupMenu);
        fileList.setComponentPopupMenu(popupMenu);
        fileTreeScrollPane.setComponentPopupMenu(popupMenu);
        tree.setComponentPopupMenu(popupMenu);

        addActionsForFileTree();

    }



    private void openFile(File file) {
        TextEditor editor = new TextEditor(file);
        editor.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        editor.setVisible(true);
    }

    private boolean canOpen(File file) {
        return file.isFile() && file.canRead() && file.getAbsolutePath().toLowerCase().endsWith(".txt");
    }

    private void addActionsForFileTree() {
        InputMap inputMap = tree.getInputMap(JComponent.WHEN_FOCUSED);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK), "Open action");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK), "Copy action");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK), "Paste action");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_MASK), "Cut action");
        inputMap.put(KeyStroke.getKeyStroke("DELETE"), "Delete action");

        Action openAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MyTreeNode selectedNode = (MyTreeNode) tree.getLastSelectedPathComponent();
                if (selectedNode != null) {
                    File selectedFile = (File) selectedNode.getUserObject();
                    if (canOpen(selectedFile)) {
                        openFile(selectedFile);
                    }
                }
            }
        };
        Action copyAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MyTreeNode selectedNode = (MyTreeNode) tree.getLastSelectedPathComponent();
                if (selectedNode != null) {
                    fileToCopy = (File) selectedNode.getUserObject();
                    fileToCut = null;
                    cutFromNode = null;
                }
            }
        };
        Action cutAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MyTreeNode selectedNode = (MyTreeNode) tree.getLastSelectedPathComponent();
                if (selectedNode != null) {
                    fileToCopy = null;
                    fileToCut = (File) selectedNode.getUserObject();
                    cutFromNode = selectedNode;
                }
            }
        };
        Action pasteAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fileToCopy != null || fileToCut != null) {
                    MyTreeNode selectedDirectory = (MyTreeNode) tree.getLastSelectedPathComponent();
                    if (selectedDirectory != null) {
                        TreeFile destinationDirectory = (TreeFile) selectedDirectory.getUserObject();
                        if (destinationDirectory.isDirectory()) {
                            try {
                                //need to copy
                                if(fileToCopy != null){
                                    TreeFile destinationFile = new TreeFile(destinationDirectory.getAbsolutePath() + "\\" + fileToCopy.getName());
                                    if(destinationFile.exists()){
                                        JOptionPane.showMessageDialog(frame, "Файл вже існує в цій папці", "Повідомлення", JOptionPane.INFORMATION_MESSAGE);
                                    }else{
                                        Files.copy(fileToCopy.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                        MyTreeNode destNode = new MyTreeNode(destinationFile);
                                        if(destinationFile.isFile()){
                                            destNode.setAllowsChildren(false);
                                        }
                                        treeModel.insertNodeInto(destNode, selectedDirectory, selectedDirectory.getChildCount());
                                    }
                                }else{
                                    //need to cut
                                    TreeFile destinationFile = new TreeFile(destinationDirectory.getAbsolutePath() + "\\" + fileToCut.getName());
                                    if(destinationFile.exists()){
                                        JOptionPane.showMessageDialog(frame, "Файл вже існує в цій папці", "Повідомлення", JOptionPane.INFORMATION_MESSAGE);
                                    }else{
                                        Files.copy(fileToCut.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                        MyTreeNode destNode = new MyTreeNode(destinationFile);
                                        if(destinationFile.isFile()){
                                            destNode.setAllowsChildren(false);
                                        }
                                        treeModel.insertNodeInto(destNode, selectedDirectory, selectedDirectory.getChildCount());
                                        if(deleteFile(fileToCut)){
                                            treeModel.removeNodeFromParent(cutFromNode);
                                        }
                                    }
                                }
                                updateFileList(selectedDirectory);
                            } catch (IOException exception) {
                                exception.printStackTrace();
                            }

                        }
                    }
                }
            }
        };
        Action deleteAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MyTreeNode node = (MyTreeNode) tree.getLastSelectedPathComponent();
                TreeFile file = (TreeFile) node.getUserObject();
                if(deleteFile(file)){
                    MyTreeNode parent = (MyTreeNode) node.getParent();
                    tree.setSelectionPath(getPath(parent));
                    treeModel.removeNodeFromParent(node);
                }
            }
        };

        ActionMap actionMap = tree.getActionMap();
        actionMap.put("Open action", openAction);
        actionMap.put("Copy action", copyAction);
        actionMap.put("Cut action", cutAction);
        actionMap.put("Paste action", pasteAction);
        actionMap.put("Delete action", deleteAction);

    }

    private TreePath getPath(TreeNode treeNode) {
        ArrayList<Object> nodes = new ArrayList<Object>();
        if (treeNode != null) {
            nodes.add(treeNode);
            treeNode = treeNode.getParent();
            while (treeNode != null) {
                nodes.add(0, treeNode);
                treeNode = treeNode.getParent();
            }
        }

        return nodes.isEmpty() ? null : new TreePath(nodes.toArray());
    }

    private boolean deleteFile(File file){
        if(file.getName().toLowerCase().endsWith(".txt") && file.isFile()){
            if(!file.delete()){
                JOptionPane.showMessageDialog(frame, "Програма не може видалити наступний файл: " + file.getAbsolutePath(), "Попередження", JOptionPane.WARNING_MESSAGE);
                return false;
            }else{
                return true;
            }
        }else{
            JOptionPane.showMessageDialog(frame, "Зараз дозволено видаляти тільки файли з розширенням *.txt", "Повідомлення", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
    }




}

/*
    private void addActionsForFileList() {
        InputMap inputMap = fileList.getInputMap(JComponent.WHEN_FOCUSED);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK), "Open action");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK), "Copy action");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK), "Paste action");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_MASK), "Cut action");

        ActionMap actionMap = fileList.getActionMap();
        Action openAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fileName = (String) fileList.getSelectedValue();
                if (fileName != null) {
                    File fileToOpen = new File(selectedDirectory.getAbsolutePath() + "\\" + fileName);
                    if (canOpen(fileToOpen)) {
                        openFile(fileToOpen);
                    }
                }
            }
        };
        Action copyCutAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fileName = (String) fileList.getSelectedValue();
                if (fileName != null && selectedDirectory != null) {
                    fileToCopy = new File(selectedDirectory.getAbsolutePath() + "\\" + fileName);
                    fileToCut = null;
                }
            }
        };
        Action pasteAction = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (fileToCut != null || fileToCopy != null) {
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                    if (selectedNode != null) {
                        File destinationDirectory = (File) selectedNode.getUserObject();
                        if (destinationDirectory.isDirectory()) {
                            try {
                                if (fileToCopy != null) {
                                    Files.copy(fileToCopy.toPath(), destinationDirectory.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                } else {
                                    Files.copy(fileToCut.toPath(), destinationDirectory.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                    fileToCut.delete();
                                    updateFileList(selectedDirectory);
                                }
                            } catch (IOException exception) {
                                exception.printStackTrace();
                            }
                        }
                    }
                }
            }
        };
        actionMap.put("Open action", openAction);
        actionMap.put("Copy action", copyCutAction);
        actionMap.put("Cut action", copyCutAction);
        actionMap.put("Paste action", pasteAction);

    }
*/
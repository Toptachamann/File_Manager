/**
 * Created by Timofey on 9/17/2017.
 */

import auxiliary.MyTreeNode;
import auxiliary.TreeFile;

import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.tree.*;

class FileManagerSearchPanel extends SearchPanel {

    private JPopupMenu popupMenu;

    private MyTreeNode cutFromNode;
    private TreeFile fileToCopy;
    private TreeFile fileToCut;

    public FileManagerSearchPanel(JFrame frame) {
        super(frame);

        MouseListener mouseListener = new PopupMouseListener();
        fileListScrollPane.addMouseListener(mouseListener);
        fileTreeScrollPane.addMouseListener(mouseListener);
        fileList.addMouseListener(mouseListener);
        tree.addMouseListener(mouseListener);

        addActionsToFileTree();
    }


    private void copyFileTo(TreeFile source, TreeFile parentDirectory, MyTreeNode parentDirectoryNode) throws IOException {
        TreeFile childFile = new TreeFile(parentDirectory.getAbsolutePath() + "\\" + source.getName());
        if (childFile.exists()) {
            JOptionPane.showMessageDialog(this, "Файл " + childFile.getName() + " вже існує в папці " + parentDirectory.getAbsolutePath(),
                    "Повідомлення", JOptionPane.INFORMATION_MESSAGE);
        } else {
            try {
                Files.copy(source.toPath(), childFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                MyTreeNode childNode = new MyTreeNode(childFile);
                childNode.setLoaded(true);
                if (childFile.isFile()) {
                    childNode.setAllowsChildren(false);
                }
                treeModel.insertNodeInto(childNode, parentDirectoryNode, parentDirectoryNode.getChildCount());
                if (source.isDirectory()) {
                    File[] files = source.listFiles(pathname -> true);
                    for (File file : files) {
                        TreeFile treeFile = new TreeFile(file.getAbsolutePath());
                        copyFileTo(treeFile, childFile, childNode);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void addActionsToFileTree() {
        InputMap inputMap = tree.getInputMap(JComponent.WHEN_FOCUSED);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK), "Open action");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK), "Copy action");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK), "Paste action");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK), "Cut action");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK|KeyEvent.SHIFT_DOWN_MASK),
                "Copy all with extension action");
        inputMap.put(KeyStroke.getKeyStroke("DELETE"), "Delete action");
        inputMap.put(KeyStroke.getKeyStroke("F3"), "Clear content action");

        Action openAction = new OpenAction();
        Action copyAction = new CopyAction();
        Action cutAction = new CutAction();
        Action pasteAction = new PasteAction();
        Action deleteAction = new DeleteAction();
        Action clearContentAction = new ClearContentAction();
        Action copyAllWithExtensionAction = new CopyAllWithExtensionAction();
        Action newFolderAction = new ItemAction("", true);
        Action newTextFileAction = new ItemAction(".txt", false);
        Action newHtmlFileAction = new ItemAction(".htm", false);

        /*tree.setEditable(true);
        treeModel.addTreeModelListener(new TreeModelListener() {
            @Override
            public void treeNodesChanged(TreeModelEvent e) {
                MyTreeNode changedNode = (MyTreeNode) tree.getLastSelectedPathComponent();
                if(changedNode != null){
                    MyTreeNode parent = (MyTreeNode) changedNode.getParent();
                    TreeFile parentFile = (TreeFile) parent.getUserObject();
                    //TreeFile newName = (TreeFile) changedNode.getUserObject();
                    //System.out.println(newName);
                    String newName = (String) changedNode.getUserObject();
                    String newPath = parentFile.getAbsolutePath() + "\\" + newName;
                    TreeFile newFile = new TreeFile(newPath);

                    changedNode.setUserObject(newFile);
                }

            }

            @Override
            public void treeNodesInserted(TreeModelEvent e) {

            }

            @Override
            public void treeNodesRemoved(TreeModelEvent e) {

            }

            @Override
            public void treeStructureChanged(TreeModelEvent e) {

            }
        });*/

        ActionMap actionMap = tree.getActionMap();
        actionMap.put("Open action", openAction);
        actionMap.put("Copy action", copyAction);
        actionMap.put("Cut action", cutAction);
        actionMap.put("Paste action", pasteAction);
        actionMap.put("Delete action", deleteAction);
        actionMap.put("Clear content action", clearContentAction);
        actionMap.put("Copy all with extension action", copyAllWithExtensionAction);

        popupMenu = new JPopupMenu();
        setComponentPopupMenu(popupMenu);
        JMenuItem copyPopup = new JMenuItem("Скопіювати");
        JMenuItem pastePopup = new JMenuItem("Вставити");
        JMenuItem cutPopup = new JMenuItem("Вирізати");
        JMenuItem deletePopup = new JMenuItem("Видалити");
        JMenu newItem = new JMenu("Додати");
        JMenuItem newFolder = new JMenuItem("Папку");
        JMenuItem newTextFile = new JMenuItem("Текстовий файл (*txt)");
        JMenuItem newHtmlFile = new JMenuItem("HTML файл (*.html)");

        copyPopup.addActionListener(copyAction);
        pastePopup.addActionListener(pasteAction);
        cutPopup.addActionListener(cutAction);
        deletePopup.addActionListener(deleteAction);
        newFolder.addActionListener(newFolderAction);
        newTextFile.addActionListener(newTextFileAction);
        newHtmlFile.addActionListener(newHtmlFileAction);

        popupMenu.add(copyPopup);
        popupMenu.add(pastePopup);
        popupMenu.add(cutPopup);
        popupMenu.add(deletePopup);
        popupMenu.add(newItem);
        newItem.add(newFolder);
        newItem.add(newTextFile);
        newItem.add(newHtmlFile);

        tree.setComponentPopupMenu(popupMenu);
    }


    private class CopyAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            MyTreeNode selectedNode = (MyTreeNode) tree.getLastSelectedPathComponent();
            if (selectedNode != null) {
                fileToCopy = (TreeFile) selectedNode.getUserObject();
                fileToCut = null;
                cutFromNode = null;
            }
        }
    }

    private class CutAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            MyTreeNode selectedNode = (MyTreeNode) tree.getLastSelectedPathComponent();
            if (selectedNode != null) {
                fileToCopy = null;
                fileToCut = (TreeFile) selectedNode.getUserObject();
                cutFromNode = selectedNode;
            }
        }
    }

    private class PasteAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (fileToCopy != null || fileToCut != null) {
                MyTreeNode selectedNode = (MyTreeNode) tree.getLastSelectedPathComponent();
                if (selectedNode != null) {
                    TreeFile parentDirectory = (TreeFile) selectedNode.getUserObject();
                    if (parentDirectory.isDirectory()) {
                        try {
                            if (fileToCopy != null) {  //need to copy
                                if (isParent(fileToCopy, parentDirectory)) {
                                    JOptionPane.showMessageDialog(frame, "Обрана папка є підпапкою папки, яку ви хочете копіювати",
                                            "Повідомлення", JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    copyFileTo(fileToCopy, parentDirectory, selectedNode);
                                }
                            } else {                   //need to cut
                                if (isParent(fileToCut, parentDirectory)) {
                                    JOptionPane.showMessageDialog(frame, "Обрана папка є підпапкою папки, яку ви хочете копіювати",
                                            "Повідомлення", JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    copyFileTo(fileToCut, parentDirectory, selectedNode);
                                    if (deleteFile(fileToCut)) {
                                        treeModel.removeNodeFromParent(cutFromNode);
                                    }
                                }
                            }
                            updateFileList(selectedNode);
                        } catch (IOException exception) {
                            exception.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private class OpenAction extends AbstractAction {
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
    }

    private class DeleteAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            MyTreeNode node = (MyTreeNode) tree.getLastSelectedPathComponent();
            TreeFile file = (TreeFile) node.getUserObject();
            if (deleteFile(file)) {
                MyTreeNode parent = (MyTreeNode) node.getParent();
                tree.setSelectionPath(getPath(parent));
                treeModel.removeNodeFromParent(node);
            }
        }
    }

    private class ClearContentAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            MyTreeNode selectedNode = (MyTreeNode) tree.getLastSelectedPathComponent();
            if (selectedNode != null) {
                TreeFile selectedFile = (TreeFile) selectedNode.getUserObject();
                clearContent(selectedFile);
            }
        }
    }

    private class PopupMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                showPopupMenu(e);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                showPopupMenu(e);
            }
        }

        private void showPopupMenu(MouseEvent e) {
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    private class CopyAllWithExtensionAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (fileToCopy != null) {
                MyTreeNode selectedNode = (MyTreeNode) tree.getLastSelectedPathComponent();
                if (selectedNode != null) {
                    TreeFile selectedFile = (TreeFile) selectedNode.getUserObject();
                    if (selectedFile.isDirectory()) {
                        try {
                            String selectedFileType = (String) extensionBox.getSelectedItem();
                            String extension = getExtension(selectedFileType);
                            File[] specifiedFiles = fileToCopy.listFiles((dir, name) -> name.toLowerCase().endsWith(extension));
                            for (File file : specifiedFiles) {
                                TreeFile treeFile = new TreeFile(file.getAbsolutePath());
                                copyFileTo(treeFile, selectedFile, selectedNode);
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void clearContent(TreeFile file) {
        if (file.isFile() && file.getName().toLowerCase().endsWith(".txt")) {
            int reply = JOptionPane.showConfirmDialog(frame, "Ви впевненяб що хочете назавжди видалити вміст цього файлу?", "Запит підтверждення", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                    writer.write("");
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Не вдалося видалити вміст файлу", "Повідомлення", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Зараз можна видаляти вміст тільки текстових файлів", "Повідомлення", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void openFile(File file) {
        TextEditor editor = new TextEditor(file);
        editor.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        editor.setVisible(true);
    }

    private boolean canOpen(File file) {
        return file.isFile() && file.canRead() && file.getAbsolutePath().toLowerCase().endsWith(".txt");
    }

    private TreePath getPath(TreeNode treeNode) {
        ArrayList<Object> nodes = new ArrayList<>();
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

    private boolean deleteFile(File file) {
        if (file.getName().toLowerCase().endsWith(".txt") && file.isFile()) {
            if (!file.delete()) {
                JOptionPane.showMessageDialog(frame, "Програма не може видалити наступний файл: " + file.getAbsolutePath(), "Попередження", JOptionPane.WARNING_MESSAGE);
                return false;
            } else {
                return true;
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Зараз дозволено видаляти тільки файли з розширенням *.txt", "Повідомлення", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
    }

    private boolean isParent(File parent, File child) {
        if (parent.equals(child)) {
            return true;
        }
        while (child != null) {
            child = child.getParentFile();
            if (child.equals(parent)) {
                return true;
            }
        }
        return false;
    }


}
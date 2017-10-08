package components;
/**
 * Created by Timofey on 9/17/2017.
 */

import actions.CopyAction;
import actions.CutAction;
import actions.DeleteAction;
import actions.OpenAction;
import actions.PasteAction;
import auxiliary.MyTreeNode;
import auxiliary.TreeFile;
import com.sun.jna.platform.FileUtils;

import java.awt.EventQueue;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.swing.*;
import javax.swing.tree.*;

public class FileManagerSearchPanel extends SearchPanel {

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

    private void addActionsToFileTree() {
        Action openAction = new OpenAction(this);
        Action copyAction = new CopyAction(this);
        Action cutAction = new CutAction(this);
        Action pasteAction = new PasteAction(this);
        Action deleteAction = new DeleteAction(this);
        Action clearContentAction = new ClearContentAction();
        Action copyAllWithExtensionAction = new CopyAllWithExtensionAction();
        Action copyWithoutMultipleLines = new CopyWithoutMultipleLinesAction();
        Action copyHtmlFileAction = new CopyHtmlFileAction();
        Action newFolderAction = new SearchPanel.ItemAction("", true);
        Action newTextFileAction = new SearchPanel.ItemAction(".txt", false);
        Action newHtmlFileAction = new SearchPanel.ItemAction(".html", false);

        InputMap inputMap = tree.getInputMap(JComponent.WHEN_FOCUSED);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK), "Open action");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK), "Copy action");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK), "Paste action");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK), "Cut action");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK),
                "Copy all with extension action");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK | KeyEvent.ALT_DOWN_MASK),
                "Copy without multiple lines action");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK),
                "Copy html file action");
        inputMap.put(KeyStroke.getKeyStroke("DELETE"), "Delete action");
        inputMap.put(KeyStroke.getKeyStroke("F3"), "Clear content action");

        ActionMap actionMap = tree.getActionMap();
        actionMap.put("Open action", openAction);
        actionMap.put("Copy action", copyAction);
        actionMap.put("Cut action", cutAction);
        actionMap.put("Paste action", pasteAction);
        actionMap.put("Delete action", deleteAction);
        actionMap.put("Clear content action", clearContentAction);
        actionMap.put("Copy all with extension action", copyAllWithExtensionAction);
        actionMap.put("Copy without multiple lines action", copyWithoutMultipleLines);
        actionMap.put("Copy html file action", copyHtmlFileAction);

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


    private boolean checkCanCopy(File fileToCopy, File parentDirectory) {
        if (isParent(fileToCopy, parentDirectory)) {
            JOptionPane.showMessageDialog(frame, "Обрана папка є підпапкою папки, яку ви хочете копіювати",
                    "Повідомлення", JOptionPane.INFORMATION_MESSAGE);
            return false;
        } else {
            return true;
        }
    }

    private void copyFileTo(TreeFile source, TreeFile parentDirectory, MyTreeNode parentDirectoryNode) throws IOException {
        if (!checkCanCopy(source, parentDirectory)) {
            return;
        }
        TreeFile childFile = new TreeFile(parentDirectory.getAbsolutePath() + File.separator + source.getName());
        if (childFile.exists()) {
            JOptionPane.showMessageDialog(this, "Файл " + childFile.getName() + " вже існує в папці " + parentDirectory.getAbsolutePath(),
                    "Повідомлення", JOptionPane.INFORMATION_MESSAGE);
        } else {
            try {
                Files.copy(source.toPath(), childFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                if (!parentDirectoryNode.isLoaded()) {
                    addOneLevel(parentDirectoryNode);
                }
                MyTreeNode childNode = insertFile(parentDirectoryNode, childFile);
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

    private MyTreeNode insertFile(MyTreeNode node, TreeFile file) {
        if (!node.isLoaded()) {
            addOneLevel(node);
        }
        MyTreeNode childNode = new MyTreeNode(file);
        childNode.setLoaded(true);
        if (file.isFile()) {
            childNode.setAllowsChildren(false);
        }
        treeModel.insertNodeInto(childNode, node, node.getChildCount());
        return childNode;
    }

    public void copyAct() {
        MyTreeNode selectedNode = (MyTreeNode) tree.getLastSelectedPathComponent();
        if (selectedNode != null) {
            fileToCopy = (TreeFile) selectedNode.getUserObject();
            fileToCut = null;
            cutFromNode = null;
        }
    }

    public void cutAct() {
        MyTreeNode selectedNode = (MyTreeNode) tree.getLastSelectedPathComponent();
        if (selectedNode != null) {
            fileToCopy = null;
            fileToCut = (TreeFile) selectedNode.getUserObject();
            cutFromNode = selectedNode;
        }
    }

    public void pasteAct() {
        if (fileToCopy != null || fileToCut != null) {
            MyTreeNode selectedNode = (MyTreeNode) tree.getLastSelectedPathComponent();
            if (selectedNode != null) {
                TreeFile parentDirectory = (TreeFile) selectedNode.getUserObject();
                if (parentDirectory.isDirectory()) {
                    try {
                        if (fileToCopy != null) {  //need to copy
                            copyFileTo(fileToCopy, parentDirectory, selectedNode);
                        } else {                   //need to cut
                            copyFileTo(fileToCut, parentDirectory, selectedNode);
                            if (moveFileToTrash(fileToCut)) {
                                treeModel.removeNodeFromParent(cutFromNode);
                            }
                            fileToCut = null;
                        }
                        updateFileList(selectedNode);
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }
    }

    public void openAct() {
        MyTreeNode selectedNode = (MyTreeNode) tree.getLastSelectedPathComponent();
        if (selectedNode != null) {
            TreeFile selectedFile = (TreeFile) selectedNode.getUserObject();
            if (canOpen(selectedFile)) {
                openFile(selectedFile);
            }
        }
    }

    public void deleteAct() {
        MyTreeNode node = (MyTreeNode) tree.getLastSelectedPathComponent();
        TreeFile file = (TreeFile) node.getUserObject();
        if (moveFileToTrash(file)) {
            MyTreeNode parent = (MyTreeNode) node.getParent();
            tree.setSelectionPath(getPath(parent));
            treeModel.removeNodeFromParent(node);
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
                            File[] filesWithSpecifiedExtension = fileToCopy.listFiles((dir, name) -> name.toLowerCase().endsWith(extension));
                            for (File file : filesWithSpecifiedExtension) {
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

    private class CopyWithoutMultipleLinesAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            MyTreeNode selectedNode = (MyTreeNode) tree.getLastSelectedPathComponent();
            if (selectedNode != null) {
                TreeFile selectedDirectory = (TreeFile) selectedNode.getUserObject();
                if (selectedDirectory.isDirectory() && fileToCopy != null) {
                    TreeFile destinationFile = new TreeFile(selectedDirectory.getAbsolutePath() + File.separator + fileToCopy.getName());
                    if (!destinationFile.exists()) {
                        try {
                            destinationFile.createNewFile();
                            copyWithoutMultipleLines(fileToCopy, destinationFile);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(frame, "Програма не може створити новий файл",
                                    "Повідомлення", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else {
                        if (fileToCopy.equals(destinationFile)) {
                            JOptionPane.showMessageDialog(frame, "Програма не може копіювати файл в самого себе",
                                    "Повідомлення", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            int reply = JOptionPane.showConfirmDialog(frame
                                    , "Ви хочете записати вміст файла " + fileToCopy.getName() + "\n в файл " + destinationFile.getName()
                                    , "", JOptionPane.YES_NO_OPTION);
                            if (reply == JOptionPane.YES_OPTION) {
                                copyWithoutMultipleLines(fileToCopy, destinationFile);
                            }
                        }
                    }
                }
            }
        }
    }

    private class CopyHtmlFileAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            MyTreeNode selectedNode = (MyTreeNode) tree.getLastSelectedPathComponent();
            if (selectedNode != null) {
                TreeFile selectedDirectory = (TreeFile) selectedNode.getUserObject();
                if (selectedDirectory.isDirectory()) {
                    if (fileToCopy != null) {
                        if (fileToCopy.canRead() && fileToCopy.getName().endsWith(".html")) {
                            TreeFile destinationFile = new TreeFile(selectedDirectory.getAbsolutePath() + File.separator + fileToCopy.getName());
                            if (!destinationFile.exists()) {
                                copyHtmlFile(fileToCopy, destinationFile);
                                insertFile(selectedNode, destinationFile);
                            } else {
                                JOptionPane.showMessageDialog(frame, "Файл вже існує в цій папці",
                                        "Повідомлення", JOptionPane.INFORMATION_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(frame, "Для цієї дії Ви повинні обрати файл HTML формату",
                                    "Повідомлення", JOptionPane.INFORMATION_MESSAGE);
                        }

                    } else {
                        if (fileToCut.canRead() && fileToCut.getName().endsWith(".html")) {
                            TreeFile destinationFile = new TreeFile(selectedDirectory.getAbsolutePath() + File.separator + fileToCut.getName());
                            if (!destinationFile.exists()) {
                                copyHtmlFile(fileToCut, destinationFile);
                                insertFile(selectedNode, destinationFile);
                                moveFileToTrash(fileToCut);
                                fileToCut = null;
                            } else {
                                JOptionPane.showMessageDialog(frame, "Файл вже існує в цій папці",
                                        "Повідомлення", JOptionPane.INFORMATION_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(frame, "Для цієї дії Ви повинні обрати файл HTML формату",
                                    "Повідомлення", JOptionPane.INFORMATION_MESSAGE);
                        }

                    }

                }
            }
        }
    }

    private void copyHtmlFile(File source, File destination) {
        try {
            Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
            EventQueue.invokeLater(() -> {
                TextEditor editor = new TextEditor(destination);
                editor.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                editor.highlightTitle();
                editor.setVisible(true);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copyWithoutMultipleLines(File source, File destination) {
        try (BufferedReader reader = new BufferedReader(new FileReader(source));
             BufferedWriter writer = new BufferedWriter(new FileWriter(destination))) {
            String prevLine = reader.readLine();
            String curLine = reader.readLine();
            if (prevLine != null) {
                writer.write(prevLine);
                writer.write('\n');
                while (curLine != null) {
                    if (!curLine.equals(prevLine)) {
                        writer.write(curLine);
                        writer.write('\n');
                        prevLine = curLine;
                    }
                    curLine = reader.readLine();
                }
            } else {
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
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
        return file.isFile() && file.canRead() && (file.getName().toLowerCase().endsWith(".txt")||file.getName().toLowerCase().endsWith(".html"));
    }

    private TreePath getPath(TreeNode treeNode) {
        TreeNode[] nodes = treeModel.getPathToRoot(treeNode);
        TreePath path = new TreePath(nodes);
        return path;
    }

    private boolean moveFileToTrash(File file) {
        FileUtils fileUtils = FileUtils.getInstance();
        try {
            if (file.exists()) {
                if (file.isFile()) {
                    fileUtils.moveToTrash(new File[]{file});
                    return true;
                } else {
                    int reply = JOptionPane.showConfirmDialog(frame, "Ви дійсно хочете перемістити цю папку в корзину?",
                            "Запит підтвердження", JOptionPane.YES_NO_OPTION);
                    if (reply == JOptionPane.YES_OPTION) {
                        fileUtils.moveToTrash(new File[]{file});
                        return true;
                    } else {
                        return false;
                    }
                }
            } else {
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isParent(File parent, File child) {
        do {
            if (child.equals(parent)) {
                return true;
            }
            child = child.getParentFile();
        } while (child != null);
        return false;
    }
}
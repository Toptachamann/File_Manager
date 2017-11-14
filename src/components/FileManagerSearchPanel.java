package components;

/*
 * Created by Timofey on 9/17/2017.
 */

import TextEditor.TextEditorFrame;
import auxiliary.MyTreeNode;
import auxiliary.TreeFile;
import com.sun.jna.platform.FileUtils;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;
import table_manager.TableEditorFrame;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FileManagerSearchPanel extends SearchPanel {

  private JPopupMenu popupMenu;

  private MyTreeNode cutFromNode;
  private TreeFile fileToCopy;
  private TreeFile fileToCut;

  private UserMessenger messenger;

  public FileManagerSearchPanel(JFrame frame) {
    super(frame);
    init();
    addComponents();
    addActions();
    addListeners();
  }

  private void init() {
    messenger = new UserMessenger(super.frame);
  }

  private void addComponents() {
    popupMenu = new FileManagerPopupMenu();
    tree.setComponentPopupMenu(popupMenu);
  }

  private void addActions() {
    Action copyAction = new CopyAction();
    Action cutAction = new CutAction();
    Action pasteAction = new PasteAction();
    Action deleteAction = new DeleteAction();
    Action clearContentAction = new ClearContentAction();
    Action copyAllWithExtensionAction = new CopyAllWithExtensionAction();

    InputMap inputMap = tree.getInputMap(JComponent.WHEN_FOCUSED);
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK), "Copy action");
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK), "Paste action");
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK), "Cut action");
    inputMap.put(KeyStroke.getKeyStroke("DELETE"), "Delete action");
    inputMap.put(KeyStroke.getKeyStroke("F3"), "Clear content action");
    inputMap.put(
        KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK),
        "Copy all with extension action");

    ActionMap actionMap = tree.getActionMap();
    actionMap.put("Copy action", copyAction);
    actionMap.put("Paste action", pasteAction);
    actionMap.put("Cut action", cutAction);
    actionMap.put("Delete action", deleteAction);
    actionMap.put("Clear content action", clearContentAction);
    actionMap.put("Copy all with extension action", copyAllWithExtensionAction);
  }

  private void addListeners() {
    MouseListener mouseListener =
        new MouseAdapter() {
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
        };
    fileList.addMouseListener(mouseListener);
    tree.addMouseListener(mouseListener);
  }

  @Nullable
  public TreeFile prepareToMove(TreeFile source, TreeFile targetFolder) throws IOException {
    Validate.isTrue(source.exists(), "Source should exist");
    Validate.isTrue(targetFolder.isDirectory(), "Folder to copy in should be an existing folder");
    if (isAncestor(source, targetFolder)) {
      messenger.fileIsAncestor(source, targetFolder);
      return null;
    }
    TreeFile childObject =
        new TreeFile(targetFolder.getAbsolutePath() + File.separator + source.getName());
    if (childObject.exists()) {
      int reply =
          source.isFile()
              ? messenger.wantsToOverwriteFile(childObject, targetFolder)
              : messenger.wantsToOverwriteDirectory(source, childObject, targetFolder);
      if (reply != JOptionPane.YES_OPTION) {
        return null;
      } else {
        moveFileToTrash(childObject);
        return childObject;
      }
    } else {
      return childObject;
    }
  }
  // copies all content of a source folder into target folder
  private void copyFolder(TreeFile sourceFolder, TreeFile targetFolder) throws IOException {
    if (sourceFolder.isFile()) {
      copyFile(sourceFolder, targetFolder);
    } else {
      Validate.isTrue(sourceFolder.isDirectory(), "Source should exist");
      if (targetFolder.exists()) {
        moveFileToTrash(targetFolder);
      }
      if (targetFolder.mkdir()) {
        File[] children = sourceFolder.listFiles(pathname -> true);
        if (children != null) {
          for (File child : children) {
            TreeFile treeChild = new TreeFile(child);
            TreeFile targetFile =
                new TreeFile(targetFolder.getAbsolutePath() + File.separator + treeChild.getName());
            copyFolder(treeChild, targetFile);
          }
        }
      } else {
        throw new IOException("Can't create " + targetFolder.getAbsolutePath());
      }
    }
  }
  // finished
  private void copyFile(TreeFile sourceFile, TreeFile targetFile) throws IOException {
    Validate.isTrue(sourceFile.isFile(), "Source should exist an existing file");
    if (targetFile.exists()) {
      moveFileToTrash(targetFile);
    }
    Files.copy(
        sourceFile.toPath(),
        targetFile.toPath(),
        StandardCopyOption.REPLACE_EXISTING,
        StandardCopyOption.COPY_ATTRIBUTES);
  }
  // moves all content of the source folder into target folder
  private void moveFolder(TreeFile sourceFolder, TreeFile targetFolder) throws IOException {
    if (sourceFolder.isFile()) {
      moveFile(sourceFolder, targetFolder);
    } else {
      if (targetFolder.exists()) {
        moveFileToTrash(targetFolder);
      }
      if (targetFolder.mkdir()) {
        File[] children = sourceFolder.listFiles();
        if (children != null) {
          for (File child : children) {
            TreeFile treeChild = new TreeFile(child);
            TreeFile destinationFile =
                new TreeFile(targetFolder.getAbsolutePath() + File.separator + treeChild.getName());
            moveFolder(treeChild, destinationFile);
          }
        }
        moveFileToTrash(sourceFolder);
      } else {
        throw new IOException("Can't create " + targetFolder.getAbsolutePath());
      }
    }
  }
  // finished
  private void moveFile(TreeFile source, TreeFile targetFile) throws IOException {
    Validate.isTrue(source.exists(), "Source should exist");
    Files.move(
        source.toPath(),
        targetFile.toPath(),
        StandardCopyOption.REPLACE_EXISTING);
  }

  private MyTreeNode getChildNodeWithFile(MyTreeNode parent, File child) {
    for (int i = 0; i < parent.getChildCount(); i++) {
      MyTreeNode node = (MyTreeNode) parent.getChildAt(i);
      TreeFile file = (TreeFile) node.getUserObject();
      if (file.equals(child)) {
        return node;
      }
    }
    return null;
  }

  // finished
  private TreePath getPath(TreeNode treeNode) {
    TreeNode[] nodes = treeModel.getPathToRoot(treeNode);
    return new TreePath(nodes);
  }
  // finished
  private void moveFileToTrash(File file) throws IOException {
    FileUtils fileUtils = FileUtils.getInstance();
    fileUtils.moveToTrash(new File[] {file});
  }
  // finished
  private boolean isAncestor(File parent, File child) {
    do {
      if (child.equals(parent)) {
        return true;
      }
      child = child.getParentFile();
    } while (child != null);
    return false;
  }
  // finished
  private void openTxtFile(File file) {
    EventQueue.invokeLater(
        () -> {
          try {
            TextEditorFrame textEditorFrame = new TextEditorFrame(file);
            textEditorFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            textEditorFrame.setVisible(true);
          } catch (IOException e) {
            messenger.textEditorFailed(file);
          }
        });
  }
  // finished
  private void openTableFile(File file) {
    EventQueue.invokeLater(
        () -> {
          try {
            TableEditorFrame textEditorFrame = new TableEditorFrame(file);
            textEditorFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            textEditorFrame.setVisible(true);
          } catch (IOException e) {
            messenger.tableEditorFailed(file);
          }
        });
  }
  // finished
  private class FileManagerPopupMenu extends JPopupMenu {
    public FileManagerPopupMenu() {
      setComponentPopupMenu(popupMenu);
      JMenu openWithItem = new JMenu("Open with");
      JMenu newObjectMenu = new JMenu("Add");
      JMenuItem copyItem = new JMenuItem("Copy");
      JMenuItem pasteItem = new JMenuItem("Paste");
      JMenuItem cutItem = new JMenuItem("Cut");
      JMenuItem deleteItem = new JMenuItem("Delete");

      add(openWithItem);
      add(copyItem);
      add(pasteItem);
      add(cutItem);
      add(deleteItem);
      add(newObjectMenu);

      JMenuItem inTextEditor = new JMenuItem("Text Editor");
      JMenuItem inTableEditor = new JMenuItem("Table Editor");
      inTextEditor.addActionListener(new OpenTextFileAction());
      inTableEditor.addActionListener(new OpenTableAction());
      openWithItem.add(inTextEditor);
      openWithItem.add(inTableEditor);

      JMenuItem newFolderItem = new JMenuItem("Folder");
      JMenuItem newJsonItem = new JMenuItem("Json file (*.json");
      JMenuItem newHtmlFile = new JMenuItem("HTML file (*.html)");
      JMenuItem newTextFile = new JMenuItem("Text file (*txt)");

      newObjectMenu.add(newFolderItem);
      newObjectMenu.add(newJsonItem);
      newObjectMenu.add(newHtmlFile);
      newObjectMenu.add(newTextFile);

      copyItem.addActionListener(new CopyAction());
      pasteItem.addActionListener(new PasteAction());
      cutItem.addActionListener(new CutAction());
      deleteItem.addActionListener(new DeleteAction());
      newFolderItem.addActionListener(new ItemAction("", true));
      newJsonItem.addActionListener(new ItemAction(".json", false));
      newTextFile.addActionListener(new ItemAction(".txt", false));
      newHtmlFile.addActionListener(new ItemAction(".html", false));
    }
  }
  // finished
  private class OpenTextFileAction extends AbstractAction {

    @Override
    public void actionPerformed(ActionEvent e) {
      MyTreeNode selectedNode = (MyTreeNode) tree.getLastSelectedPathComponent();
      if (selectedNode != null) {
        TreeFile selectedFile = (TreeFile) selectedNode.getUserObject();
        openTxtFile(selectedFile);
      }
    }
  }
  // finished
  private class OpenTableAction extends AbstractAction {

    @Override
    public void actionPerformed(ActionEvent e) {
      MyTreeNode selectedNode = (MyTreeNode) tree.getLastSelectedPathComponent();
      if (selectedNode != null) {
        TreeFile selectedFile = (TreeFile) selectedNode.getUserObject();
        openTableFile(selectedFile);
      }
    }
  }
  // finished
  public class DeleteAction extends AbstractAction {
    @Override
    public void actionPerformed(ActionEvent e) {
      MyTreeNode selectedNode = (MyTreeNode) tree.getLastSelectedPathComponent();
      if (selectedNode != null) {
        File selectedFile = (File) selectedNode.getUserObject();
        try {
          MyTreeNode parentNode = (MyTreeNode) selectedNode.getParent();
          moveFileToTrash(selectedFile);
          treeModel.removeNodeFromParent(selectedNode);
          tree.setSelectionPath(getPath(parentNode));
        } catch (IOException ex) {
          messenger.moveToTrashFailed(selectedFile);
        }
      }
    }
  }
  // finished
  public class CopyAction extends AbstractAction {
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

  public class PasteAction extends AbstractAction {

    @Override
    public void actionPerformed(ActionEvent e) {
      MyTreeNode selectedNode = (MyTreeNode) tree.getLastSelectedPathComponent();
      if (selectedNode != null) {
        TreeFile folderToCopyIn = (TreeFile) selectedNode.getUserObject();
        if (!folderToCopyIn.isDirectory()) {
          messenger.selectDirectory();
        } else {
          if (fileToCopy != null) {
            try {
              TreeFile destinationFile = prepareToMove(fileToCopy, folderToCopyIn);
              if (destinationFile != null) {
                copyFolder(fileToCopy, destinationFile);
                updateNode(selectedNode);
                MyTreeNode copiedNode = getChildNodeWithFile(selectedNode, destinationFile);
                tree.setSelectionPath(getPath(copiedNode));
                updateFileList(selectedNode);
              }
            } catch (IOException ex) {
              ex.printStackTrace();
              messenger.copyFileFailed(fileToCopy, folderToCopyIn);
            }
          } else if (cutFromNode != null) {
            try {
              TreeFile destinationFile = prepareToMove(fileToCut, folderToCopyIn);
              if (destinationFile != null) {
                moveFolder(fileToCut, destinationFile);
                updateNode(selectedNode);
                MyTreeNode movedNode = getChildNodeWithFile(selectedNode, destinationFile);
                tree.setSelectionPath(getPath(movedNode));
                updateFileList(movedNode);
                treeModel.removeNodeFromParent(cutFromNode);
                cutFromNode = null;
                fileToCut = null;
              }
            } catch (IOException ex) {
              ex.printStackTrace();
              messenger.moveFileFailed(fileToCut, folderToCopyIn);
            }
          }
        }
      }
    }
  }
  // finished
  public class CutAction extends AbstractAction {
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
  // finished
  private class ClearContentAction extends AbstractAction {
    @Override
    public void actionPerformed(ActionEvent e) {
      MyTreeNode selectedNode = (MyTreeNode) tree.getLastSelectedPathComponent();
      if (selectedNode != null) {
        TreeFile file = (TreeFile) selectedNode.getUserObject();
        if (file.isFile() && file.getName().toLowerCase().endsWith(".txt")) {
          int reply = messenger.wantsToClear();
          if (reply == JOptionPane.YES_OPTION) {
            try {
              BufferedWriter writer = new BufferedWriter(new FileWriter(file));
              writer.write("");
            } catch (IOException ex) {
              ex.printStackTrace();
              messenger.clearContentFailed();
            }
          }
        } else {
          messenger.canClearOnlyTxt();
        }
      }
    }
  }

  public class CopyAllWithExtensionAction extends AbstractAction {
    @Override
    public void actionPerformed(ActionEvent e) {
      if (fileToCopy != null) {
        MyTreeNode selectedNode = (MyTreeNode) tree.getLastSelectedPathComponent();
        if (selectedNode != null) {
          MyTreeNode parentNode = (MyTreeNode) selectedNode.getParent();
          TreeFile selectedDirectory = (TreeFile) parentNode.getUserObject();
          if (selectedDirectory.isDirectory()) {
            try {
              String selectedFileType = (String) extensionBox.getSelectedItem();
              String extension = getExtension(selectedFileType);
              File[] filesWithSpecifiedExtension =
                  fileToCopy.listFiles((dir, name) -> name.toLowerCase().endsWith(extension));
              if (filesWithSpecifiedExtension != null) {
                for (File file : filesWithSpecifiedExtension) {
                  TreeFile treeFile = new TreeFile(file.getAbsolutePath());
                  TreeFile destination = prepareToMove(treeFile, selectedDirectory);
                  if (destination != null) {
                    copyFolder(treeFile, destination);
                    updateNode(parentNode);
                  }
                }
              }
            } catch (IOException ex) {
              ex.printStackTrace();
              // need to message about it
            }
          }
        }
      }
    }
  }
}

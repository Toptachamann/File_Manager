package components;

import actions.RenameAction;
import auxiliary.GBC;
import auxiliary.MyTreeNode;
import auxiliary.TreeFile;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class SearchPanel extends JPanel {

  protected static final String[] extensions = {
    "All files (*.*)",
    "Normal text file (*.txt)",
    "C# source file (*.cs)",
    "Java source file (*.java)",
    "JSON file (*.json)",
    "HTML file (*.html)",
    "PDF file (*.pdf)",
    "Python source file (*.py)",
    "XML file (*.xml)"
  };
  protected static final Pattern extensionPattern = Pattern.compile("\\*.([a-z]+|\\*)");
  protected static final Font PANEL_FONT = new Font("Arial", Font.BOLD, 12);
  protected JFrame frame;
  protected JSplitPane splitPane;
  protected JPanel extensionPanel;
  protected JTree tree;
  protected DefaultTreeModel treeModel;
  protected JList fileList;
  protected DefaultListModel<String> fileListModel;
  protected JLabel extensionHint;
  protected JComboBox<String> extensionBox;
  protected TreeFile selectedDirectory;
  protected TreeFile lastSelectedFile;

  public SearchPanel(JFrame frame) {
    this.frame = frame;
    setLayout(new GridBagLayout());

    createTree();
    addListenersToTree();
    createList();
    createComboBox();

    JScrollPane fileTreeScrollPane = new JScrollPane(tree);
    JScrollPane fileListScrollPane = new JScrollPane(fileList);

    splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, fileTreeScrollPane, fileListScrollPane);
    splitPane.setResizeWeight(0.5);
    this.add(splitPane, new GBC(0, 0, 1, 1, 1, 1).setFill(GBC.BOTH));

    extensionHint = new JLabel("Extension:");
    extensionHint.setFont(PANEL_FONT);
    extensionPanel = new JPanel();
    extensionPanel.add(extensionHint);
    extensionPanel.add(extensionBox);
    this.add(
        extensionPanel, new GBC(0, 1, 1, 1, 0, 0).setAnchor(GBC.CENTER).setInsets(5, 0, 5, 10));

    addAllowedAction();
  }

  private void createComboBox() {
    extensionBox = new JComboBox<>(extensions);
    extensionBox.setFont(PANEL_FONT);
    extensionBox.addActionListener(
        (event) -> {
          MyTreeNode selectedNode = (MyTreeNode) tree.getLastSelectedPathComponent();
          this.updateFileList(selectedNode);
        });
  }

  private void createTree() {
    TreeFile invisibleRoot = new TreeFile("");
    MyTreeNode invisibleRootNode = new MyTreeNode(invisibleRoot);
    addRoots(invisibleRootNode);
    treeModel = new DefaultTreeModel(invisibleRootNode);
    treeModel.setAsksAllowsChildren(true);
    tree = new JTree(treeModel);
    tree.setEditable(true);
    MyTreeCellEditor editor =
        new MyTreeCellEditor(tree, (DefaultTreeCellRenderer) tree.getCellRenderer());
    tree.setCellEditor(editor);
    treeModel.addTreeModelListener(new MyTreeModelListener());
    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    tree.expandRow(0);
    tree.setRootVisible(false);
  }

  private void createList() {
    fileListModel = new DefaultListModel<>();
    fileListModel.addElement("Folder not selected");
    fileList = new JList<>(fileListModel);
    fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
  }

  private void addListenersToTree() {
    TreeWillExpandListener treeWillExpandListener =
        new TreeWillExpandListener() {
          @Override
          public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
            TreePath pathToNode = event.getPath();
            if (tree.isExpanded(pathToNode)) {
              treeWillCollapse(event);
            } else {
              MyTreeNode node = (MyTreeNode) pathToNode.getLastPathComponent();
              addOneLevel(node);
              if (node.getChildCount() > 0) {
                tree.setSelectionPath(pathToNode.pathByAddingChild(node.getFirstChild()));
              } else {
                tree.setSelectionPath(pathToNode);
              }
            }
          }

          @Override
          public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
            TreePath pathToNode = event.getPath();
            if (tree.isCollapsed(pathToNode)) {
              treeWillExpand(event);
            }
          }
        };
    tree.addTreeWillExpandListener(treeWillExpandListener);
    tree.addTreeSelectionListener(
        e -> {
          MyTreeNode selectedNode = (MyTreeNode) tree.getLastSelectedPathComponent();
          if (selectedNode != null) {
            lastSelectedFile = (TreeFile) selectedNode.getUserObject();
            if (lastSelectedFile.isDirectory()) {
              updateFileList(selectedNode);
            }
          }
        });
    tree.setCellRenderer(new FileCellRenderer());
  }

  private void addAllowedAction() {
    Action renameAction = new RenameAction(this);

    InputMap inputMap = tree.getInputMap(JComponent.WHEN_FOCUSED);

    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK), "Rename action");

    ActionMap actionMap = tree.getActionMap();
    actionMap.put("Rename action", renameAction);
  }

  protected boolean contains(File parent, @NotNull String child) {
    return new File(parent, child).exists();
  }

  protected boolean contains(File parent, File child) {
    return new File(parent, child.getName()).exists();
  }

  protected void addOneLevel(MyTreeNode node) {
    if (node.isLoaded()) {
      return;
    }
    node.setLoaded(true);
    TreeFile file = (TreeFile) node.getUserObject();
    if (file.isDirectory()) {
      File[] children = file.listFiles((fileName) -> !fileName.isHidden());
      if (children != null) {
        for (File child : children) {
          TreeFile treeChild = new TreeFile(child.toString());
          MyTreeNode childNode;
          if (treeChild.isDirectory()) {
            childNode = new MyTreeNode(treeChild, true);
          } else {
            childNode = new MyTreeNode(treeChild, false);
          }
          treeModel.insertNodeInto(childNode, node, node.getChildCount());
        }
      }
    }
  }

  private void addRoots(MyTreeNode invisibleRootNode) {
    File[] roots = File.listRoots();
    for (File root : roots) {
      TreeFile treeFileRoot = new TreeFile(root);
      if (treeFileRoot.exists()) {
        MyTreeNode rootNode = new MyTreeNode(treeFileRoot);
        invisibleRootNode.add(rootNode);
      }
    }
  }

  public void startRenameAct() {
    MyTreeNode selectedNode = (MyTreeNode) tree.getLastSelectedPathComponent();
    if (selectedNode != null) {
      TreeNode[] nodes = treeModel.getPathToRoot(selectedNode);
      TreePath path = new TreePath(nodes);
      tree.startEditingAtPath(path);
    }
  }

  private void folderNotSelected() {
    JOptionPane.showMessageDialog(
        frame, "Folder not selected", "Message", JOptionPane.INFORMATION_MESSAGE);
  }

  private void validateNodeTreeFile(File file) {
    Validate.notNull(file, "Tree file is null");
  }

  private void objectCreationFailed(){
    JOptionPane.showMessageDialog(
        frame, "Object creation failed", "Message", JOptionPane.INFORMATION_MESSAGE);
  }

  @Nullable
  private TreeFile addNewFileToDirectory(File parentDirectory, String extension, boolean isDirectory) {
    Validate.isTrue(parentDirectory.isDirectory(), "Can't crete file in another file");
    String untitled = "Untitled";
    String newTitle = untitled;
    int i = 0;
    while (contains(parentDirectory, newTitle + extension)) {
      i += 1;
      newTitle = untitled + '(' + i + ')';
    }
    newTitle = newTitle + extension;
    TreeFile newUntitledFile =
        new TreeFile(parentDirectory.getAbsolutePath() + File.separator + newTitle);
    boolean isCreated = false;
    if (isDirectory) {
      isCreated = newUntitledFile.mkdir();
    } else {
      try {
        isCreated = newUntitledFile.createNewFile();
      } catch (IOException e) {
      }
    }
    return isCreated ? newUntitledFile : null;
  }

  private void insertFileAsChild(@NotNull File child,@NotNull DefaultMutableTreeNode parentNode){
    Validate.notNull(child, "Child must be not null");
    Validate.notNull(child, "parentNode must be not null");
  }

  protected void addUntitledFileToSystem(@NotNull String extension, boolean isDirectory) {
    Validate.notNull(extension, "Extension must be not null");
    MyTreeNode selectedNode = (MyTreeNode) tree.getLastSelectedPathComponent();
    if (selectedNode == null) {
      folderNotSelected();
    } else {
      TreeFile selectedFile = (TreeFile) selectedNode.getUserObject();
      validateNodeTreeFile(selectedFile);
      if (selectedFile.isDirectory()) {
        TreeFile createdFile = addNewFileToDirectory(selectedDirectory, extension, isDirectory);
        if(createdFile != null){
          MyTreeNode createdNode = new MyTreeNode(createdFile, isDirectory);
          treeModel.insertNodeInto(createdNode, selectedNode, selectedNode.getChildCount());
          TreeNode[] nodes = treeModel.getPathToRoot(createdNode);
          TreePath path = new TreePath(nodes);
          tree.scrollPathToVisible(path);
          tree.setSelectionPath(path);
          tree.startEditingAtPath(path);
          lastSelectedFile = createdFile;
        } else {
          objectCreationFailed();
        }
      } else {
        JOptionPane.showMessageDialog(
            frame,
            "Choose a folder to create a new object",
            "Message",
            JOptionPane.INFORMATION_MESSAGE);
      }
    }
  }

  protected void updateFileList(MyTreeNode selectedNode) {
    if (selectedNode != null) {
      TreeFile selectedFile = (TreeFile) selectedNode.getUserObject();
      updateFileList(selectedFile);
    }
  }

  protected void updateFileList(TreeFile selectedFile) {
    String selectedItem = (String) extensionBox.getSelectedItem();
    String extension = getExtension(selectedItem);
    this.updateFileList(selectedFile, extension);
  }

  protected String getExtension(String fileType) {
    Matcher matcher = extensionPattern.matcher(fileType);
    if (matcher.find()) {
      String extension = matcher.group(1);
      if (extension.equals("*")) {
        return "";
      } else {
        return extension;
      }
    } else {
      return null;
    }
  }

  protected void updateFileList(TreeFile newParentDirectory, String suffix) {
    if (newParentDirectory.isDirectory()) {
      selectedDirectory = newParentDirectory;
      File[] targetFiles =
          newParentDirectory.listFiles(
              (file) -> !file.isDirectory() && file.getName().toLowerCase().endsWith(suffix));
      fileListModel.removeAllElements();
      if (newParentDirectory.isRoot()) {
        fileListModel.addElement("Files on disk " + newParentDirectory.getAbsolutePath());
      } else if (suffix.isEmpty()) {
        fileListModel.addElement("Files in folder " + newParentDirectory.getName());
      } else {
        fileListModel.addElement(
            "Files in folder " + newParentDirectory.getName() + " with extension " + suffix);
      }
      if (targetFiles != null) {
        for (File file : targetFiles) {
          fileListModel.addElement(file.getName());
        }
        fileList.revalidate();
        fileList.repaint();
      }
    }
  }

  private class FileCellRenderer implements TreeCellRenderer{
    private Font defaultFont;
    private DefaultTreeCellRenderer defaultRenderer;
    public FileCellRenderer(){
      defaultFont = new Font("Arial", Font.PLAIN, 10);
      defaultRenderer = new DefaultTreeCellRenderer();
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
      if(value instanceof DefaultMutableTreeNode){
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        File file = (File) node.getUserObject();
        validateNodeTreeFile(file);
        JLabel nameLabel = new JLabel(file.getName());
        nameLabel.setFont(defaultFont);
        return nameLabel;
      }else{
        return defaultRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
      }
    }
  }

  private class MyTreeCellEditor extends DefaultTreeCellEditor {

    public MyTreeCellEditor(JTree tree, DefaultTreeCellRenderer renderer) {
      super(tree, renderer);
    }

    public MyTreeCellEditor(JTree tree, DefaultTreeCellRenderer renderer, TreeCellEditor editor) {
      super(tree, renderer, editor);
    }

    @Override
    public Component getTreeCellEditorComponent(
        JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
      if (value instanceof TreeFile) {
        value = ((TreeFile) value).getName();
      }
      return super.getTreeCellEditorComponent(tree, value, isSelected, expanded, leaf, row);
    }
  }

  private class MyTreeModelListener implements TreeModelListener {

    @Override
    public void treeNodesChanged(TreeModelEvent e) {
      MyTreeNode changedNode = (MyTreeNode) tree.getLastSelectedPathComponent();
      if (changedNode != null) {
        Object userObject = changedNode.getUserObject();
        if (userObject instanceof String) {
          String newFileName = (String) userObject;
          MyTreeNode parentNode = (MyTreeNode) changedNode.getParent();
          TreeFile parentFile = (TreeFile) parentNode.getUserObject();
          TreeFile newFileInstance =
              new TreeFile(parentFile.getAbsolutePath() + File.separator + newFileName);
          if (newFileInstance.exists()) {
            changedNode.setUserObject(lastSelectedFile);
            if (!lastSelectedFile.equals(newFileInstance)) {
              JOptionPane.showMessageDialog(
                  frame,
                  "An object with the same name already exists in this folder",
                  "Message",
                  JOptionPane.INFORMATION_MESSAGE);
            }
          } else if (lastSelectedFile.renameTo(newFileInstance)) {
            changedNode.setUserObject(newFileInstance);
            lastSelectedFile = newFileInstance;
          } else {
            JOptionPane.showMessageDialog(
                frame, "Rename failed", "Message", JOptionPane.INFORMATION_MESSAGE);
            changedNode.setUserObject(lastSelectedFile);
          }
        }
      }
    }

    @Override
    public void treeNodesInserted(TreeModelEvent e) {}

    @Override
    public void treeNodesRemoved(TreeModelEvent e) {}

    @Override
    public void treeStructureChanged(TreeModelEvent e) {}
  }

  protected class ItemAction extends AbstractAction {

    public ItemAction(String extension, boolean isDirectory) {
      putValue("Extension", extension);
      putValue("Is directory", isDirectory);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      String extension = (String) getValue("Extension");
      Boolean isDirectory = (Boolean) getValue("Is directory");
      addUntitledFileToSystem(extension, isDirectory);
    }
  }
}

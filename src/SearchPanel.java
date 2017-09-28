
import auxiliary.GBC;
import auxiliary.MyTreeNode;
import auxiliary.TreeFile;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class SearchPanel extends JPanel {

    private static final Font PANEL_FONT = new Font("Arial", Font.BOLD, 12);
    protected static final String[] extensions = {"All files (*.*)", "Normal text file (*.txt)", "C# source file (*.cs)", "Java source file (*.java)", "JSON file (*.json)",
            "HTML file (*.html)", "PDF file (*.pdf)", "Python source file (*.py)", "XML file (*.xml)"};

    protected JFrame frame;
    protected JSplitPane splitPane;
    protected JPanel extensionPanel;

    protected JScrollPane fileTreeScrollPane;
    protected JTree tree;
    protected DefaultTreeModel treeModel;

    protected JScrollPane fileListScrollPane;
    protected JList fileList;
    protected DefaultListModel<String> fileListModel;

    protected JLabel extensionHint;
    protected JComboBox<String> extensionBox;
    protected static final Pattern extensionPattern = Pattern.compile("\\*.([a-z]+|\\*)");

    protected TreeFile selectedDirectory;

    public SearchPanel(JFrame frame) {
        this.frame = frame;
        setLayout(new GridBagLayout());

        TreeFile invisibleRoot = new TreeFile("");
        MyTreeNode invisibleRootNode = new MyTreeNode(invisibleRoot);
        addRoots(invisibleRootNode);
        TreeWillExpandListener treeWillExpandListener = new TreeWillExpandListener() {
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
        treeModel = new DefaultTreeModel(invisibleRootNode);
        treeModel.setAsksAllowsChildren(true);
        tree = new JTree(treeModel);
        tree.addTreeWillExpandListener(treeWillExpandListener);
        tree.addTreeSelectionListener(e -> {
            MyTreeNode selectedNode = (MyTreeNode) tree.getLastSelectedPathComponent();
            updateFileList(selectedNode);
        });
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        fileTreeScrollPane = new JScrollPane(tree);
        tree.expandRow(0);
        tree.setRootVisible(false);

        fileListModel = new DefaultListModel<>();
        fileListModel.addElement("");
        fileList = new JList<>(fileListModel);
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fileListScrollPane = new JScrollPane();
        fileListScrollPane.getViewport().add(fileList);

        extensionHint = new JLabel("Extension:");
        extensionHint.setFont(PANEL_FONT);

        extensionBox = new JComboBox<>(extensions);
        extensionBox.setFont(PANEL_FONT);
        extensionBox.addActionListener((event) -> {
            MyTreeNode selectedNode = (MyTreeNode) tree.getLastSelectedPathComponent();
            this.updateFileList(selectedNode);

        });
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, fileTreeScrollPane, fileListScrollPane);
        splitPane.setResizeWeight(0.5);

        extensionPanel = new JPanel();
        extensionPanel.add(extensionHint);
        extensionPanel.add(extensionBox);

        this.add(splitPane, new GBC(0, 0, 1, 1, 1, 1).setFill(GBC.BOTH));
        this.add(extensionPanel, new GBC(0, 1, 1, 1, 0, 0).setAnchor(GBC.CENTER).setInsets(5, 0, 5, 10));

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

    protected void addUntitledFileToSystem(String extension, boolean isDirectory) {
        try {
            MyTreeNode selectedNode = (MyTreeNode) tree.getLastSelectedPathComponent();
            if (selectedNode == null) {
                JOptionPane.showMessageDialog(frame, "Ви не вказали папку", "Повідомлення", JOptionPane.INFORMATION_MESSAGE);
            } else {
                TreeFile selectedFile = (TreeFile) selectedNode.getUserObject();
                if (selectedFile.isDirectory()) {
                    String untitled = "Untitled";
                    if (contains(selectedFile, untitled + extension)) {
                        int i = 0;
                        String newTitle;
                        do {
                            i += 1;
                            newTitle = untitled + '(' + i + ')';
                        } while (!contains(selectedFile, newTitle + extension));
                        untitled = untitled + '(' + i + ')';
                    }
                    untitled = untitled + extension;
                    TreeFile fileToAdd = new TreeFile(selectedFile.getAbsolutePath() + "\\" + untitled);
                    boolean isCreated;
                    MyTreeNode newNode;
                    if (isDirectory) {
                        isCreated = fileToAdd.mkdir();
                        newNode = new MyTreeNode(fileToAdd, true);
                    } else {
                        isCreated = fileToAdd.createNewFile();
                        newNode = new MyTreeNode(fileToAdd, false);
                    }
                    if (isCreated) {
                        treeModel.insertNodeInto(newNode, selectedNode, selectedNode.getChildCount());
                    } else {
                        JOptionPane.showMessageDialog(frame, "Програма не може створити об'єкт в обраній папці", "Повідомлення", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Для створення нового об'єкту оберіть папку", "Повідомлення", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected boolean contains(File parent, String child) {
        File childFile = new File(parent.getAbsolutePath() + "\\" + child);
        return contains(parent, childFile);
    }

    protected boolean contains(File parent, File child) {
        if (parent.isDirectory()) {
            File[] files = parent.listFiles((dir, name) -> true);
            try {
                for (File file : files) {
                    if (child.getCanonicalPath().equals(file.getCanonicalPath())) {
                        return true;
                    }
                }
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return true;
            }
        } else {
            return false;
        }
    }

    private void addRoots(MyTreeNode invisibleRootNode) {
        File[] roots = File.listRoots();
        for (File root : roots) {
            TreeFile treeRoot = new TreeFile(root.toString());
            if (treeRoot.exists()) {
                MyTreeNode rootNode = new MyTreeNode(treeRoot);
                invisibleRootNode.add(rootNode);
            }
        }
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
                    node.add(childNode);
                }
            }
        }
        treeModel.reload();
    }

    protected void updateFileList(MyTreeNode selectedNode) {
        if (selectedNode != null) {
            TreeFile selectedFile = (TreeFile) selectedNode.getUserObject();
            updateFileList(selectedFile);
        }
    }

    protected void updateFileList(TreeFile selectedFile) {
        String selectedItem = (String) extensionBox.getSelectedItem();
        Matcher matcher = extensionPattern.matcher(selectedItem);
        matcher.find();
        String extension = matcher.group(1);
        if (extension.equals("*")) {
            this.updateFileList(selectedFile, "");
        } else {
            this.updateFileList(selectedFile, extension);
        }
    }

    protected void updateFileList(TreeFile newParentDirectory, String suffix) {
        if (newParentDirectory.isDirectory()) {
            selectedDirectory = newParentDirectory;
            File[] targetFiles = newParentDirectory.listFiles((file) -> !file.isDirectory() && file.getName().toLowerCase().endsWith(suffix));
            if (targetFiles != null) {
                fileListModel.removeAllElements();
                for (File targetFile : targetFiles) {
                    fileListModel.addElement(targetFile.getName());
                }
                fileListScrollPane.revalidate();
                fileListScrollPane.repaint();
            }
        }
    }
}
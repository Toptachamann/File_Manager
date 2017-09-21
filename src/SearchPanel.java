import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchPanel extends JPanel {
    JFrame frame;

    protected JScrollPane fileTreeScrollPane, fileListScrollPane;
    protected JTree tree;
    protected DefaultTreeModel treeModel;

    protected JList fileList;
    protected DefaultListModel<String> fileListModel;
    protected JComboBox<String> extensionBox;
    protected final String[] extensions = {"All files (*.*)", "Normal text file (*.txt)", "C# source file (*.cs)", "Java source file (*.java)", "JSON file (*.json)",
            "HTML file (*.html)", "PDF file (*.pdf)", "Python source file (*.py)", "XML file (*.xml)"};
    protected final Pattern extensionPattern = Pattern.compile("\\*.([a-z]+|\\*)");

    protected TreeFile selectedDirectory;

    protected void addComponents() {
        this.setLayout(new GridBagLayout());

        TreeFile virtualRoot = new TreeFile("");
        MyTreeNode virtualRootNode = new MyTreeNode(virtualRoot);
        treeModel = new DefaultTreeModel(virtualRootNode);
        File[] roots = File.listRoots();
        for (File root : roots) {
            TreeFile treeRoot = new TreeFile(root.toString());
            if (treeRoot.exists()) {
                MyTreeNode rootNode = new MyTreeNode(treeRoot);
                virtualRootNode.add(rootNode);
            }
        }
        treeModel.setAsksAllowsChildren(true);
        tree = new JTree(treeModel);
        tree.addTreeWillExpandListener(new TreeWillExpandListener() {
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
        });
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


        Font boxAndHintFont = new Font("Arial", Font.BOLD, 12);

        JLabel extensionHint = new JLabel("Extension:");
        extensionHint.setFont(boxAndHintFont);

        extensionBox = new JComboBox<>(extensions);
        extensionBox.setFont(boxAndHintFont);
        extensionBox.addActionListener((event) -> {
            MyTreeNode selectedNode = (MyTreeNode) tree.getLastSelectedPathComponent();
            this.updateFileList(selectedNode);

        });
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, fileTreeScrollPane, fileListScrollPane);
        splitPane.setResizeWeight(0.5);
        this.add(splitPane, new GBC(0, 0, 1, 1, 1, 1).setFill(GBC.BOTH));

        JPanel extensionPanel = new JPanel();
        extensionPanel.add(extensionHint);
        extensionPanel.add(extensionBox);
        this.add(extensionPanel, new GBC(0, 1, 1, 1, 0, 0).setAnchor(GBC.CENTER).setInsets(5, 0, 5, 10));

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
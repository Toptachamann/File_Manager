import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.regex.*;

/**
 * Created by Timofey on 9/17/2017.
 */
abstract class SearchPanel extends JPanel {
    protected JScrollPane fileTreeScrollPane, fileListScrollPane;
    protected JTree tree;
    protected DefaultTreeModel treeModel;

    protected JList fileList;
    protected DefaultListModel<String> fileListModel;
    protected JComboBox<String> extensionBox;
    protected final String[] extensions = {"All files (*.*)", "Normal text file (*.txt)", "C# source file (*.cs)", "Java source file (*.java)", "JSON file (*.json)",
            "HTML file (*.html)", "PDF file (*.pdf)", "Python source file (*.py)", "XML file (*.xml)"};
    protected final Pattern extensionPattern = Pattern.compile("\\*.([a-z]+|\\*)");

    private JPopupMenu popupMenu;

    protected TreeFile selectedDirectory;
    private File fileToCopy;
    private File fileToCut;


    public SearchPanel() {
        this.setLayout(new GridBagLayout());

        TreeFile virtualRoot = new TreeFile("");
        TreeNode virtualRootNode = new TreeNode(virtualRoot);
        treeModel = new DefaultTreeModel(virtualRootNode);
        File[] roots = File.listRoots();
        for (File root : roots) {
            TreeFile treeRoot = new TreeFile(root.toString());
            if (treeRoot.exists()) {
                TreeNode rootNode = new TreeNode(treeRoot);
                addOneLevel(rootNode);
                virtualRootNode.add(rootNode);
            }
        }

        tree = new JTree(treeModel);
        tree.addTreeWillExpandListener(new TreeWillExpandListener() {
            @Override
            public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
                TreePath pathToNode = event.getPath();
                if (tree.isExpanded(pathToNode)) {
                    treeWillCollapse(event);
                } else {
                    TreeNode node = (TreeNode) pathToNode.getLastPathComponent();
                    int childCount = node.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        TreeNode child = (TreeNode) treeModel.getChild(node, i);
                        if (!child.isLoaded()) {
                            addOneLevel(child);
                        }
                    }
                    tree.setSelectionPath(pathToNode.pathByAddingChild(node.getFirstChild()));
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
            TreeNode selectedNode = (TreeNode) tree.getLastSelectedPathComponent();
            updateFileList(selectedNode);
        });
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        fileTreeScrollPane = new JScrollPane(tree);
        this.add(fileTreeScrollPane, new GBC(0, 0, 1, 1, 2, 1).setFill(GBC.BOTH));
        tree.expandRow(0);
        tree.setRootVisible(false);

        fileListModel = new DefaultListModel<>();
        fileListModel.addElement("");
        fileList = new JList<>(fileListModel);
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fileListScrollPane = new JScrollPane();
        fileListScrollPane.getViewport().add(fileList);

        this.add(fileListScrollPane, new GBC(1, 0, 1, 1, 1, 1).setFill(GBC.BOTH));

        Font boxAndHintFont = new Font("Arial", Font.BOLD, 12);

        JLabel extensionHint = new JLabel("Extension:");
        extensionHint.setFont(boxAndHintFont);

        extensionBox = new JComboBox<>(extensions);
        extensionBox.setFont(boxAndHintFont);
        extensionBox.addActionListener((event) -> {
            TreeNode selectedNode = (TreeNode) tree.getLastSelectedPathComponent();
            this.updateFileList(selectedNode);

        });

        popupMenu = new MyPopupMenu();

        this.setComponentPopupMenu(popupMenu);
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

        addActionsForFileList();
        addActionsForFileTree();

        this.add(extensionHint, new GBC(0, 1, 1, 1, 0, 0).setAnchor(GBC.EAST).setInsets(5, 0, 5, 10));
        this.add(extensionBox, new GBC(1, 1, 1, 1, 0, 0).setAnchor(GBC.WEST).setInsets(5, 0, 5, 0));
    }

    private void addActionsForFileList() {
        InputMap inputMap = fileListScrollPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke("ctrl O"), "Open action");
        inputMap.put(KeyStroke.getKeyStroke("ctrl C"), "Copy action");
        inputMap.put(KeyStroke.getKeyStroke("ctrl V"), "Paste action");
        inputMap.put(KeyStroke.getKeyStroke("ctrl X"), "Cut Action");

        ActionMap actionMap = fileListScrollPane.getActionMap();
        actionMap.put("Open action", new AbstractAction() {
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
        });
        actionMap.put("Copy action", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fileName = (String) fileList.getSelectedValue();
                if (fileName != null && selectedDirectory != null) {
                    fileToCopy = new File(selectedDirectory.getAbsolutePath() + "\\" + fileName);
                    fileToCut = null;
                }
            }
        });
        actionMap.put("Cut action", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fileName = (String) fileList.getSelectedValue();
                if (fileName != null && selectedDirectory != null) {
                    fileToCut = new File(selectedDirectory.getAbsolutePath() + "\\" + fileName);
                    fileToCopy = null;
                }
            }
        });
        actionMap.put("Paste action", new AbstractAction() {
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
        });
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
        InputMap inputMap = fileTreeScrollPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke("ctrl O"), "Open action");
        inputMap.put(KeyStroke.getKeyStroke("ctrl C"), "Copy action");
        inputMap.put(KeyStroke.getKeyStroke("ctrl V"), "Paste action");
        inputMap.put(KeyStroke.getKeyStroke("ctrl X"), "Cut Action");

        ActionMap actionMap = fileTreeScrollPane.getActionMap();
        actionMap.put("Open action", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (selectedNode != null) {
                    File selectedFile = (File) selectedNode.getUserObject();
                    if (canOpen(selectedFile)) {
                        openFile(selectedFile);
                    }
                }
            }
        });
        actionMap.put("Copy action", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (selectedNode != null) {
                    fileToCopy = (File) selectedNode.getUserObject();
                    fileToCut = null;
                }
            }
        });
        actionMap.put("Cut action", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (selectedNode != null) {
                    fileToCut = (File) selectedNode.getUserObject();
                    fileToCopy = null;
                }
            }
        });
        actionMap.put("Paste action", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fileToCopy != null || fileToCut != null) {
                    DefaultMutableTreeNode selectedDirectory = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                    if (selectedDirectory != null) {
                        File destinationDirectory = (File) selectedDirectory.getUserObject();
                        if (destinationDirectory.isDirectory()) {
                            try {
                                if (fileToCopy != null) {
                                    Files.copy(destinationDirectory.toPath(), fileToCopy.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                } else {
                                    Files.copy(destinationDirectory.toPath(), fileToCut.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                }
                            } catch (IOException exception) {
                                exception.printStackTrace();
                            }

                        }
                    }
                }
            }
        });
    }


    private void updateFileList(TreeNode selectedNode) {
        if (selectedNode != null) {
            TreeFile selectedFile = (TreeFile) selectedNode.getUserObject();
            updateFileList(selectedFile);
        }
    }

    private void updateFileList(TreeFile selectedFile) {
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

    public void updateFileList(TreeFile newParentDirectory, String suffix) {
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


    private void addOneLevel(TreeNode node) {
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
                    TreeNode childNode = new TreeNode(treeChild);
                    node.add(childNode);
                }
            }
        }
        treeModel.reload();
    }

}
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 * Created by Timofey on 9/9/2017.
 */
public class FileManagerFrame extends JFrame {
    private final String TITLE = "Файловий менеджер";


    public FileManagerFrame() {
        super();
        super.setTitle(TITLE);
        Image fileManagerImage = new ImageIcon("D:\\Java_Projects\\OOP_Labs\\Lab_1\\Renders\\File_Manager_Icon.png").getImage();
        this.setIconImage(fileManagerImage);
        System.out.println(fileManagerImage);
        this.setFrameSize();
        this.setJMenuBar(new MyMenuBar(this));
        this.getContentPane().add(new MainPanel(this));
    }


    private void setFrameSize() {
        super.setLocationByPlatform(true);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int x = dim.width, y = dim.height;
        super.setSize(2 * x / 3, 2 * y / 3);
    }
}

class MainPanel extends JPanel {
    private FileManagerFrame frame;

    public MainPanel(FileManagerFrame frame) {
        super();
        this.frame = frame;

        this.setLayout(new GridBagLayout());

        LeftSearchPanel leftSearchPanel = new LeftSearchPanel();
        RightSearchPanel rightSearchPanel = new RightSearchPanel();
        LeftHintPanel leftHintPanel = new LeftHintPanel();
        RightHintPanel rightHintPanel = new RightHintPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSearchPanel, rightSearchPanel);
        splitPane.setResizeWeight(0.5);
        splitPane.setOneTouchExpandable(false);


        JPanel hintPanel = new JPanel();
        hintPanel.setLayout(new GridBagLayout());
        hintPanel.add(leftHintPanel, new GBC(0, 0, 1, 1, 1, 0).setFill(GBC.HORIZONTAL));
        hintPanel.add(rightHintPanel, new GBC(1, 0, 1, 1, 1, 0).setFill(GBC.HORIZONTAL));


        this.add(splitPane, new GBC(0, 0, 1, 1, 1, 1).setFill(GBC.BOTH));
        this.add(hintPanel, new GBC(0, 1, 2, 1, 1, 0).setFill(GBC.HORIZONTAL));


    }
}

class MyMenuBar extends JMenuBar {
    FileManagerFrame frame;

    private JMenu programMenu, fileMenu, editMenu, viewMenu, helpMenu;
    private JMenu lookAndFeelMenu;
    private JMenuItem metalItem, nimbusItem, motifItem, windowsItem, windowsClassicItem;

    public MyMenuBar(FileManagerFrame frame) {
        super();

        this.frame = frame;

        programMenu = new JMenu("Програма");
        fileMenu = new JMenu("Файл");
        editMenu = new JMenu("Операції");
        viewMenu = new JMenu("Вигляд");
        helpMenu = new JMenu("Допомога");

        this.add(programMenu);
        this.add(fileMenu);
        this.add(editMenu);
        this.add(viewMenu);
        this.add(helpMenu);

        JMenuItem copyItem = new JMenuItem("Скопіювати");
        JMenuItem cutItem = new JMenuItem("Вирізати");
        JMenuItem pasteItem = new JMenuItem("Вставити");
        JMenuItem deleteItem = new JMenuItem("Видалити");

        editMenu.add(copyItem);
        editMenu.add(cutItem);
        editMenu.add(pasteItem);
        editMenu.add(deleteItem);

        lookAndFeelMenu = new JMenu("Зовнішній вигляд");
        viewMenu.add(lookAndFeelMenu);

        metalItem = new JMenuItem(new ChangeLookAndFeelAction("Металічний", LookAndFeelClassNames.METAL_LOOK_AND_FEEL));
        nimbusItem = new JMenuItem(new ChangeLookAndFeelAction("Німбус", LookAndFeelClassNames.NIMBUS_LOOK_AND_FEEL));
        motifItem = new JMenuItem(new ChangeLookAndFeelAction("Мотіф", LookAndFeelClassNames.MOTIF_LOOK_AND_FEEL));
        windowsItem = new JMenuItem(new ChangeLookAndFeelAction("Віндоус", LookAndFeelClassNames.WINDOWS_LOOK_AND_FEEL));
        windowsClassicItem = new JMenuItem(new ChangeLookAndFeelAction("Віндоус класичний", LookAndFeelClassNames.WINDOWS_CLASSIC_LOOK_AND_FEEL));

        lookAndFeelMenu.add(metalItem);
        lookAndFeelMenu.add(nimbusItem);
        lookAndFeelMenu.add(motifItem);
        lookAndFeelMenu.add(windowsItem);
        lookAndFeelMenu.add(windowsClassicItem);
    }

    private class ChangeLookAndFeelAction extends AbstractAction {

        public ChangeLookAndFeelAction(String lookAndFeelName, String lookAndFeelClassName) {
            putValue(Action.NAME, lookAndFeelName);
            putValue(Action.SHORT_DESCRIPTION, "Change look and feel to " + lookAndFeelName);
            putValue("Class name", lookAndFeelClassName);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                UIManager.setLookAndFeel((String) getValue("Class name"));
                SwingUtilities.updateComponentTreeUI(frame);

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}


abstract class SearchPanel extends JPanel {
    protected JScrollPane fileTreeScrollPane, fileListScrollPane;
    protected JTree tree;
    protected DefaultTreeModel treeModel;
    protected JList fileList;
    protected DefaultListModel<File> fileListModel;
    protected JComboBox<String> extensionBox;
    protected final String[] extensions = {"All files (*.*)", "Normal text file (*.txt)", "C# source file (*.cs)", "Java source file (*.java)", "JSON file (*.json)",
            "HTML file (*.html)", "Python source file (*.py)", "XML file (*.xml)"};
    protected final Pattern extensionPattern = Pattern.compile("\\*.([a-z]+|\\*)");

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
                /*int childCount = rootNode.getChildCount();
                for(int i = 0; i < childCount; i++){
                    TreeNode node = (TreeNode) treeModel.getChild(rootNode, i);
                    addOneLevel(node);
                }*/
                virtualRootNode.add(rootNode);
            }
        }

        tree = new JTree(treeModel);
        tree.addTreeWillExpandListener(new TreeWillExpandListener() {
            @Override
            public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
                TreePath pathToNode = event.getPath();
                TreeNode node = (TreeNode) pathToNode.getLastPathComponent();
                int childCount = node.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    TreeNode child = (TreeNode) treeModel.getChild(node, i);
                    if (!child.isLoaded()) {
                        addOneLevel(child);
                    }
                }
            }

            @Override
            public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
                //tree.collapsePath(event.getPath());
            }
        });
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                TreeNode selectedNode = (TreeNode) tree.getLastSelectedPathComponent();
                updateFileList(selectedNode);
            }
        });
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        fileTreeScrollPane = new JScrollPane(tree);
        this.add(fileTreeScrollPane, new GBC(0, 0, 1, 1, 2, 1).setFill(GBC.BOTH));
        tree.expandRow(0);
        tree.setRootVisible(false);

        fileListModel = new DefaultListModel<>();
        fileListModel.addElement(new TreeFile("D:\\"));
        fileList = new JList(fileListModel);
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

        this.add(extensionHint, new GBC(0, 1, 1, 1, 0, 0).setAnchor(GBC.EAST).setInsets(5, 0, 5, 10));
        this.add(extensionBox, new GBC(1, 1, 1, 1, 0, 0).setAnchor(GBC.WEST).setInsets(5, 0, 5, 0));
    }

    private void updateFileList(TreeNode selectedNode) {
        if (selectedNode != null) {
            TreeFile selectedFile = (TreeFile) selectedNode.getUserObject();
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
    }

    private void addOneLevel(TreeNode node) {
        if (node.isLoaded()) {
            return;
        }
        node.setLoaded(true);
        TreeFile file = (TreeFile) node.getUserObject();
        if (file.isDirectory()) {
            File[] children = file.listFiles((fileName) -> {
                if (fileName.isHidden()) {
                    return false;
                } else {
                    return true;
                }
            });
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

    public void updateFileList(TreeFile newParentDirectory, String suffix) {
        File[] targetFiles = newParentDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(suffix));
        if(targetFiles != null){
            fileListModel.removeAllElements();
            for (File targetFile : targetFiles) {
                TreeFile targetTreeFile = new TreeFile(targetFile.toString());
                fileListModel.addElement(targetTreeFile);
            }
            fileListScrollPane.revalidate();
            fileListScrollPane.repaint();
        }
    }

    protected class CopyAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }

    protected class PasteAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }

    protected class CutAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }
}

class TreeFile extends File {
    private static File[] roots = File.listRoots();
    private String absolutePath;

    public TreeFile(String pathname) {
        super(pathname);
        this.absolutePath = pathname;
    }

    public TreeFile(File parent, String child) {
        super(parent, child);
        absolutePath = parent.toString() + "\\" + child;
    }

    public TreeFile(String parent, String child) {
        super(parent, child);
        absolutePath = parent + "\\" + child;
    }

    @Override
    public String toString() {
        for (File file : roots) {
            if (absolutePath.equals(file.toString())) {
                return absolutePath;
            }
        }
        return this.getName();
    }
}

class TreeNode extends DefaultMutableTreeNode {
    private boolean loaded;

    public TreeNode() {
        super();
        this.loaded = false;
    }

    public TreeNode(Object userObject) {
        super(userObject);
        this.loaded = false;
    }

    public TreeNode(Object userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public boolean isLoaded() {
        return this.loaded;
    }
}

class LeftSearchPanel extends SearchPanel {

    public LeftSearchPanel() {
        super();
        this.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.GRAY, 1, true), "Ліва"));
    }
}

class RightSearchPanel extends SearchPanel {

    public RightSearchPanel() {
        super();
        this.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.GRAY, 1, true), "Права"));
    }
}

abstract class HintPanel extends JPanel {
    protected Font borderTitleFont = new Font("Arial", Font.PLAIN, 12);
    protected Font labelFont = new Font("Arial", Font.BOLD, 12);

    public HintPanel() {
        super();
        setLayout(new GridBagLayout());
    }
}

class LeftHintPanel extends HintPanel {

    public LeftHintPanel() {
        super();
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true), "Файл", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, borderTitleFont, Color.BLUE));

        JLabel copyHint = new JLabel("F5 Скопіювати");
        JLabel pasteHint = new JLabel("F6 Вставити");
        JLabel cutHint = new JLabel("F7 Вирізати");
        JLabel deleteHint = new JLabel("F8 Видалити");

        copyHint.setFont(labelFont);
        pasteHint.setFont(labelFont);
        cutHint.setFont(labelFont);
        deleteHint.setFont(labelFont);


        add(copyHint, new GBC(0, 0, 1, 1, 1, 1).setAnchor(GridBagConstraints.CENTER).setInsets(10, 0, 15, 0));
        add(pasteHint, new GBC(1, 0, 1, 1, 1, 1).setAnchor(GridBagConstraints.CENTER).setInsets(10, 0, 15, 0));
        add(cutHint, new GBC(2, 0, 1, 1, 1, 1).setAnchor(GridBagConstraints.CENTER).setInsets(10, 0, 15, 0));
        add(deleteHint, new GBC(3, 0, 1, 1, 1, 1).setAnchor(GridBagConstraints.CENTER).setInsets(10, 0, 15, 0));
    }
}

class RightHintPanel extends HintPanel {

    public RightHintPanel() {
        super();

        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true), "Операції", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, borderTitleFont, Color.BLUE));

        JLabel openHint = new JLabel("F9 Редагувати");
        JLabel clearHint = new JLabel("F10 Почистити");

        openHint.setFont(labelFont);
        clearHint.setFont(labelFont);

        add(openHint, new GBC(0, 0, 1, 1, 1, 1).setAnchor(GridBagConstraints.CENTER).setInsets(10, 0, 15, 0));
        add(clearHint, new GBC(1, 0, 1, 1, 1, 1).setAnchor(GridBagConstraints.CENTER).setInsets(10, 0, 15, 0));
    }
}

class LookAndFeelClassNames {
    public static final String
            METAL_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel",
            NIMBUS_LOOK_AND_FEEL = "javax.swing.plaf.nimbus.NimbusLookAndFeel",
            MOTIF_LOOK_AND_FEEL = "com.sun.java.swing.plaf.motif.MotifLookAndFeel",
            WINDOWS_LOOK_AND_FEEL = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel",
            WINDOWS_CLASSIC_LOOK_AND_FEEL = "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel";
}

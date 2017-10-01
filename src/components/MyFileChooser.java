package components;

import auxiliary.GBC;
import auxiliary.TreeFile;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class MyFileChooser {
    private static final String TITLE = "File chooser";
    private TextEditor editor;
    FileChooserSearchPanel searchPanel;
    JDialog dialog;

    public MyFileChooser(TextEditor editor) {
        this.editor = editor;


        dialog = new JDialog(editor, TITLE, true);
        Image icon = new ImageIcon("images\\File_Chooser_icon.png").getImage();
        dialog.setIconImage(icon);
        dialog.setLocationRelativeTo(null);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width, height = screenSize.height;
        dialog.setSize(new Dimension(width / 2, height / 2));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        searchPanel = new FileChooserSearchPanel(editor);
        mainPanel.add(searchPanel, new GBC(0, 0, 1, 1, 1, 1).setAnchor(GBC.CENTER));

        dialog.getContentPane().add(searchPanel);
    }

    public void showDialog() {
        this.dialog.setLocationRelativeTo(null);
        this.dialog.setVisible(true);
    }

    private class FileChooserSearchPanel extends SearchPanel {

        private JPopupMenu popupMenu;
        private JButton chooseButton;

        public FileChooserSearchPanel(JFrame frame) {
            super(frame);

            PopupMouseListener mouseListener = new PopupMouseListener();
            fileListScrollPane.addMouseListener(mouseListener);
            fileTreeScrollPane.addMouseListener(mouseListener);
            fileList.addMouseListener(mouseListener);
            tree.addMouseListener(mouseListener);


            chooseButton = new JButton("Choose");
            Action chooseAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                    if (selectedNode == null) {
                        JOptionPane.showMessageDialog(frame, "Ви не обрали жодного файлу", "Повідомлення", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        TreeFile selectedFile = (TreeFile) selectedNode.getUserObject();
                        MyFileChooser.this.dialog.setVisible(false);
                        editor.openFile(selectedFile);
                    }
                }
            };
            chooseButton.addActionListener(chooseAction);

            InputMap inputMap = tree.getInputMap(JComponent.WHEN_FOCUSED);
            inputMap.put(KeyStroke.getKeyStroke("ENTER"), "Choose action");

            ActionMap actionMap = tree.getActionMap();
            actionMap.put("Choose action", chooseAction);

            addActions();

            add(chooseButton, new GBC(0, 2, 1, 1, 0, 0).setAnchor(GBC.EAST).setInsets(5, 0, 5, 10));
        }

        private void addActions() {
            popupMenu = new JPopupMenu();
            setComponentPopupMenu(popupMenu);
            JMenu newItem = new JMenu("Додати");
            JMenuItem newFolder = new JMenuItem("Папку");
            JMenuItem newTextFile = new JMenuItem("Текстовий файл (*txt)");
            JMenuItem newHtmlFile = new JMenuItem("HTML файл (*.html)");

            newItem.add(newFolder);
            newItem.add(newTextFile);
            newItem.add(newHtmlFile);
            popupMenu.add(newItem);

            Action newFolderAction = new ItemAction("", true);
            Action newTextFileAction = new ItemAction(".txt", false);
            Action newHtmlFileAction = new ItemAction(".htm", false);

            newFolder.addActionListener(newFolderAction);
            newTextFile.addActionListener(newTextFileAction);
            newHtmlFile.addActionListener(newHtmlFileAction);
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
                if(SwingUtilities.isRightMouseButton(e)){
                    showPopupMenu(e);
                }
            }

            private void showPopupMenu(MouseEvent e) {
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }

    }

}



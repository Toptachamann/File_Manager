import auxiliary.GBC;
import auxiliary.TreeFile;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class MyFileChooser extends JFrame {
    private static final String TITLE = "File chooser";
    FileChooserSearchPanel searchPanel;

    public MyFileChooser() {
        setTitle(TITLE);
        setLocationByPlatform(true);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width, height = screenSize.height;
        setSize(new Dimension(2 * width / 3, 2 * height / 3));
        Image icon = new ImageIcon("images\\File_Chooser_icon.png").getImage();
        setIconImage(icon);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        searchPanel = new FileChooserSearchPanel(this);
        mainPanel.add(searchPanel, new GBC(0, 0, 1, 1, 1, 1).setAnchor(GBC.CENTER));

        add(searchPanel);
    }

    public void addButtonActionListener(ActionListener listener){
        searchPanel.addButtonActionListener(listener);
    }
}

class FileChooserSearchPanel extends SearchPanel {

    private JButton chooseButton;

    public FileChooserSearchPanel(JFrame frame) {
        super(frame);
        chooseButton = new JButton("Choose");
        Action chooseAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (selectedNode == null) {
                    JOptionPane.showMessageDialog(frame, "Ви не обрали жодного файлу", "Повідомлення", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    TreeFile selectedFile = (TreeFile) selectedNode.getUserObject();
                    openFile(selectedFile);
                    frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                }
            }
        };
        chooseButton.addActionListener(chooseAction);

        InputMap inputMap = tree.getInputMap(JComponent.WHEN_FOCUSED);
        inputMap.put(KeyStroke.getKeyStroke("ENTER"), "Choose action");

        ActionMap actionMap = tree.getActionMap();
        actionMap.put("Choose action", chooseAction);

        add(chooseButton, new GBC(0, 2, 1, 1, 0, 0).setAnchor(GBC.EAST).setInsets(5, 0, 5, 10));
    }

    public void addButtonActionListener(ActionListener listener){
        chooseButton.addActionListener(listener);
    }

    private void openFile(File file) {
        TextEditor editor = new TextEditor(file);
        editor.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        editor.setVisible(true);
    }
}

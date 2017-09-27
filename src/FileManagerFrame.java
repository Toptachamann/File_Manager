/**
 * Created by Timofey on 9/9/2017.
 */

import auxiliary.GBC;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class FileManagerFrame extends JFrame {
    private final String TITLE = "Файловий менеджер";

    private FileManagerMenuBar menuBar;
    private JPanel mainPanel;
    private FileManagerSearchPanel searchPanel;
    private JPanel hintPanel;
    private LeftHintPanel leftHintPanel;
    private RightHintPanel rightHintPanel;

    public FileManagerFrame() {
        super();
        super.setTitle(TITLE);
        Image fileManagerImage = new ImageIcon("images\\File_Manager_Icon.png").getImage();
        this.setIconImage(fileManagerImage);
        this.setFrameSize();

        menuBar = new FileManagerMenuBar(this);
        this.setJMenuBar(menuBar);

        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        searchPanel = new FileManagerSearchPanel(this);
        searchPanel.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.GRAY, 1, true), "Панель пошуку"));

        hintPanel = new JPanel();
        leftHintPanel = new LeftHintPanel();
        rightHintPanel = new RightHintPanel();
        hintPanel.setLayout(new GridBagLayout());
        hintPanel.add(leftHintPanel, new GBC(0, 0, 1, 1, 1, 0).setFill(GBC.HORIZONTAL));
        hintPanel.add(rightHintPanel, new GBC(1, 0, 1, 1, 1, 0).setFill(GBC.HORIZONTAL));

        mainPanel.add(searchPanel, new GBC(0, 0, 1, 1, 1, 1).setFill(GBC.BOTH));
        mainPanel.add(hintPanel, new GBC(0, 1, 1, 1, 1, 0).setFill(GBC.HORIZONTAL));
        this.getContentPane().add(mainPanel);
    }

    private void setFrameSize() {
        super.setLocationByPlatform(true);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int x = dim.width, y = dim.height;
        super.setSize(2 * x / 3, 2 * y / 3);
    }

    private class FileManagerMenuBar extends MyMenuBar {
        FileManagerMenuBar(FileManagerFrame frame) {
            super(frame);
        }
    }
}




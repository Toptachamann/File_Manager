package components;
/** Created by Timofey on 9/9/2017. */
import actions.CopyAction;
import actions.CutAction;
import actions.DeleteAction;
import actions.OpenAction;
import actions.PasteAction;
import auxiliary.GBC;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;

public class FileManagerFrame extends JFrame {
  private final String TITLE = "File Manager";

  private FileManagerComponentManager componentManager;
  private JPanel mainPanel;
  private JPanel hintPanel;

  public FileManagerFrame() {
    init();
    mainPanel = new JPanel();
    mainPanel.setLayout(new GridBagLayout());

    componentManager = new FileManagerComponentManager();

    JPanel searchPanel = componentManager.getSearchPanel();
    searchPanel.setBorder(
        BorderFactory.createTitledBorder(new LineBorder(Color.GRAY, 1, true), "Search panel"));

    hintPanel = new JPanel(new GridBagLayout());
    JPanel leftHintPanel = componentManager.getLeftHintPanel();
    JPanel rightHintPanel = componentManager.getRightHintPanel();
    hintPanel.add(leftHintPanel, new GBC(0, 0, 1, 1, 1, 0).setFill(GBC.HORIZONTAL));
    hintPanel.add(rightHintPanel, new GBC(1, 0, 1, 1, 1, 0).setFill(GBC.HORIZONTAL));

    JMenuBar menuBar = componentManager.getMenuBar();
    this.setJMenuBar(menuBar);

    mainPanel.add(searchPanel, new GBC(0, 0, 1, 1, 1, 1).setFill(GBC.BOTH));
    mainPanel.add(hintPanel, new GBC(0, 1, 1, 1, 1, 0).setFill(GBC.HORIZONTAL));
    this.getContentPane().add(mainPanel);
  }

  private void init(){
    super.setTitle(TITLE);
    Image fileManagerImage = new ImageIcon("images\\File_Manager_Icon.png").getImage();
    this.setIconImage(fileManagerImage);
    this.setFrameSize();
  }

  private void setFrameSize() {
    super.setLocationByPlatform(true);
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    int x = dim.width, y = dim.height;
    super.setSize(2 * x / 3, 2 * y / 3);
  }
}

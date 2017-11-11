package components;

import actions.ChangeLookAndFeelAction;
import actions.CopyAction;
import actions.CutAction;
import actions.DeleteAction;
import actions.OpenAction;
import actions.PasteAction;
import auxiliary.LookAndFeelClassName;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

public class FileManagerComponentManager {
  private FileManagerFrame frame;

  private JMenuBar menuBar;
  private FileManagerSearchPanel searchPanel;

  private JPanel leftHintPanel;
  private JPanel rightHintPanel;

  private HelpDialog helpDialog;

  public FileManagerComponentManager(FileManagerFrame frame){
    this.frame = frame;
    createFileManagerMenuBar(frame);
    createSearchPanel(frame);
    createLeftHintPanel();
    createRightHintPanel();
    createHelpDialog(frame);
  }

  private void createHelpDialog(JFrame frame) {
    helpDialog = new HelpDialog(frame);
  }

  private void createRightHintPanel() {
    rightHintPanel = new RightHintPanel();
  }

  private void createLeftHintPanel() {
    leftHintPanel = new LeftHintPanel();
  }

  private void createSearchPanel(FileManagerFrame frame) {
    searchPanel = new FileManagerSearchPanel(frame);
  }

  private void createFileManagerMenuBar(JFrame frame) {
    menuBar = new JMenuBar();
    JMenu fileMenu = new JMenu("File");
    JMenu editMenu = new JMenu("Operations");
    JMenu viewMenu = new JMenu("View");
    JMenu helpMenu = new JMenu("Help");

    menuBar.add(fileMenu);
    menuBar.add(editMenu);
    menuBar.add(viewMenu);
    menuBar.add(helpMenu);

    JMenuItem copyItem = new JMenuItem("Copy");
    JMenuItem cutItem = new JMenuItem("Cut");
    JMenuItem pasteItem = new JMenuItem("Paste");
    JMenuItem deleteItem = new JMenuItem("Delete");
    JMenuItem openItem = new JMenuItem("Open");

    JMenuItem helpMenuItem = new JMenuItem("Commands list");
    helpMenu.add(helpMenuItem);

    fileMenu.add(openItem);
    editMenu.add(copyItem);
    editMenu.add(cutItem);
    editMenu.add(pasteItem);
    editMenu.add(deleteItem);

    JMenu lookAndFeelMenu = new JMenu("Look and feel");
    viewMenu.add(lookAndFeelMenu);

    JMenuItem metalItem =
        new JMenuItem(
            new ChangeLookAndFeelAction("Metal", LookAndFeelClassName.METAL_LOOK_AND_FEEL, frame));
    JMenuItem nimbusItem =
        new JMenuItem(
            new ChangeLookAndFeelAction(
                "Nimbus", LookAndFeelClassName.NIMBUS_LOOK_AND_FEEL, frame));
    JMenuItem motifItem =
        new JMenuItem(
            new ChangeLookAndFeelAction("Motif", LookAndFeelClassName.MOTIF_LOOK_AND_FEEL, frame));
    JMenuItem windowsItem =
        new JMenuItem(
            new ChangeLookAndFeelAction(
                "Windows", LookAndFeelClassName.WINDOWS_LOOK_AND_FEEL, frame));
    JMenuItem windowsClassicItem =
        new JMenuItem(
            new ChangeLookAndFeelAction(
                "Windows classic", LookAndFeelClassName.WINDOWS_CLASSIC_LOOK_AND_FEEL, frame));

    lookAndFeelMenu.add(metalItem);
    lookAndFeelMenu.add(nimbusItem);
    lookAndFeelMenu.add(motifItem);
    lookAndFeelMenu.add(windowsItem);
    lookAndFeelMenu.add(windowsClassicItem);

    copyItem.addActionListener(new CopyAction(searchPanel));
    cutItem.addActionListener(new CutAction(searchPanel));
    pasteItem.addActionListener(new PasteAction(searchPanel));
    deleteItem.addActionListener(new DeleteAction(searchPanel));
    openItem.addActionListener(new OpenAction(searchPanel));
    helpMenuItem.addActionListener((e) -> helpDialog.showDialog());
  }

  public JMenuBar getMenuBar() {
    return menuBar;
  }

  public JPanel getLeftHintPanel() {
    return leftHintPanel;
  }

  public JPanel getRightHintPanel() {
    return rightHintPanel;
  }

  public FileManagerSearchPanel getSearchPanel() {
    return searchPanel;
  }

  public HelpDialog getHelpDialog() {
    return helpDialog;
  }
}

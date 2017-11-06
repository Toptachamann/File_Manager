package components;
/** Created by Timofey on 9/14/2017. */
import actions.ChangeLookAndFeelAction;
import auxiliary.LookAndFeelClassName;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MyMenuBar extends JMenuBar {

  protected JFrame frame;
  protected JMenu fileMenu, editMenu, viewMenu, helpMenu;
  protected JMenu lookAndFeelMenu;
  protected JMenuItem metalItem, nimbusItem, motifItem, windowsItem, windowsClassicItem;
  protected JMenuItem copyItem, cutItem, pasteItem, deleteItem, openItem;
  protected JMenuItem helpMenuItem;

  public MyMenuBar(JFrame frame) {
    super();
    this.frame = frame;

    fileMenu = new JMenu("File");
    editMenu = new JMenu("Operations");
    viewMenu = new JMenu("View");
    helpMenu = new JMenu("Help");

    this.add(fileMenu);
    this.add(editMenu);
    this.add(viewMenu);
    this.add(helpMenu);

    copyItem = new JMenuItem("Copy");
    cutItem = new JMenuItem("Cut");
    pasteItem = new JMenuItem("Paste");
    deleteItem = new JMenuItem("Delete");
    openItem = new JMenuItem("Open");

    helpMenuItem = new JMenuItem("Commands list");
    helpMenu.add(helpMenuItem);

    fileMenu.add(openItem);
    editMenu.add(copyItem);
    editMenu.add(cutItem);
    editMenu.add(pasteItem);
    editMenu.add(deleteItem);

    lookAndFeelMenu = new JMenu("Look and feel");
    viewMenu.add(lookAndFeelMenu);

    metalItem =
        new JMenuItem(
            new ChangeLookAndFeelAction("Metal", LookAndFeelClassName.METAL_LOOK_AND_FEEL, frame));
    nimbusItem =
        new JMenuItem(
            new ChangeLookAndFeelAction(
                "Nimbus", LookAndFeelClassName.NIMBUS_LOOK_AND_FEEL, frame));
    motifItem =
        new JMenuItem(
            new ChangeLookAndFeelAction("Motif", LookAndFeelClassName.MOTIF_LOOK_AND_FEEL, frame));
    windowsItem =
        new JMenuItem(
            new ChangeLookAndFeelAction(
                "Windows", LookAndFeelClassName.WINDOWS_LOOK_AND_FEEL, frame));
    windowsClassicItem =
        new JMenuItem(
            new ChangeLookAndFeelAction(
                "Windows classic", LookAndFeelClassName.WINDOWS_CLASSIC_LOOK_AND_FEEL, frame));

    lookAndFeelMenu.add(metalItem);
    lookAndFeelMenu.add(nimbusItem);
    lookAndFeelMenu.add(motifItem);
    lookAndFeelMenu.add(windowsItem);
    lookAndFeelMenu.add(windowsClassicItem);
  }
}

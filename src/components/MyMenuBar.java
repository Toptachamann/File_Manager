package components; /**
 * Created by Timofey on 9/14/2017.
 */

import actions.ChangeLookAndFeelAction;
import auxiliary.LookAndFeelClassName;

import javax.swing.*;

public class MyMenuBar extends JMenuBar {

    protected JFrame frame;
    protected JMenu fileMenu, editMenu, viewMenu, helpMenu;
    protected JMenu lookAndFeelMenu;
    protected JMenuItem metalItem, nimbusItem, motifItem, windowsItem, windowsClassicItem;
    protected JMenuItem copyItem, cutItem, pasteItem, deleteItem, openItem;

    public MyMenuBar(JFrame frame) {
        super();
        this.frame = frame;

        fileMenu = new JMenu("Файл");
        editMenu = new JMenu("Операції");
        viewMenu = new JMenu("Вигляд");
        helpMenu = new JMenu("Допомога");

        this.add(fileMenu);
        this.add(editMenu);
        this.add(viewMenu);
        this.add(helpMenu);

        copyItem = new JMenuItem("Скопіювати");
        cutItem = new JMenuItem("Вирізати");
        pasteItem = new JMenuItem("Вставити");
        deleteItem = new JMenuItem("Видалити");
        openItem = new JMenuItem("Відкрити");

        editMenu.add(copyItem);
        editMenu.add(cutItem);
        editMenu.add(pasteItem);
        editMenu.add(deleteItem);
        editMenu.add(openItem);

        lookAndFeelMenu = new JMenu("Зовнішній вигляд");
        viewMenu.add(lookAndFeelMenu);

        metalItem = new JMenuItem(new ChangeLookAndFeelAction("Металічний", LookAndFeelClassName.METAL_LOOK_AND_FEEL, frame));
        nimbusItem = new JMenuItem(new ChangeLookAndFeelAction("Німбус", LookAndFeelClassName.NIMBUS_LOOK_AND_FEEL, frame));
        motifItem = new JMenuItem(new ChangeLookAndFeelAction("Мотіф", LookAndFeelClassName.MOTIF_LOOK_AND_FEEL, frame));
        windowsItem = new JMenuItem(new ChangeLookAndFeelAction("Віндоус", LookAndFeelClassName.WINDOWS_LOOK_AND_FEEL, frame));
        windowsClassicItem = new JMenuItem(new ChangeLookAndFeelAction("Віндоус класичний", LookAndFeelClassName.WINDOWS_CLASSIC_LOOK_AND_FEEL, frame));

        lookAndFeelMenu.add(metalItem);
        lookAndFeelMenu.add(nimbusItem);
        lookAndFeelMenu.add(motifItem);
        lookAndFeelMenu.add(windowsItem);
        lookAndFeelMenu.add(windowsClassicItem);
    }
}

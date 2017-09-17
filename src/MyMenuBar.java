import javax.swing.*;

/**
 * Created by Timofey on 9/14/2017.
 */
abstract class MyMenuBar extends JMenuBar{
    JFrame frame;
    protected JMenu programMenu, fileMenu, editMenu, viewMenu, helpMenu;
    protected JMenu lookAndFeelMenu;
    protected JMenuItem metalItem, nimbusItem, motifItem, windowsItem, windowsClassicItem;

    public MyMenuBar(JFrame frame){
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

        JMenuItem copyItem = new JMenuItem("Скопіювати"), cutItem = new JMenuItem("Вирізати"),
                pasteItem = new JMenuItem("Вставити"), deleteItem = new JMenuItem("Видалити");

        editMenu.add(copyItem);
        editMenu.add(cutItem);
        editMenu.add(pasteItem);
        editMenu.add(deleteItem);

        lookAndFeelMenu = new JMenu("Зовнішній вигляд");
        viewMenu.add(lookAndFeelMenu);

        metalItem = new JMenuItem(new ChangeLookAndFeelAction("Металічний", LookAndFeelClassNames.METAL_LOOK_AND_FEEL, frame));
        nimbusItem = new JMenuItem(new ChangeLookAndFeelAction("Німбус", LookAndFeelClassNames.NIMBUS_LOOK_AND_FEEL, frame));
        motifItem = new JMenuItem(new ChangeLookAndFeelAction("Мотіф", LookAndFeelClassNames.MOTIF_LOOK_AND_FEEL, frame));
        windowsItem = new JMenuItem(new ChangeLookAndFeelAction("Віндоус", LookAndFeelClassNames.WINDOWS_LOOK_AND_FEEL, frame));
        windowsClassicItem = new JMenuItem(new ChangeLookAndFeelAction("Віндоус класичний", LookAndFeelClassNames.WINDOWS_CLASSIC_LOOK_AND_FEEL, frame));

        lookAndFeelMenu.add(metalItem);
        lookAndFeelMenu.add(nimbusItem);
        lookAndFeelMenu.add(motifItem);
        lookAndFeelMenu.add(windowsItem);
        lookAndFeelMenu.add(windowsClassicItem);
    }
}

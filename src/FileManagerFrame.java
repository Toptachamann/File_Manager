import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;


/**
 * Created by Timofey on 9/9/2017.
 */
public class FileManagerFrame extends JFrame {
    private final String TITLE = "Файловий менеджер";

    public FileManagerFrame() {
        super();
        super.setTitle(TITLE);
        Image fileManagerImage = new ImageIcon("D:\\Java_Projects\\OOP_Labs\\Lab_1\\images\\File_Manager_Icon.png").getImage();
        this.setIconImage(fileManagerImage);
        System.out.println(fileManagerImage);
        this.setFrameSize();
        this.setJMenuBar(new FileManagerMenuBar(this));
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


class FileManagerMenuBar extends MyMenuBar {
    FileManagerMenuBar(FileManagerFrame frame) {
        super(frame);
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

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
        Image fileManagerImage = new ImageIcon("images\\File_Manager_Icon.png").getImage();
        this.setIconImage(fileManagerImage);
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

        FileManagerSearchPanel searchPanel = new FileManagerSearchPanel(frame);
        searchPanel.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.GRAY, 1, true), "Панель пошуку"));
        LeftHintPanel leftHintPanel = new LeftHintPanel();
        RightHintPanel rightHintPanel = new RightHintPanel();


        JPanel hintPanel = new JPanel();
        hintPanel.setLayout(new GridBagLayout());
        hintPanel.add(leftHintPanel, new GBC(0, 0, 1, 1, 1, 0).setFill(GBC.HORIZONTAL));
        hintPanel.add(rightHintPanel, new GBC(1, 0, 1, 1, 1, 0).setFill(GBC.HORIZONTAL));



        this.add(searchPanel, new GBC(0, 0, 1, 1, 1, 1).setFill(GBC.BOTH));
        this.add(hintPanel, new GBC(0, 1, 1, 1, 1, 0).setFill(GBC.HORIZONTAL));
    }
}


class FileManagerMenuBar extends MyMenuBar {
    FileManagerMenuBar(FileManagerFrame frame) {
        super(frame);
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

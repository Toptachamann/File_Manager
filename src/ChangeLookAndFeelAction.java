import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by Timofey on 9/14/2017.
 */
public class ChangeLookAndFeelAction extends AbstractAction {
    JFrame frame;
    public ChangeLookAndFeelAction(String lookAndFeelName, String lookAndFeelClassName, JFrame frame) {
        this.frame = frame;
        putValue(Action.NAME, lookAndFeelName);
        putValue(Action.SHORT_DESCRIPTION, "Change look and feel to " + lookAndFeelName);
        putValue("Class name", lookAndFeelClassName);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            UIManager.setLookAndFeel((String) getValue("Class name"));
            SwingUtilities.updateComponentTreeUI(frame);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}

/**
 * Created by Timofey on 9/14/2017.
 */

package actions;

import auxiliary.LookAndFeelClassName;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class ChangeLookAndFeelAction extends AbstractAction {

    protected JFrame frame;


    public ChangeLookAndFeelAction(String lookAndFeelName, LookAndFeelClassName lookAndFeelClassName, JFrame frame) {
        this.frame = frame;
        putValue(Action.NAME, lookAndFeelName);
        putValue(Action.SHORT_DESCRIPTION, "Change look and feel to " + lookAndFeelName);
        putValue("Class name", lookAndFeelClassName.className());
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
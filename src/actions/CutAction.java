package actions;

import components.FileManagerSearchPanel;

import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;

public class CutAction extends AbstractAction {

    public CutAction(FileManagerSearchPanel searchPanel){
        putValue("Search panel", searchPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        FileManagerSearchPanel searchPanel = (FileManagerSearchPanel) getValue("Search panel");
        searchPanel.cutAct();
    }
}
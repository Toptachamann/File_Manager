package actions;

import components.FileManagerSearchPanel;

import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;

public class PasteAction extends AbstractAction {

    public PasteAction(FileManagerSearchPanel searchPanel){
        putValue("Search panel", searchPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        FileManagerSearchPanel searchPanel = (FileManagerSearchPanel) getValue("Search panel");
        searchPanel.pasteAct();
    }
}

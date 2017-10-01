package actions;

import components.SearchPanel;

import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;

public class RenameAction extends AbstractAction {

    public RenameAction(SearchPanel searchPanel){
        putValue("Search panel", searchPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SearchPanel searchPanel = (SearchPanel) getValue("Search panel");
        searchPanel.renameAct();
    }
}

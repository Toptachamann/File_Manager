package actions;

import components.FileManagerSearchPanel;
import components.SearchPanel;

import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;

public class CopyWithoutMultipleLinesAction extends AbstractAction {
    public CopyWithoutMultipleLinesAction(FileManagerSearchPanel searchPanel){
        putValue("Search panel", searchPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        FileManagerSearchPanel searchPanel = (FileManagerSearchPanel) getValue("Search panel");
        searchPanel.copyWithoutMultipleLinesAct();
    }
}

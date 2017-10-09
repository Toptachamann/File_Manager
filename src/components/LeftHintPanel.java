package components;

import actions.CopyAction;
import actions.CutAction;
import actions.DeleteAction;
import actions.PasteAction;
import auxiliary.GBC;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class LeftHintPanel extends JPanel {

    private FileManagerSearchPanel searchPanel;
    private Font borderTitleFont = new Font("Arial", Font.PLAIN, 12);
    private Font labelFont = new Font("Arial", Font.BOLD, 12);


    public LeftHintPanel(FileManagerSearchPanel searchPanel) {
        super();
        this.searchPanel = searchPanel;
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY,
                1, true), "Файл", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                borderTitleFont, Color.BLUE));
        setLayout(new GridBagLayout());
        /*JLabel copyHint = new JLabel("Ctrl + C -  Скопіювати");
        JLabel pasteHint = new JLabel("Ctrl + V - Вставити");
        JLabel cutHint = new JLabel("Ctrl + X - Вирізати");
        JLabel deleteHint = new JLabel("Delete - Видалити");

        copyHint.setFont(labelFont);
        pasteHint.setFont(labelFont);
        cutHint.setFont(labelFont);
        deleteHint.setFont(labelFont);

        add(copyHint, new GBC(0, 0, 1, 1, 1, 1)
                .setAnchor(GridBagConstraints.CENTER).setInsets(5, 0, 5, 0));
        add(pasteHint, new GBC(1, 0, 1, 1, 1, 1)
                .setAnchor(GridBagConstraints.CENTER).setInsets(5, 0, 5, 0));
        add(cutHint, new GBC(0, 1, 1, 1, 1, 1)
                .setAnchor(GridBagConstraints.CENTER).setInsets(5, 0, 5, 0));
        add(deleteHint, new GBC(1, 1, 1, 1, 1, 1)
                .setAnchor(GridBagConstraints.CENTER).setInsets(5, 0, 5, 0));
*/
        JButton copyButton = new JButton("Скопіювати");
        JButton pasteButton = new JButton("Вставити");
        JButton cutButton = new JButton("Вирізати");
        JButton deleteButton = new JButton("Видалити");

        copyButton.addActionListener(new CopyAction(searchPanel));
        pasteButton.addActionListener(new PasteAction(searchPanel));
        cutButton.addActionListener(new CutAction(searchPanel));
        deleteButton.addActionListener(new DeleteAction(searchPanel));
        add(copyButton, new GBC(0, 0, 1, 1, 1, 1)
                .setAnchor(GridBagConstraints.CENTER).setInsets(5, 0, 5, 0));
        add(pasteButton, new GBC(1, 0, 1, 1, 1, 1)
                .setAnchor(GridBagConstraints.CENTER).setInsets(5, 0, 5, 0));
        add(cutButton, new GBC(0, 1, 1, 1, 1, 1)
                .setAnchor(GridBagConstraints.CENTER).setInsets(5, 0, 5, 0));
        add(deleteButton, new GBC(1, 1, 1, 1, 1, 1)
                .setAnchor(GridBagConstraints.CENTER).setInsets(5, 0, 5, 0));
    }
}
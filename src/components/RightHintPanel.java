package components;

import actions.ClearContentAction;
import actions.CopyAllWithExtensionAction;
import actions.CopyHtmlFileAction;
import actions.CopyWithoutMultipleLinesAction;
import actions.OpenAction;
import actions.PasteAction;
import actions.RenameAction;
import auxiliary.GBC;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class RightHintPanel extends JPanel {

    protected Font borderTitleFont = new Font("Arial", Font.PLAIN, 12);
    protected Font labelFont = new Font("Arial", Font.BOLD, 12);
    private FileManagerSearchPanel searchPanel;

    public RightHintPanel(FileManagerSearchPanel searchPanel) {
        super();
        this.searchPanel = searchPanel;
        this.setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY,
                1, true), "Операції", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                    borderTitleFont, Color.BLUE));

        /*JLabel openHint = new JLabel("Ctrl + O - Редагувати");
        JLabel clearHint = new JLabel("F3 - Видалити зміст");
        JLabel renameHint = new JLabel("Ctrl + R - перейменувати");
        JLabel copyWithoutMultipleLines = new JLabel("Ctrl + Alt + V - скопіювати без повторів");
        JLabel pasteHtml = new JLabel("Ctrl + Shift + D - вставити HTML");
        JLabel copyAllWithExtension = new JLabel("Ctrl + Shift + V - скопіювати всі з розширенням");

        openHint.setFont(labelFont);
        clearHint.setFont(labelFont);

        add(openHint, new GBC(0, 0, 1, 1, 1, 1)
                .setAnchor(GBC.CENTER).setInsets(5, 0, 5, 0));
        add(clearHint, new GBC(1, 0, 1, 1, 1, 1)
                .setAnchor(GBC.CENTER).setInsets(5, 0, 5, 0));
        add(renameHint, new GBC(2, 0, 1, 1, 1, 1)
                .setAnchor(GBC.CENTER).setInsets(5, 0, 5, 0));
        add(copyWithoutMultipleLines, new GBC(0, 1, 1, 1, 1, 1)
                .setAnchor(GBC.CENTER).setInsets(5, 0, 5, 0));
        add(copyAllWithExtension, new GBC(1, 1, 1, 1, 1, 1)
                .setAnchor(GBC.CENTER).setInsets(5, 0, 5, 0));
        add(pasteHtml, new GBC(2, 1, 1, 1, 1, 1)
                .setAnchor(GBC.CENTER).setInsets(5, 0, 5, 0));*/

        JButton openButton = new JButton("Редагувати");
        JButton clearButton = new JButton("Видалити зміст");
        JButton renameButton = new JButton("Перейменувати");
        JButton copyWithoutMultipleLinesButton = new JButton("Скопіювати без повторень");
        JButton pasteHtml = new JButton("Вставити HTML");
        JButton copyAllWithExtensionButton = new JButton("Скопіювати всі з заданим розширенням");

        openButton.addActionListener(new OpenAction(searchPanel));
        clearButton.addActionListener(new ClearContentAction(searchPanel));
        renameButton.addActionListener(new RenameAction(searchPanel));
        copyAllWithExtensionButton.addActionListener(new CopyAllWithExtensionAction(searchPanel));
        pasteHtml.addActionListener(new CopyHtmlFileAction(searchPanel));
        copyWithoutMultipleLinesButton.addActionListener(new CopyWithoutMultipleLinesAction(searchPanel));

        add(openButton, new GBC(0, 0, 1, 1, 1, 1)
                .setAnchor(GBC.CENTER).setInsets(5, 0, 5, 0));
        add(clearButton, new GBC(1, 0, 1, 1, 1, 1)
                .setAnchor(GBC.CENTER).setInsets(5, 0, 5, 0));
        add(renameButton, new GBC(2, 0, 1, 1, 1, 1)
                .setAnchor(GBC.CENTER).setInsets(5, 0, 5, 0));
        add(copyWithoutMultipleLinesButton, new GBC(0, 1, 1, 1, 1, 1)
                .setAnchor(GBC.CENTER).setInsets(5, 0, 5, 0));
        add(pasteHtml, new GBC(1, 1, 1, 1, 1, 1)
                .setAnchor(GBC.CENTER).setInsets(5, 0, 5, 0));
        add(copyAllWithExtensionButton, new GBC(2, 1, 1, 1, 1, 1)
                .setAnchor(GBC.CENTER).setInsets(5, 0, 5, 0));
    }
}
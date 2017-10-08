package components;

import auxiliary.GBC;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class RightHintPanel extends JPanel {

    protected Font borderTitleFont = new Font("Arial", Font.PLAIN, 12);
    protected Font labelFont = new Font("Arial", Font.BOLD, 12);


    public RightHintPanel() {
        super();
        this.setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY,
                1, true), "Операції", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                    borderTitleFont, Color.BLUE));

        JLabel openHint = new JLabel("Ctrl + O - Редагувати");
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
                .setAnchor(GBC.CENTER).setInsets(5, 0, 5, 0));
    }
}
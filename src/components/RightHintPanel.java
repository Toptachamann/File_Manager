package components;

import auxiliary.GBC;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;

public class RightHintPanel extends JPanel {

  protected Font borderTitleFont = new Font("Arial", Font.PLAIN, 12);
  protected Font labelFont = new Font("Arial", Font.BOLD, 12);

  public RightHintPanel() {
    super();
    this.setLayout(new GridBagLayout());
    setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1, true),
            "Operations",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            borderTitleFont,
            Color.BLUE));
    addLabels();
  }

  private void addLabels() {
    JLabel openHint = new JLabel("Ctrl + O - Edit");
    JLabel clearHint = new JLabel("F3 - Clear content");
    JLabel renameHint = new JLabel("Ctrl + R - Rename");
    JLabel copyAllWithExtension = new JLabel("Ctrl + Shift + V - скопіювати всі з розширенням");

    openHint.setFont(labelFont);
    clearHint.setFont(labelFont);

    add(openHint, new GBC(0, 0, 1, 1, 1, 1).setAnchor(GBC.CENTER).setInsets(5, 0, 5, 0));
    add(clearHint, new GBC(1, 0, 1, 1, 1, 1).setAnchor(GBC.CENTER).setInsets(5, 0, 5, 0));
    add(renameHint, new GBC(0, 1, 1, 1, 1, 1).setAnchor(GBC.CENTER).setInsets(5, 0, 5, 0));
    add(
        copyAllWithExtension,
        new GBC(1, 1, 1, 1, 1, 1).setAnchor(GBC.CENTER).setInsets(5, 0, 5, 0));
  }
}

/*JButton openButton = new JButton("Edit");
    JButton clearButton = new JButton("Clear content");
    JButton renameButton = new JButton("Rename");
    JButton copyWithoutMultipleLinesButton = new JButton("Copy without repeating lines");
    JButton pasteHtml = new JButton("Paste HTML");
    JButton copyAllWithExtensionButton = new JButton("Copy all with chosen extension");

    openButton.addActionListener(new OpenAction(searchPanel));
    clearButton.addActionListener(new ClearContentAction(searchPanel));
    renameButton.addActionListener(new RenameAction(searchPanel));
    copyAllWithExtensionButton.addActionListener(new CopyAllWithExtensionAction(searchPanel));
    pasteHtml.addActionListener(new CopyHtmlFileAction(searchPanel));
    copyWithoutMultipleLinesButton.addActionListener(
        new CopyWithoutMultipleLinesAction(searchPanel));

    add(openButton, new GBC(0, 0, 1, 1, 1, 1).setAnchor(GBC.CENTER).setInsets(5, 0, 5, 0));
    add(clearButton, new GBC(1, 0, 1, 1, 1, 1).setAnchor(GBC.CENTER).setInsets(5, 0, 5, 0));
    add(renameButton, new GBC(2, 0, 1, 1, 1, 1).setAnchor(GBC.CENTER).setInsets(5, 0, 5, 0));
    add(
        copyWithoutMultipleLinesButton,
        new GBC(0, 1, 1, 1, 1, 1).setAnchor(GBC.CENTER).setInsets(5, 0, 5, 0));
    add(pasteHtml, new GBC(1, 1, 1, 1, 1, 1).setAnchor(GBC.CENTER).setInsets(5, 0, 5, 0));
    add(
        copyAllWithExtensionButton,
        new GBC(2, 1, 1, 1, 1, 1).setAnchor(GBC.CENTER).setInsets(5, 0, 5, 0));
*/

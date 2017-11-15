package file_manager;

import auxiliary.GBC;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class LeftHintPanel extends JPanel {

  private Font borderTitleFont = new Font("Arial", Font.PLAIN, 12);
  private Font labelFont = new Font("Arial", Font.BOLD, 12);

  public LeftHintPanel() {
    super();
    setLayout(new GridBagLayout());
    setBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1, true),
            "File",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            borderTitleFont,
            Color.BLUE));
    addHelpLabels();
     }

  private void addHelpLabels(){
      JLabel copyHint = new JLabel("Ctrl + C -  Скопіювати");
      JLabel pasteHint = new JLabel("Ctrl + V - Вставити");
      JLabel cutHint = new JLabel("Ctrl + X - Вирізати");
      JLabel deleteHint = new JLabel("Delete - Видалити");

      copyHint.setFont(labelFont);
      pasteHint.setFont(labelFont);
      cutHint.setFont(labelFont);
      deleteHint.setFont(labelFont);

      add(
          copyHint,
          new GBC(0, 0, 1, 1, 1, 1).setAnchor(GridBagConstraints.CENTER).setInsets(5, 0, 5, 0));
      add(
          pasteHint,
          new GBC(1, 0, 1, 1, 1, 1).setAnchor(GridBagConstraints.CENTER).setInsets(5, 0, 5, 0));
      add(
          cutHint,
          new GBC(0, 1, 1, 1, 1, 1).setAnchor(GridBagConstraints.CENTER).setInsets(5, 0, 5, 0));
      add(
          deleteHint,
          new GBC(1, 1, 1, 1, 1, 1).setAnchor(GridBagConstraints.CENTER).setInsets(5, 0, 5, 0));

    }
}

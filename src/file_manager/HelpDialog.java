package file_manager;

import auxiliary.GBC;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class HelpDialog extends JDialog {

  private static final String TITLE = "File manager commands list";
  private static final Color BACKGROUND_COLOR = new Color(255, 255, 255);
  private static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 12);

  private JFrame owner;
  private JPanel mainPanel;
  private JLabel[] labels;
  private JButton okeyButton;

  public HelpDialog(JFrame owner) {
    super(owner, TITLE, ModalityType.APPLICATION_MODAL);
    init(owner);
    mainPanel = new JPanel();
    mainPanel.setBackground(BACKGROUND_COLOR);
    mainPanel.setLayout(new GridBagLayout());
    this.getContentPane().add(mainPanel);

    addHelpLabels();
    addUserButton();

    addWindowListener(
        new WindowAdapter() {
          @Override
          public void windowClosing(WindowEvent e) {
            setVisible(false);
          }
        });
  }

  private void init(JFrame owner) {
    this.owner = owner;
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int width = screenSize.width;
    int height = screenSize.height;
    setSize(new Dimension(width / 3, height / 2));
  }

  private void addHelpLabels() {
    JLabel copyLabel = new JLabel("Ctrl + C - copy object");
    JLabel pasteLabel = new JLabel("Ctrl + V - paste object");
    JLabel cutLabel = new JLabel("Ctrl + X - cut object");
    JLabel deleteLabel = new JLabel("Delete - delete object");
    JLabel openLabel = new JLabel("Ctrl + O - open file");
    JLabel clearContentLabel = new JLabel("F3 - clear content");
    JLabel renameLabel = new JLabel("Ctrl + R - rename object");
    JLabel copyAllWithExtensionLabel =
        new JLabel("Ctrl + Shift + V - copy all files with the specified extension");
    JLabel copyWithoutMultipleLinesLabel =
        new JLabel("Ctrl + Alt + V - copy without repeating lines");
    JLabel copyHtmlFileAction = new JLabel("Ctrl + Shift + D - paste HTML file");

    labels =
        new JLabel[] {
          copyLabel,
          pasteLabel,
          cutLabel,
          deleteLabel,
          openLabel,
          clearContentLabel,
          renameLabel,
          copyAllWithExtensionLabel,
          copyWithoutMultipleLinesLabel,
          copyHtmlFileAction
        };

    for (int i = 0; i < labels.length; i++) {
      JLabel label = labels[i];
      label.setFont(LABEL_FONT);
      mainPanel.add(label, new GBC(0, i, 1, 1, 1, 1).setAnchor(GBC.CENTER).setInsets(10, 0, 10, 0));
    }
  }

  private void addUserButton() {
    okeyButton = new JButton("All right");
    mainPanel.add(
        okeyButton,
        new GBC(0, labels.length, 1, 1, 0, 0).setAnchor(GBC.EAST).setInsets(10, 0, 10, 25));
    okeyButton.addActionListener((e) -> setVisible(false));
  }

  public void showDialog() {
    this.setLocationRelativeTo(owner);
    this.setVisible(true);
  }
}

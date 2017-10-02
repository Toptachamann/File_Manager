package components;

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
import java.awt.event.WindowListener;

public class HelpDialog extends JDialog {

    private static final String TITLE = "Список команд файлового менеджера";
    private static final Color BACKGROUND_COLOR = new Color(255, 255, 255);
    private static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 12);

    JFrame owner;

    public HelpDialog(JFrame owner) {
        super(owner, TITLE, ModalityType.APPLICATION_MODAL);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width;
        int height = screenSize.height;
        setSize(new Dimension(width/3, height/2));
        this.owner = owner;
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setLayout(new GridBagLayout());
        JLabel copyLabel = new JLabel("Ctrl + C - спопіювати файл");
        JLabel pasteLabel = new JLabel("Ctrl + V - вставити файл");
        JLabel cutLabel = new JLabel("Ctrl + X - вирізати файл");
        JLabel deleteLabel = new JLabel("Delete - видалити файл");
        JLabel openLabel = new JLabel("Ctrl + O - відкрити файл");
        JLabel clearContentLabel = new JLabel("F3 - видалити вміст файла");
        JLabel renameLabel = new JLabel("Ctrl + R - перейменувати файл");
        JLabel copyAllWithExtensionLabel = new JLabel("Ctrl + Shift + V - скопіювати всі файли з обраним розширенням");
        JLabel copyWithoutMultipleLinesLabel = new JLabel("Ctrl + Alt + V - скопіювати без стрічок, що повторються");
        JLabel copyHtmlFileAction = new JLabel("Ctrl + Shift + D - скопіювати HTML файл");

        JLabel[] labels = new JLabel[]{copyLabel, pasteLabel, cutLabel, deleteLabel, openLabel,
                clearContentLabel, renameLabel, copyAllWithExtensionLabel, copyWithoutMultipleLinesLabel, copyHtmlFileAction};

        for(int i = 0; i < labels.length; i++){
            JLabel label = labels[i];
            label.setFont(LABEL_FONT);
            mainPanel.add(label, new GBC(0, i, 1, 1, 1, 1)
                    .setAnchor(GBC.CENTER).setInsets(10, 0, 10, 0));
        }

        WindowListener windowListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setVisible(false);
            }
        };

        JButton okeyButton = new JButton("Добре");
        mainPanel.add(okeyButton, new GBC(0, labels.length, 1, 1, 0, 0)
                .setAnchor(GBC.EAST).setInsets(10, 0, 10, 25));
        okeyButton.addActionListener((e)->setVisible(false));

        this.getContentPane().add(mainPanel);
    }

    public void showDialod(){
        this.setLocationRelativeTo(owner);
        this.setVisible(true);
    }

}

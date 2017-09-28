import auxiliary.GBC;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

class RightHintPanel extends JPanel {

    protected Font borderTitleFont = new Font("Arial", Font.PLAIN, 12);
    protected Font labelFont = new Font("Arial", Font.BOLD, 12);


    public RightHintPanel() {
        super();
        this.setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true), "Операції", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, borderTitleFont, Color.BLUE));

        JLabel openHint = new JLabel("Ctrl + O - Редагувати");
        JLabel clearHint = new JLabel("F3 - Видалити зміст");

        openHint.setFont(labelFont);
        clearHint.setFont(labelFont);

        add(openHint, new GBC(0, 0, 1, 1, 1, 1).setAnchor(GridBagConstraints.CENTER).setInsets(10, 0, 15, 0));
        add(clearHint, new GBC(1, 0, 1, 1, 1, 1).setAnchor(GridBagConstraints.CENTER).setInsets(10, 0, 15, 0));
    }
}
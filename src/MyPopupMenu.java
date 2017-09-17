import javax.swing.*;

/**
 * Created by Timofey on 9/14/2017.
 */
public class MyPopupMenu extends JPopupMenu{
    public MyPopupMenu(){
        super();
        JMenuItem copyPopup = new JMenuItem("Скопіювати"), pastePopup = new JMenuItem("Вставити"),
                cutPopup = new JMenuItem("Вирізати"), deletePopup = new JMenuItem("Видалити");
        add(copyPopup);
        add(pastePopup);
        add(cutPopup);
        add(deletePopup);
    }
}

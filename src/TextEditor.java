import javax.swing.*;
import javax.xml.soap.Text;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;

/**
 * Created by Timofey on 9/14/2017.
 */
public class TextEditor extends JFrame {
    private final String TITLE = "Текстовий редактор";
    private String currentTitle = "Untitled";
    private JTextArea textArea;
    private JFileChooser fileChooser;
    private boolean changed = false;
    JPopupMenu popupMenu;


    public TextEditor(){
        setTitle(TITLE);
        Image editorImage = new ImageIcon("D:\\Java_Projects\\OOP_Labs\\Lab_1\\images\\Text_Editor_Image.png").getImage();
        setIconImage(editorImage);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width, height = screenSize.height;
        setSize(new Dimension(2*width/3, 2*height/3));
        setLocationByPlatform(true);

        fileChooser = new JFileChooser(System.getProperty("user.dir"));

        textArea = new JTextArea(20, 60);
        textArea.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        popupMenu = new MyPopupMenu();
        MouseListener mouseListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                showPopupMenu(e);
            }
            @Override
            public void mousePressed(MouseEvent e){
                showPopupMenu(e);
            }
            private void showPopupMenu(MouseEvent e){
                if(e.isPopupTrigger()){
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        };
        scrollPane.addMouseListener(mouseListener);
        textArea.addMouseListener(mouseListener);
        scrollPane.setComponentPopupMenu(popupMenu);
        textArea.setComponentPopupMenu(popupMenu);

        setJMenuBar(new TextEditorMenuBar(this));
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    public TextEditor(File file){
        this();
        try{
            BufferedReader reader = new BufferedReader(new FileReader(file));
            textArea.read(reader, null);
        }catch(IOException e){
            e.printStackTrace();
        }

    }
}

class TextEditorMenuBar extends MyMenuBar{
    public TextEditorMenuBar(TextEditor frame){
        super(frame);
        JMenuItem newItem = new JMenuItem("Новий"), open = new JMenuItem("Відкрити"),
                save = new JMenuItem("Зберегти"), saveAs = new JMenuItem("Зберегти як...");
        fileMenu.add(newItem);
        fileMenu.add(open);
        fileMenu.add(save);
        fileMenu.add(saveAs);
    }
}












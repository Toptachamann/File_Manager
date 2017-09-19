import com.sun.scenario.effect.impl.sw.java.JSWBlend_COLOR_BURNPeer;

import javax.swing.*;
import javax.xml.soap.Text;
import java.awt.*;
import java.awt.event.ActionEvent;
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

    private JScrollPane textScrollPane;
    private JTextArea textArea;
    private JFileChooser fileChooser;

    private File originFile;
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
        textScrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

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
        textScrollPane .addMouseListener(mouseListener);
        textArea.addMouseListener(mouseListener);
        textScrollPane .setComponentPopupMenu(popupMenu);
        textArea.setComponentPopupMenu(popupMenu);
        InputMap inputMap = textScrollPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke("ctrl S"), "Save action");
        inputMap.put(KeyStroke.getKeyStroke("ctrl S A"), "Save as action");

        ActionMap actionMap = textScrollPane.getActionMap();
        actionMap.put("Save action", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(originFile != null){
                    try{
                        BufferedWriter writer = new BufferedWriter(new FileWriter(originFile));
                        textArea.write(writer);
                        changed = false;
                    } catch(IOException ex){
                        ex.printStackTrace();
                    }
                }
            }
        });
        actionMap.put()

        setJMenuBar(new TextEditorMenuBar(this));
        getContentPane().add(textScrollPane , BorderLayout.CENTER);
    }

    public TextEditor(File file){
        this();
        try{
            BufferedReader reader = new BufferedReader(new FileReader(file));
            textArea.read(reader, null);
            currentTitle = file.getName();
            originFile = file;
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void setOrigin(File file){
        if(file.exists()){
            ActionMap actionMap = textScrollPane.getActionMap();
            actionMap.remove("Save action");
            actionMap.put("Save as action", new )
        }
    }

    private class SaveAsAction extends AbstractAction{

        @Override
        public void actionPerformed(ActionEvent e) {
            //choose file
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












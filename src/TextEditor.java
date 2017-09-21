import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
    private boolean documentChanged = false;
    JPopupMenu popupMenu;


    public TextEditor(){
        setTitle(TITLE);
        Image editorImage = new ImageIcon("images\\Text_Editor_Image.png").getImage();
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
        textArea.addMouseListener(mouseListener);
        textArea.setComponentPopupMenu(popupMenu);

        addActions();

        setJMenuBar(new TextEditorMenuBar(this));
        getContentPane().add(textScrollPane , BorderLayout.CENTER);
    }

    private class TextEditorSearchPanel extends SearchPanel{
        public TextEditorSearchPanel(JFrame frame){
            super.frame = frame;

        }
    }

    public TextEditor(File file){
        this();
        originFile = file;
        try{
            BufferedReader reader = new BufferedReader(new FileReader(file));
            textArea.read(reader, null);
            currentTitle = file.getName();
            setOrigin(file);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void addActions(){
        InputMap inputMap = textArea.getInputMap(JComponent.WHEN_FOCUSED);
        inputMap.put(KeyStroke.getKeyStroke("ctrl S"), "Save action");
        inputMap.put(KeyStroke.getKeyStroke("ctrl S A"), "Save as action");

        ActionMap actionMap = textArea.getActionMap();
        actionMap.put("Save action", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(originFile != null){
                    saveDocument(originFile);
                }else{
                    saveDocumentAs();
                }
            }
        });
        actionMap.put("Save as action", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveDocumentAs();
            }
        });
        KeyListener keyListener = new KeyAdapter(){
            @Override
            public void keyPressed(KeyEvent e){
                documentChanged = true;
            }
        };
        textArea.addKeyListener(keyListener);
    }

    private void saveDocument(File file){
        if(documentChanged){
            try{
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                textArea.write(writer);
                documentChanged = false;
            } catch(IOException ex){
                ex.printStackTrace();
            }
        }
    }
    private void saveDocumentAs(){

    }

    private void setOrigin(File file){
        if(file.exists()){
            ActionMap actionMap = textScrollPane.getActionMap();
            actionMap.remove("Save action");
        }
    }

    private class TextEditorMenuBar extends MyMenuBar{
        public TextEditorMenuBar(TextEditor frame){
            super(frame);
            JMenuItem newItem = new JMenuItem("Новий"), open = new JMenuItem("Відкрити"),
                    save = new JMenuItem("Зберегти"), saveAs = new JMenuItem("Зберегти як...");
            newItem.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    TextEditor textEditor = new TextEditor();
                    textEditor.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    textEditor.setVisible(true);
                }
            });
            save.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(originFile != null){
                        saveDocument(originFile);
                    }else{
                        saveDocumentAs();
                    }
                }
            });
            saveAs.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    saveDocumentAs();
                }
            });
            open.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    MyFileChooser fileChooser = new MyFileChooser();
                    fileChooser.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    fileChooser.setVisible(true);
                }
            });
            fileMenu.add(newItem);
            fileMenu.add(open);
            fileMenu.add(save);
            fileMenu.add(saveAs);
        }
    }
}















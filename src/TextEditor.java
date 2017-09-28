
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

/**
 * Created by Timofey on 9/14/2017.
 */
public class TextEditor extends JFrame {

    private static final String UNTITLED = "Untitled";
    private static final String FRAME_NAME = "Файловий менеджер";

    private TextEditorMenuBar menuBar;
    private JScrollPane textScrollPane;
    private JTextArea textArea;

    private File originFile;
    private boolean documentChanged = false;
    private final MyFileChooser fileChooser;

    {
        fileChooser = new MyFileChooser(this);
    }

    public TextEditor() {
        originFile = null;
        setTitle(UNTITLED + " - " + FRAME_NAME);
        initializeFrame();
    }

    public TextEditor(File file) {
        originFile = file;
        updateTitle();
        initializeFrame();
        openFile(file);
    }

    private void initializeFrame() {
        Image editorImage = new ImageIcon("images\\Text_Editor_Image.png").getImage();
        setIconImage(editorImage);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width, height = screenSize.height;
        setSize(new Dimension(2 * width / 3, 2 * height / 3));
        setLocationByPlatform(true);

        textArea = new JTextArea(20, 60);
        textArea.setFont(new Font("Arial", Font.PLAIN, 12));
        textScrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        //popupMenu = new MyPopupMenu();
        MouseListener mouseListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showPopupMenu(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                showPopupMenu(e);
            }

            private void showPopupMenu(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    //popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        };
        textArea.addMouseListener(mouseListener);
        //textArea.setComponentPopupMenu(popupMenu);

        addActions();

        menuBar = new TextEditorMenuBar(this);
        setJMenuBar(menuBar);
        getContentPane().add(textScrollPane, BorderLayout.CENTER);
    }


    public void openFile(File file) {
        if (documentChanged) {
            if (originFile != null) {
                int reply = JOptionPane.showConfirmDialog(this,
                        "Файл містить незбережені зміни\nВи хочете їх зберегти?", "Запит підтвердження", JOptionPane.YES_NO_CANCEL_OPTION);
                if(reply == JOptionPane.YES_OPTION){
                    saveDocument(originFile);
                }else if(reply == JOptionPane.CANCEL_OPTION){
                    return;
                }
            } else {
                saveDocumentAs();
            }
        }
        originFile = file;
        updateTitle();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            textArea.setText("");
            String line;
            while ((line = reader.readLine()) != null) {
                textArea.append(line);
                textArea.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.textArea.setCaretPosition(0);
        this.documentChanged = false;
    }

    private void updateTitle() {
        if (originFile != null) {
            setTitle(originFile.getName() + " - " + FRAME_NAME);
        } else {
            setTitle(UNTITLED + " - " + FRAME_NAME);
        }
    }

    private void addActions() {
        InputMap inputMap = textArea.getInputMap(JComponent.WHEN_FOCUSED);
        inputMap.put(KeyStroke.getKeyStroke("ctrl S"), "Save action");
        inputMap.put(KeyStroke.getKeyStroke("ctrl S A"), "Save as action");

        ActionMap actionMap = textArea.getActionMap();
        actionMap.put("Save action", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (originFile != null) {
                    saveDocument(originFile);
                } else {
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
        DocumentListener documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                TextEditor.this.documentChanged = true;
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                TextEditor.this.documentChanged = true;
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        };
        this.textArea.getDocument().addDocumentListener(documentListener);

        WindowListener windowListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (documentChanged) {
                    int result = JOptionPane.showConfirmDialog(TextEditor.this,
                            "Файл містить незбережені зміни.\n Ви хочете їх зберегти?", "Запит підтвердження", JOptionPane.YES_NO_CANCEL_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        saveDocument(originFile);
                        dispose();
                    } else if (result == JOptionPane.NO_OPTION) {
                        dispose();
                    }
                }
            }
        };
        this.addWindowListener(windowListener);
    }

    private void saveDocument(File file) {
        if (documentChanged) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                textArea.write(writer);
                documentChanged = false;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void saveDocumentAs() {

    }


    private class TextEditorMenuBar extends MyMenuBar {
        public TextEditorMenuBar(TextEditor frame) {
            super(frame);
            JMenuItem newItem = new JMenuItem("Новий"), open = new JMenuItem("Відкрити"),
                    save = new JMenuItem("Зберегти"), saveAs = new JMenuItem("Зберегти як...");
            newItem.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (documentChanged) {
                        int result = JOptionPane.showConfirmDialog(TextEditor.this,
                                "Файл містить незбережені зміни.\n Ви хочете їх зберегти?", "Запит підтвердження", JOptionPane.YES_NO_CANCEL_OPTION);
                        if (result == JOptionPane.YES_OPTION) {
                            saveDocument(originFile);
                        }
                        originFile = null;
                        textArea.setText("");
                    }
                }
            });
            save.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (originFile != null) {
                        saveDocument(originFile);
                    } else {
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
                    TextEditor.this.fileChooser.showDialog();
                }
            });
            fileMenu.add(newItem);
            fileMenu.add(open);
            fileMenu.add(save);
            fileMenu.add(saveAs);
        }
    }
}















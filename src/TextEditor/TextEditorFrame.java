package TextEditor;

import auxiliary.InvalidFileException;
import components.MyFileChooser;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Created by Timofey on 9/14/2017. */
public class TextEditorFrame extends JFrame {

  private static final String TITLE = "Text Editor";
  private static final Font DEFAULT_FONT = new Font("Trebuchet MS", Font.PLAIN, 14);
  private final MyFileChooser fileChooser;
  private TextEditorMenuBar menuBar;
  private TextEditorPopupMenu popupMenu;
  private JScrollPane textScrollPane;
  private JTextArea textArea;
  private File originFile;
  private boolean documentChanged = false;
  private Highlighter highlighter;
  private Highlighter.HighlightPainter painter;

  {
    fileChooser = new MyFileChooser(this);
  }

  public TextEditorFrame() {
    init();
    originFile = null;
    updateTitle(null);
    try{
      openNewFile();
    } catch(IOException e){
      e.printStackTrace();
    }
  }

  public TextEditorFrame(File file) throws IOException {
    init();
    originFile = file;
    openFile(file);
    updateTitle(file);
  }

  private void init() {
    Image editorImage = new ImageIcon("images\\Text_Editor_Image.png").getImage();
    setIconImage(editorImage);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int width = screenSize.width, height = screenSize.height;
    setSize(new Dimension(2 * width / 3, 2 * height / 3));
    setLocationByPlatform(true);
    addComponents();
    addListeners();
    addActions();
  }

  private void addComponents() {
    menuBar = new TextEditorMenuBar();
    setJMenuBar(menuBar);

    textArea = new JTextArea(20, 60);
    textArea.setFont(DEFAULT_FONT);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    textScrollPane =
        new JScrollPane(
            textArea,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    getContentPane().add(textScrollPane, BorderLayout.CENTER);
    highlighter = textArea.getHighlighter();
    painter = new DefaultHighlighter.DefaultHighlightPainter(new Color(135, 206, 250));

    popupMenu = new TextEditorPopupMenu();
    textArea.setComponentPopupMenu(popupMenu);
  }

  private void addListeners() {
    DocumentListener documentListener =
        new DocumentListener() {
          @Override
          public void insertUpdate(DocumentEvent e) {
            TextEditorFrame.this.documentChanged = true;
            highlighter.removeAllHighlights();
          }

          @Override
          public void removeUpdate(DocumentEvent e) {
            TextEditorFrame.this.documentChanged = true;
            highlighter.removeAllHighlights();
          }

          @Override
          public void changedUpdate(DocumentEvent e) {}
        };
    this.textArea.getDocument().addDocumentListener(documentListener);
    MouseListener mouseListener =
        new MouseAdapter() {
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
              // popupMenu.show(e.getTable(), e.getX(), e.getY());
            }
          }
        };
    textArea.addMouseListener(mouseListener);
    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        try{
          saveCurrentFile();
        } catch (InvalidFileException ex) {
          invalidFile();
        } catch (IOException ex) {
          saveFileFailed();
        }
      }
    });
  }

  private void addActions() {
    InputMap inputMap = textArea.getInputMap(JComponent.WHEN_FOCUSED);
    inputMap.put(KeyStroke.getKeyStroke("ctrl shift N"), "New document action");
    inputMap.put(KeyStroke.getKeyStroke("ctrl shift O"), "Open document action");
    inputMap.put(KeyStroke.getKeyStroke("ctrl S"), "Save action");
    inputMap.put(KeyStroke.getKeyStroke("ctrl shift A"), "Save as action");
    inputMap.put(KeyStroke.getKeyStroke("ctrl F"), "Search word action");

    ActionMap actionMap = textArea.getActionMap();
    actionMap.put("New document action", new NewDocumentAction());
    actionMap.put("Open document action", new OpenDocumentAction());
    actionMap.put("Save action", new SaveAction());
    actionMap.put("Save as action", new SaveAsAction());
    actionMap.put("Search word action", new SearchWordAction());
  }

  private void openNewFile() throws IOException {
    saveCurrentFile();
    this.originFile = null;
    textArea.setText("");
    updateTitle(null);
    documentChanged = false;
  }

  private void openFile(File file) throws IOException {
    if(!isValidOrigin(file)){
      throw new InvalidFileException("Invalid origin");
    }
    saveCurrentFile();
    Validate.isTrue(isValidOrigin(file), "Can't open such file: " + file);
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      textArea.setText("");
      String line;
      while ((line = reader.readLine()) != null) {
        textArea.append(line);
        textArea.append("\n");
      }
      updateTitle(file);
      textArea.setCaretPosition(0);
      documentChanged = false;
      this.originFile = file;
    }
  }

  private void saveCurrentFile() throws IOException {
    if (documentChanged) {
      if (originFile == null) {
        saveCurrentFileAs();
      } else {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(originFile))) {
          for (String line : textArea.getText().split("\n")) {
            writer.write(line);
            writer.write('\n');
          }
        }
        documentChanged = false;
      }
    }
  }

  private void saveCurrentFileAs() throws IOException {
    if (documentChanged) {
      File selectedFile = getFileFromUser();
      if (isValidOrigin(selectedFile)) {
        this.originFile = selectedFile;
        saveCurrentFile();
      } else {
        throw new InvalidFileException("Invalid origin");
      }
    }
  }

  public void highlightTitle() {
    highlightTitle(textArea.getText(), 0);
  }

  private void highlightTitle(String text, int fromIndex) {
    int start = text.indexOf("<title>", fromIndex);
    if (start != -1) {
      int end = text.indexOf("</title>", start);
      if (end != -1) {
        try {
          highlighter.addHighlight(start + "<title>".length(), end, painter);
          highlightTitle(text, end + 1);
        } catch (BadLocationException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private void highlightWord(String word) {
    Set<String> differentWords = getAllDifferent(word);
    StringBuilder builder = new StringBuilder();
    if (differentWords.size() > 1) {
      builder.append("File contains several words, which satisfy specified character sequence \n");
    } else if (differentWords.size() == 1) {
      builder.append("File contains single work, which satisfy specified character sequence\n");
    } else {
      builder.append("File doesn't contain works, which satisfy specified character sequence\n");
    }
    for (String matchedWord : differentWords) {
      builder.append(matchedWord);
      builder.append("\n");
      highlightChosenWord(matchedWord);
    }
    JOptionPane.showMessageDialog(
        TextEditorFrame.this,
        builder.toString(),
        "Search results",
        JOptionPane.INFORMATION_MESSAGE);
  }

  private void highlightChosenWord(String word) {
    try {
      int lengthCount = 0;
      for (String line : textArea.getText().split("\\n")) {
        int index = line.indexOf(word);
        while (index != -1) {
          highlighter.addHighlight(
              lengthCount + index, lengthCount + index + word.length(), painter);
          index = line.indexOf(word, index + 1);
        }
        lengthCount += line.length() + 1;
      }
    } catch (BadLocationException e) {
      e.printStackTrace();
    }
  }

  private Set<String> getAllDifferent(String pattern) {
    HashSet<String> ans = new HashSet<>();
    Pattern wordPattern = Pattern.compile("([a-zA-Z1-9]+)");
    for (String line : textArea.getText().split("\n")) {
      Matcher matcher = wordPattern.matcher(line);
      while (matcher.find()) {
        String candidate = matcher.group(1);
        if (candidate.contains(pattern)) {
          if (!ans.contains(candidate)) {
            ans.add(candidate);
          }
        }
      }
    }
    return ans;
  }

  private void updateTitle(@Nullable File file) {
    if (originFile == null) {
      setTitle("Untitled - " + TITLE);
    } else {
      setTitle(file.getAbsolutePath() + " - " + TITLE);
    }
  }

  private void invalidFile() {
    JOptionPane.showMessageDialog(
        this, "Can't save to specified file", "Message", JOptionPane.INFORMATION_MESSAGE);
  }

  private void saveFileFailed() {
    JOptionPane.showMessageDialog(
        this, "Can't save file", "Message", JOptionPane.INFORMATION_MESSAGE);
  }

  private void openFileFailed() {
    JOptionPane.showMessageDialog(
        this, "Can't open file", "Message", JOptionPane.INFORMATION_MESSAGE);
  }

  @Contract("null -> false")
  private boolean isValidOrigin(@Nullable File file) {
    return file != null
        && file.canRead()
        && (file.getName().endsWith(".txt") || file.getName().endsWith(".html"));
  }

  private int wantsToSave() {
    return JOptionPane.showConfirmDialog(
        this,
        "File contains unsaved changes\nDo you want to save them?",
        "Confirm dialog",
        JOptionPane.YES_NO_CANCEL_OPTION);
  }

  private File getFileFromUser() {
    fileChooser.showDialog();
    return fileChooser.getFile();
  }

  private String getWordFromUser(){
    return JOptionPane.showInputDialog(
        TextEditorFrame.this,
        "Enter a word for searching",
        "Search",
        JOptionPane.QUESTION_MESSAGE);
  }

  private class NewDocumentAction extends AbstractAction{

    @Override
    public void actionPerformed(ActionEvent e) {
      try {
        openNewFile();
      } catch (InvalidFileException ex) {
        invalidFile();
      } catch (IOException ex) {
        saveFileFailed();
      }
    }
  }

  private class OpenDocumentAction extends AbstractAction{

    @Override
    public void actionPerformed(ActionEvent e) {
      File selectedFile = getFileFromUser();
      try {
        openFile(selectedFile);
      } catch (InvalidFileException ex) {
        invalidFile();
      } catch (IOException ex) {
        openFileFailed();
      }
    }
  }

  private class SaveAction extends AbstractAction{

    @Override
    public void actionPerformed(ActionEvent e) {
      try {
        saveCurrentFile();
      } catch (InvalidFileException ex) {
        invalidFile();
      } catch (IOException ex) {
        saveFileFailed();
      }
    }

  }

  private class SaveAsAction extends AbstractAction{

    @Override
    public void actionPerformed(ActionEvent e) {
      try {
        saveCurrentFileAs();
      } catch (InvalidFileException ex) {
        invalidFile();
      } catch (IOException ex) {
        saveFileFailed();
      }
    }
  }

  private class SearchWordAction extends AbstractAction{
    @Override
    public void actionPerformed(ActionEvent e) {
      String word = getWordFromUser();
      if (word != null && word.length() > 0) {
        highlighter.removeAllHighlights();
        highlightChosenWord(word);
      }
    }
  }

  private class TextEditorMenuBar extends JMenuBar {
    private JMenu fileMenu;
    private JMenuItem newItem;
    private JMenuItem openItem;
    private JMenuItem saveItem;
    private JMenuItem saveAsItem;

    public TextEditorMenuBar() {
      fileMenu = new JMenu("File");
      add(fileMenu);
      newItem = new JMenuItem("New");
      openItem = new JMenuItem("Open");
      saveItem = new JMenuItem("Save");
      saveAsItem = new JMenuItem("Save as...");
      fileMenu.add(newItem);
      fileMenu.add(openItem);
      fileMenu.add(saveItem);
      fileMenu.add(saveAsItem);
      newItem.addActionListener(new NewDocumentAction());
      saveItem.addActionListener(new SaveAction());
      saveAsItem.addActionListener(new SaveAsAction());
      openItem.addActionListener(new OpenDocumentAction());
    }
  }

  private class TextEditorPopupMenu extends JPopupMenu{
    private JMenuItem selectAllItem;
    public TextEditorPopupMenu(){
      selectAllItem = new JMenuItem("Select all");
      add(selectAllItem);
      selectAllItem.addActionListener(e -> {
        String text = textArea.getText();
        textArea.select(0, text.length() - 1);
      });
    }
  }
}

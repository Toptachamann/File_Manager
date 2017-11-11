package components;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
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
import java.awt.event.WindowListener;
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
public class TextEditor extends JFrame {

  private static final String UNTITLED = "Untitled";
  private static final String FRAME_NAME = "Text Editor";
  private final MyFileChooser fileChooser;
  private TextEditorMenuBar menuBar;
  private JScrollPane textScrollPane;
  private JTextArea textArea;
  private File originFile;
  private boolean documentChanged = false;
  private Highlighter highlighter;
  private Highlighter.HighlightPainter painter;

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

  private void initializeFrame() {
    Image editorImage = new ImageIcon("images\\Text_Editor_Image.png").getImage();
    setIconImage(editorImage);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int width = screenSize.width, height = screenSize.height;
    setSize(new Dimension(2 * width / 3, 2 * height / 3));
    setLocationByPlatform(true);

    textArea = new JTextArea(20, 60);
    textArea.setFont(new Font("Arial", Font.PLAIN, 12));
    textScrollPane =
        new JScrollPane(
            textArea,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    highlighter = textArea.getHighlighter();
    painter = new DefaultHighlighter.DefaultHighlightPainter(new Color(135, 206, 250));
    // popupMenu = new MyPopupMenu();
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
    // textArea.setComponentPopupMenu(popupMenu);

    addActions();

    menuBar = new TextEditorMenuBar(this);
    setJMenuBar(menuBar);
    getContentPane().add(textScrollPane, BorderLayout.CENTER);
  }

  private boolean canOpen(File file) {
    if (file.canRead() && (file.getName().endsWith(".txt") || file.getName().endsWith(".html"))) {
      return true;
    }
    return false;
  }

  public void openFile(File file) {

    if (documentChanged) {
      if (originFile != null) {
        int reply =
            JOptionPane.showConfirmDialog(
                this,
                "File contains unsaved changes\nDo you want to save them?",
                "Confirm dialog",
                JOptionPane.YES_NO_CANCEL_OPTION);
        if (reply == JOptionPane.YES_OPTION) {
          saveDocument(originFile);
        } else if (reply == JOptionPane.CANCEL_OPTION) {
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
    inputMap.put(KeyStroke.getKeyStroke("ctrl shift A"), "Save as action");
    inputMap.put(KeyStroke.getKeyStroke("ctrl F"), "Search word action");

    Action saveAction =
        new AbstractAction() {
          @Override
          public void actionPerformed(ActionEvent e) {
            if (originFile != null) {
              saveDocument(originFile);
            } else {
              saveDocumentAs();
            }
          }
        };
    Action saveAsAction =
        new AbstractAction() {
          @Override
          public void actionPerformed(ActionEvent e) {
            saveDocumentAs();
          }
        };

    Action searchWordAction =
        new AbstractAction() {
          @Override
          public void actionPerformed(ActionEvent e) {
            String word =
                JOptionPane.showInputDialog(
                    TextEditor.this,
                    "Enter a word for searching",
                    "Search",
                    JOptionPane.QUESTION_MESSAGE);
            if (word != null && word.length() > 0) {
              highlighter.removeAllHighlights();
              Set<String> differentWords = getAllDifferent(word);
              StringBuilder builder = new StringBuilder();
              if (differentWords.size() > 1) {
                builder.append(
                    "File contains several words, which satisfy specified character sequence \n");
              } else if (differentWords.size() == 1) {
                builder.append(
                    "File contains single work, which satisfy specified character sequence\n");
              } else {
                builder.append(
                    "File doesn't contain works, which satisfy specified character sequence\n");
              }
              for (String matchedWord : differentWords) {
                builder.append(matchedWord);
                builder.append("\n");
                highlightChoosenWord(matchedWord);
              }
              JOptionPane.showMessageDialog(
                  TextEditor.this,
                  builder.toString(),
                  "Search results",
                  JOptionPane.INFORMATION_MESSAGE);
            }
          }
        };

    ActionMap actionMap = textArea.getActionMap();
    actionMap.put("Save action", saveAction);
    actionMap.put("Save as action", saveAsAction);
    actionMap.put("Search word action", searchWordAction);

    DocumentListener documentListener =
        new DocumentListener() {
          @Override
          public void insertUpdate(DocumentEvent e) {
            TextEditor.this.documentChanged = true;
            highlighter.removeAllHighlights();
          }

          @Override
          public void removeUpdate(DocumentEvent e) {
            TextEditor.this.documentChanged = true;
            highlighter.removeAllHighlights();
          }

          @Override
          public void changedUpdate(DocumentEvent e) {}
        };
    this.textArea.getDocument().addDocumentListener(documentListener);

    WindowListener windowListener =
        new WindowAdapter() {
          @Override
          public void windowClosing(WindowEvent e) {
            if (documentChanged) {
              int result =
                  JOptionPane.showConfirmDialog(
                      TextEditor.this,
                      "File contains unsaved changes\nDo you want to save them?",
                      "Confirm dialog",
                      JOptionPane.YES_NO_CANCEL_OPTION);
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

  private void highlightChoosenWord(String word) {
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

  private void saveDocumentAs() {}

  private class TextEditorMenuBar extends MyMenuBar {
    public TextEditorMenuBar(TextEditor frame) {
      super(frame);
      JMenuItem newItem = new JMenuItem("New"),
          open = new JMenuItem("Open"),
          save = new JMenuItem("Save"),
          saveAs = new JMenuItem("Save as...");
      newItem.addActionListener(
          new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
              if (documentChanged) {
                int result =
                    JOptionPane.showConfirmDialog(
                        TextEditor.this,
                        "File contains unsaved changes\nDo you want to save them?",
                        "Confirm dialog",
                        JOptionPane.YES_NO_CANCEL_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                  saveDocument(originFile);
                }
                originFile = null;
                textArea.setText("");
              }
            }
          });
      save.addActionListener(
          new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
              if (originFile != null) {
                saveDocument(originFile);
              } else {
                saveDocumentAs();
              }
            }
          });
      saveAs.addActionListener(
          new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
              saveDocumentAs();
            }
          });
      open.addActionListener(
          new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
              fileChooser.showDialog();
              File file = fileChooser.getFile();
              openFile(file);
            }
          });
      fileMenu.add(newItem);
      fileMenu.add(open);
      fileMenu.add(save);
      fileMenu.add(saveAs);
    }
  }
}

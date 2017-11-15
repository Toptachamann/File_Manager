package table_manager;

import auxiliary.GBC;
import auxiliary.InvalidFileException;
import file_manager.MyFileChooser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

public class TableEditorFrame extends JFrame {
  private static String TITLE = "Table Manager";

  private JPanel mainPanel;

  private ComponentManager componentManager;
  private JLabel formulaLabel;
  private JTextField formulaField;
  private MyFileChooser fileChooser;

  public TableEditorFrame() {
    init();
    openNewTable();
  }

  public TableEditorFrame(File inputFile) throws IOException {
    init();
    ConcreteTableModel tableModel = readTable(inputFile);
    openNewTable(tableModel, inputFile);
  }

  public static void main(String[] argc) {
    EventQueue.invokeLater(
        () -> {
          try {
            TableEditorFrame managerFrame =
                new TableEditorFrame(new File("C:\\File_Manager_Test\\new table.json"));
            managerFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            managerFrame.setVisible(true);
          } catch (IOException e) {
            e.printStackTrace();
          }
        });
  }

  private void init() {
    setTitle(TITLE);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int width = screenSize.width;
    int height = screenSize.height;
    setSize(new Dimension(5 * width / 6, 5 * height / 6));
    setLocationRelativeTo(null);
    mainPanel = new JPanel(new BorderLayout());
    add(mainPanel);
    addMenu();
    addListeners();
  }

  private void openNewTable() {
    mainPanel.removeAll();
    revalidate();
    repaint();
    createComponents();
    addComponents();
    addActions();
    setOrigin(null);
    revalidate();
    repaint();
  }

  private void openNewTable(ConcreteTableModel tableModel, File origin) {
    mainPanel.removeAll();
    revalidate();
    repaint();
    createComponents(tableModel);
    addComponents();
    setOrigin(origin);
    addActions();
    revalidate();
    repaint();
  }

  @NotNull
  private ConcreteTableModel readTable(File file) throws IOException {
    AbstractTableReader reader = new JsonTableReader();
    ConcreteTableModel tableModel = reader.readTable(file);
    return tableModel;
  }

  private void saveCurrentTableToFile() {
    try {
      saveCurrentTable();
    } catch (InvalidFileException ex) {
      saveToInvalidFile();
    } catch (IOException ex) {
      ex.printStackTrace();
      ioException();
    }
  }

  private void saveCurrentTable() throws IOException {
    if (componentManager.hasTableOrigin()) {
      componentManager.saveTable(new JsonTableSaver());
    } else {
      saveCurrentTableAs();
    }
  }

  private void saveCurrentTableAs() throws IOException {
    File origin = getFileFromFileChooser();
    JsonTableSaver saver = new JsonTableSaver();
    if (saver.isValidFile(origin)) {
      componentManager.setOrigin(origin);
      componentManager.saveTable(new JsonTableSaver());
    } else {
      throw new InvalidFileException("Not a *.json file");
    }
  }

  private void addActions() {
    JTable table = componentManager.getTable();
    InputMap inputMap = table.getInputMap(JComponent.WHEN_FOCUSED);
    inputMap.put(KeyStroke.getKeyStroke("ctrl N"), "New table action");
    inputMap.put(KeyStroke.getKeyStroke("ctrl O"), "Open action");
    inputMap.put(KeyStroke.getKeyStroke("ctrl S"), "Save action");

    ActionMap actionMap = table.getActionMap();
    actionMap.put("New table action", new NewTableAction());
    actionMap.put("Open action", new OpenAction());
    actionMap.put("Save action", new SaveAction());
  }

  @Nullable
  private File getFileFromFileChooser() {
    if (fileChooser == null) {
      fileChooser = new MyFileChooser(this);
    }
    fileChooser.showDialog();
    return fileChooser.getFile();
  }

  private void addMenu() {
    JMenuBar menuBar = new TableManagerMenuBar();
    setJMenuBar(menuBar);
  }

  private void createComponents() {
    componentManager = new ComponentManager(this);
    formulaLabel = new JLabel("Formula: ", JLabel.CENTER);
    formulaField = componentManager.getJTextField();
  }

  private void createComponents(ConcreteTableModel tableModel) {
    componentManager = new ComponentManager(this, tableModel);
    formulaLabel = new JLabel("Formula: ", JLabel.CENTER);
    formulaField = componentManager.getJTextField();
  }

  private void addComponents() {
    JTable table = componentManager.getTable();
    JScrollPane scrollPane =
        new JScrollPane(
            table,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    JPanel tablePanel = new JPanel();
    tablePanel.setLayout(new BorderLayout());
    tablePanel.add(scrollPane);

    JPanel formulaPanel = new JPanel();
    formulaPanel.setLayout(new GridBagLayout());
    formulaPanel.add(formulaLabel, new GBC(0, 0, 1, 1, 0, 0).setInsets(7, 5, 7, 5));
    formulaPanel.add(
        formulaField, new GBC(1, 0, 1, 1, 1, 0).setInsets(7, 0, 7, 0).setAnchor(GBC.WEST));

    mainPanel.setLayout(new BorderLayout());
    mainPanel.add(tablePanel, BorderLayout.CENTER);
    mainPanel.add(formulaPanel, BorderLayout.NORTH);
  }

  private void addListeners() {
    addWindowListener(
        new WindowAdapter() {
          @Override
          public void windowClosing(WindowEvent e) {
            if (!componentManager.isSaved()) {
              int reply = wantsToSave();
              if (reply == JOptionPane.YES_OPTION) {
                try {
                  saveCurrentTable();
                  dispose();
                } catch (InvalidFileException ex) {
                  saveToInvalidFile();
                } catch (IOException ex) {
                  ex.printStackTrace();
                  ioException();
                }
              } else if (reply == JOptionPane.NO_OPTION) {
                dispose();
              }
            } else {
              dispose();
            }
          }
        });
  }

  private void setOrigin(@Nullable File origin) {
    componentManager.setOrigin(origin);
    updateTitle(origin);
  }

  private void updateTitle(@Nullable File origin) {
    if (origin == null) {
      setTitle("Untitled - " + TITLE);
    } else {
      setTitle(origin.getAbsolutePath() + " - " + TITLE);
    }
  }

  private int wantsToSave() {
    return JOptionPane.showConfirmDialog(
        TableEditorFrame.this,
        "Table contains unsaved changes.\nDo you want to save them?",
        "Confirm dialog",
        JOptionPane.YES_NO_CANCEL_OPTION);
  }

  private void saveToInvalidFile() {
    JOptionPane.showMessageDialog(
        TableEditorFrame.this,
        "Tables can be saved only to *.json files",
        "Message",
        JOptionPane.INFORMATION_MESSAGE);
  }

  private void openInvalidFile() {
    JOptionPane.showMessageDialog(
        this, "Can't open specified file", "Message", JOptionPane.INFORMATION_MESSAGE);
  }

  private void ioException() {
    JOptionPane.showMessageDialog(
        TableEditorFrame.this, "Can't save table", "Warning", JOptionPane.WARNING_MESSAGE);
  }

  private class TableManagerMenuBar extends JMenuBar {
    public TableManagerMenuBar() {
      JMenu fileMenu = new JMenu("File");
      add(fileMenu);
      JMenuItem newItem = new JMenuItem("New");
      JMenuItem openItem = new JMenuItem("Open");
      JMenuItem saveItem = new JMenuItem("Save");
      JMenuItem saveAsItem = new JMenuItem("Save as");
      fileMenu.add(newItem);
      fileMenu.add(openItem);
      fileMenu.add(saveItem);
      fileMenu.add(saveAsItem);

      newItem.addActionListener(new NewTableAction());
      openItem.addActionListener(new OpenAction());
      saveItem.addActionListener(new SaveAction());
      saveAsItem.addActionListener(new SaveAction());

      JMenu optionsMenu = new JMenu("Options");
      add(optionsMenu);
      JMenuItem switchStateItem = new JMenuItem("Switch state");
      optionsMenu.add(switchStateItem);
      switchStateItem.addActionListener((e) -> componentManager.getTableModel().switchState());
    }
  }

  private class NewTableAction extends AbstractAction {

    @Override
    public void actionPerformed(ActionEvent e) {
      if (!componentManager.isSaved()) {
        int reply = wantsToSave();
        if (reply == JOptionPane.YES_OPTION) {
          saveCurrentTableToFile();
        } else if (reply == JOptionPane.NO_OPTION) {
          openNewTable();
        }
      } else {
        openNewTable();
      }
    }
  }

  private class OpenAction extends AbstractAction {
    @Override
    public void actionPerformed(ActionEvent e) {
      try {
        File source = getFileFromFileChooser();
        ConcreteTableModel tableModel = readTable(source);
        saveCurrentTable();
        openNewTable(tableModel, source);
      } catch (InvalidFileException ex) {
        openInvalidFile();
      } catch (IOException ex) {
        ex.printStackTrace();
        ioException();
      }
    }
  }

  private class SaveAction extends AbstractAction {
    @Override
    public void actionPerformed(ActionEvent e) {
      saveCurrentTableToFile();
      updateTitle(componentManager.getOrigin());
    }
  }

  private class SaveAsAction extends AbstractAction {
    @Override
    public void actionPerformed(ActionEvent e) {
      try {
        saveCurrentTableAs();
        updateTitle(componentManager.getOrigin());
      } catch (InvalidFileException ex) {
        saveToInvalidFile();
      } catch (IOException ex) {
        ex.printStackTrace();
        ioException();
      }
    }
  }
}

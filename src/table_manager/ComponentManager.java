package table_manager;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class ComponentManager {
  private static final int DEFAULT_ROW_HEIGHT = 20;
  private static final int DEFAULT_COLUMN_WIDTH = 100;
  private static final int DEFAULT_INDEXATION_COLUMN_WIDTH = 40;

  private int rowHeight = DEFAULT_ROW_HEIGHT;
  private int columnWidth = DEFAULT_COLUMN_WIDTH;

  private JFrame owner;

  private ConcreteTableModel tableModel;
  private JTable table;
  private JTextField expressionTextField;

  private boolean saved = true;
  private File origin;

  public ComponentManager(JFrame owner) {
    this.owner = owner;
    createTable();
    init();
  }

  public ComponentManager(JFrame owner, File origin) {
    this(owner);
    setOrigin(origin);
  }

  public ComponentManager(JFrame owner, ConcreteTableModel tableModel) {
    this.owner = owner;
    createTable(tableModel);
    init();
  }

  public ComponentManager(JFrame owner, ConcreteTableModel tableModel, File origin) {
    this(owner, tableModel);
    setOrigin(origin);
  }

  private void init() {
    adjustTable();
    createJTextField();
  }

  private void createTable() {
    tableModel = new ConcreteTableModel(100, 100);
    table = new JTable(tableModel);
  }

  private void createTable(ConcreteTableModel tableModel) {
    this.tableModel = tableModel;
    this.table = new JTable(tableModel);
  }

  private void adjustTable() {
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setCellSelectionEnabled(true);
    table.putClientProperty("terminateEditOnFocusLost", true);
    updateRowHeight();
    updateColumnWidth();
    DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
    cellRenderer.setHorizontalAlignment(JLabel.CENTER);
    table.getColumnModel().getColumn(0).setCellRenderer(cellRenderer);
    ListSelectionModel cellSelectionModel = table.getSelectionModel();
    cellSelectionModel.addListSelectionListener(
        e -> {
          int row = table.getSelectedRow();
          int column = table.getSelectedColumn();
          if (row != -1 && column != -1) {
            expressionTextField.setText(tableModel.getExpression(row, column));
          }
        });
    TableModelListener modelListener = e -> saved = false;
    tableModel.addTableModelListener(modelListener);
  }

  private void createJTextField() {
    expressionTextField = new JTextField(119);
    expressionTextField.setDisabledTextColor(Color.BLACK);
    expressionTextField.addFocusListener(
        new FocusAdapter() {
          @Override
          public void focusLost(FocusEvent e) {
            expressionTextField.setEnabled(false);
          }
        });
    expressionTextField.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            expressionTextField.requestFocusInWindow();
            if (hasSingleSelectedCell()) {
              int column = table.getSelectedColumn();
              if (column > 0) {
                expressionTextField.setEnabled(true);
              }
            } else {
              expressionTextField.setEnabled(false);
            }
          }
        });
    Action enterAction =
        new AbstractAction() {
          @Override
          public void actionPerformed(ActionEvent e) {
            String expression = expressionTextField.getText();
            int row = table.getSelectedRow();
            int column = table.getSelectedColumn();
            if (row == -1 || column == -1) {
              JOptionPane.showMessageDialog(
                  owner, "Cell not selected", "Message", JOptionPane.INFORMATION_MESSAGE);
            }
            tableModel.setExpression(expression, row, column);
          }
        };
    InputMap inputMap = expressionTextField.getInputMap(JComponent.WHEN_FOCUSED);
    inputMap.put(KeyStroke.getKeyStroke("ENTER"), "Expression submit");

    ActionMap actionMap = expressionTextField.getActionMap();
    actionMap.put("Expression submit", enterAction);
  }

  private void updateRowHeight() {
    table.setRowHeight(this.rowHeight);
  }

  private void updateColumnWidth() {
    table.getColumnModel().getColumn(0).setPreferredWidth(DEFAULT_INDEXATION_COLUMN_WIDTH);
    for (int i = 1; i < table.getColumnCount(); i++) {
      TableColumn column = table.getColumnModel().getColumn(i);
      column.setPreferredWidth(this.columnWidth);
    }
  }

  public void setRowHeight(int rowHeight) {
    this.rowHeight = rowHeight;
    updateRowHeight();
  }

  public void setColumnWidth(int columnWidth) {
    this.columnWidth = columnWidth;
  }

  public void saveTable(AbstractTableSaver saver) throws IOException {
    if (this.origin != null) {
      saver.persist(origin, tableModel);
      setSaved(true);
    } else {
      throw new NullPointerException("Origin is null");
    }
  }

  public boolean hasTableOrigin() {
    return origin != null;
  }

  public void setTableModel(ConcreteTableModel tableModel) {
    createTable(tableModel);
    adjustTable();
  }

  public boolean hasSingleSelectedCell() {
    int columns = table.getSelectedColumnCount();
    int rows = table.getSelectedRowCount();
    return rows == 1 && columns == 1;
  }

  public JTable getTable() {
    return table;
  }

  public JTextField getJTextField() {
    return expressionTextField;
  }

  public boolean isSaved() {
    return saved;
  }

  private void setSaved(boolean saved) {
    this.saved = saved;
  }

  public File getOrigin() {
    return origin;
  }

  public void setOrigin(File origin) {
    this.origin = origin;
  }
}

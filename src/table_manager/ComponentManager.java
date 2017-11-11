package table_manager;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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

  private TablePopupMenu popupMenu;

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
    tableModel = new ConcreteTableModel(10, 10);
    table = new JTable(tableModel);
  }

  private void createTable(ConcreteTableModel tableModel) {
    this.tableModel = tableModel;
    this.table = new JTable(tableModel);
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

  private void adjustTable() {
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    table.setColumnSelectionInterval(3, 3);
    table.setAutoCreateColumnsFromModel(false);
    table.putClientProperty("terminateEditOnFocusLost", true);
    popupMenu = new TablePopupMenu();
    table.setComponentPopupMenu(popupMenu);
    table.getTableHeader().setComponentPopupMenu(popupMenu);
    updateRowHeight();
    updateColumnWidth();
    addListeners();
    addActions();
  }

  private void addListeners() {
    JTableHeader tableHeader = table.getTableHeader();
    tableHeader.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            if(e.getButton() == MouseEvent.BUTTON1){
              int columnIndex = tableHeader.columnAtPoint(e.getPoint());
              if (tableHeader.getCursor().getType() == Cursor.E_RESIZE_CURSOR) {
                e.consume();
              } else {
                table.setColumnSelectionAllowed(true);
                table.setRowSelectionAllowed(false);
                table.clearSelection();
                table.setColumnSelectionInterval(columnIndex, columnIndex);
              }
            }
          }
        });
    table.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            System.out.println(e.getButton());
            if(e.getButton() == MouseEvent.BUTTON1){
              int rowIndex = table.rowAtPoint(e.getPoint());
              int columnIndex = table.columnAtPoint(e.getPoint());
              if (columnIndex == 0) {
                table.setColumnSelectionAllowed(false);
                table.setRowSelectionAllowed(true);
                table.clearSelection();
                table.setRowSelectionInterval(rowIndex, rowIndex);
              } else {
                table.setColumnSelectionAllowed(true);
                table.setRowSelectionAllowed(true);
                table.changeSelection(rowIndex, columnIndex, false, false);
              }
            }
          }
        });
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

  private void addActions() {
    KeyListener removeListener =
        new KeyAdapter() {
          @Override
          public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_DELETE) {
              deletePressed();
            }
          }
        };
    table.addKeyListener(removeListener);
    table.getTableHeader().addKeyListener(removeListener);
  }

  private void deletePressed() {
    if (table.getColumnSelectionAllowed() && !table.getRowSelectionAllowed()) {
      int column = table.getSelectedColumn();
      if (column != -1 && column != 0) {
        TableColumn tableColumn = table.getColumnModel().getColumn(column);
        table.getColumnModel().removeColumn(tableColumn);
        tableModel.removeColumn(column);
      }
    } else if (table.getRowSelectionAllowed() && !table.getColumnSelectionAllowed()) {
      int row = table.getSelectedRow();
      if (row != -1) {
        tableModel.removeRow(row);
      }
    }
  }

  private void addColumnToTable() {
    int column;
    if (table.getColumnSelectionAllowed() && !table.getRowSelectionAllowed()) {
      column = table.getSelectedColumn();
      if (column == -1) {
        column = tableModel.getColumnCount() - 1;
      }
    } else {
      column = tableModel.getColumnCount()-1;
    }
    String columnHeader = tableModel.appendColumn();
    TableColumn tableColumn = new TableColumn(tableModel.getColumnCount() - 1);
    tableColumn.setHeaderValue(columnHeader);
    tableColumn.setPreferredWidth(columnWidth);
    table.addColumn(tableColumn);
    table.moveColumn(tableModel.getColumnCount() - 1, column + 1);
  }

  private void addRowToTable() {
    tableModel.appendRow();
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

  public void setRowHeight(int rowHeight) {
    this.rowHeight = rowHeight;
    updateRowHeight();
  }

  public void setColumnWidth(int columnWidth) {
    this.columnWidth = columnWidth;
  }

  private class TablePopupMenu extends JPopupMenu {
    private JMenuItem newColumnItem;
    private JMenuItem newRowItem;

    public TablePopupMenu() {
      newColumnItem = new JMenuItem("New column");
      newRowItem = new JMenuItem("New row");
      add(newColumnItem);
      add(newRowItem);
      newColumnItem.addActionListener((e) -> addColumnToTable());
      newRowItem.addActionListener((e) -> addRowToTable());
    }
  }
}

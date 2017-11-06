package table_manager;

import javax.swing.table.AbstractTableModel;

public class ConcreteTableModel extends AbstractTableModel {
  private static final int ALPHABET_SIZE = 26;

  private static final int DEFAULT_ROW_COUNT = 40;
  private static final int DEFAULT_COLUMN_COUNT = 20;

  private String[] columnNames;
  private String[][] expressions;
  private Object[][] data;
  private int columnCount = DEFAULT_COLUMN_COUNT;
  private int rowCount = DEFAULT_ROW_COUNT;


  public ConcreteTableModel() {
    init();
  }

  public ConcreteTableModel(int rowCount, int columnCount) {
    this.rowCount = rowCount;
    this.columnCount = columnCount;
    init();
  }

  public ConcreteTableModel(int rowCount, int columnCount, String[] columnNames, Object[][] data, String[][] expressions){
    this.rowCount = rowCount;
    this.columnCount = columnCount;
    this.columnNames = columnNames;
    this.data = data;
    this.expressions = expressions;
  }

  private void init() {
    expressions = new String[rowCount][columnCount];
    data = new Object[rowCount][columnCount];
    generateColumnNames();
    generateIndexation();
  }

  private void generateColumnNames() {
    columnNames = new String[columnCount - 1];
    for (int i = 0; i < columnCount - 1; i++) {
      columnNames[i] = getNameForColumn(i);
    }
  }

  private String getNameForColumn(int column) {
    if (column == 0) {
      return "A";
    }
    String result = "";
    while (column > 0) {
      int residue = column % ALPHABET_SIZE;
      column /= ALPHABET_SIZE;
      result += Character.toString((char) (65 + residue));
    }
    return result;
  }

  private void generateIndexation() {
    for (int i = 0; i < rowCount; i++) {
      data[i][0] = String.valueOf(i + 1);
    }
  }

  public String getExpression(int row, int column) {
    return expressions[row][column];
  }

  public void setExpression(String expression, int row, int column) {
    expressions[row][column] = expression;
  }

  public Object[][] getValues() {
    return data;
  }

  public String[][] getExpressions(){
    return expressions;
  }

  public String[] getColumnNames(){
    return columnNames;
  }

  @Override
  public String getColumnName(int column) {
    if (column == 0) {
      return "#";
    } else {
      return columnNames[column - 1];
    }
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return column > 0;
  }

  @Override
  public int getRowCount() {
    return rowCount;
  }

  @Override
  public int getColumnCount() {
    return columnCount;
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    return data[rowIndex][columnIndex];
  }

  @Override
  public void setValueAt(Object value, int row, int column) {
    data[row][column] = value;
    fireTableCellUpdated(row, column);
  }
}

package table_manager;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collections;

public class ConcreteTableModel extends AbstractTableModel {
  private static final int ALPHABET_SIZE = 26;

  private static final int DEFAULT_ROW_COUNT = 40;
  private static final int DEFAULT_COLUMN_COUNT = 20;

  private ArrayList<String> columnNames;
  private ArrayList<ArrayList<String>> expressions;
  private ArrayList<ArrayList<String>> data;
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

  public ConcreteTableModel(
      int rowCount,
      int columnCount,
      ArrayList<String> columnNames,
      ArrayList<ArrayList<String>> data,
      ArrayList<ArrayList<String>> expressions) {
    this.rowCount = rowCount;
    this.columnCount = columnCount;
    this.columnNames = columnNames;
    this.data = data;
    this.expressions = expressions;
  }

  private void init() {
    data = new ArrayList<>(rowCount);
    expressions = new ArrayList<>(rowCount);
    for(int i = 0; i < rowCount; i++){
      data.add(new ArrayList<>(Collections.nCopies(columnCount, null)));
      expressions.add(new ArrayList<>(Collections.nCopies(columnCount, null)));
    }
    columnNames = new ArrayList<>(Collections.nCopies(columnCount, null));
    generateColumnNames();
    generateIndexation();
  }

  private void generateColumnNames() {
    for (int i = 0; i < columnCount; i++) {
      columnNames.set(i, getNameForColumn(i));
    }
  }

  private String getNameForColumn(int column) {
    if (column == 0) {
      return "#";
    }else if(column == 1){
      return "A";
    }
    --column;
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
      ArrayList<String> row = data.get(i);
      row.set(0, String.valueOf(i + 1));
    }
  }

  public void removeColumn(int column) {
    columnNames.remove(column);
    for(ArrayList<String> row : data){
      row.remove(column);
    }
    for(ArrayList<String> row : expressions){
      row.remove(column);
    }
    --columnCount;
    fireTableStructureChanged();
  }

  public void removeRow(int row) {
    data.remove(row);
    expressions.remove(row);
    --rowCount;
    fireTableRowsDeleted(row, row);
  }

  public ArrayList<String> getColumnNames() {
    return columnNames;
  }

  public ArrayList<ArrayList<String>> getValues() {
    return data;
  }

  public ArrayList<ArrayList<String>> getExpressions() {
    return expressions;
  }

  @Override
  public String getColumnName(int column) {
    return columnNames.get(column);
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
  public Object getValueAt(int row, int column) {
    return data.get(row).get(column);
  }

  @Override
  public void setValueAt(Object value, int row, int column) {
    data.get(row).set(column, value.toString());
    fireTableCellUpdated(row, column);
  }

  public String getExpression(int row, int column) {
    return expressions.get(row).get(column);
  }

  public void setExpression(String expression, int row, int column) {
    expressions.get(row).set(column, expression);
  }
}

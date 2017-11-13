package table_manager;

import auxiliary.EvaluationException;
import expression_analyses.BooleanComputer;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConcreteTableModel extends AbstractTableModel {
  private static final int ALPHABET_SIZE = 26;

  private static final int DEFAULT_ROW_COUNT = 40;
  private static final int DEFAULT_COLUMN_COUNT = 20;

  private int columnCreated = 0;
  private int rowCreated = 0;

  private ArrayList<String> columnNames;
  private ArrayList<String> rowNames;
  private ArrayList<ArrayList<String>> expressions;
  private ArrayList<ArrayList<String>> data;
  private HashMap<String, Integer> columnMap;
  private HashMap<String, Integer> rowMap;
  private int columnCount = DEFAULT_COLUMN_COUNT;
  private int rowCount = DEFAULT_ROW_COUNT;

  private boolean valueState = true;
  private boolean booleanState = true;

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
      int columnCreated,
      int rowCreated,
      ArrayList<String> columnNames,
      ArrayList<String> rowNames,
      HashMap<String, Integer> columnMap,
      HashMap<String, Integer> rowMap,
      ArrayList<ArrayList<String>> data,
      ArrayList<ArrayList<String>> expressions) {
    this.rowCount = rowCount;
    this.columnCount = columnCount;
    this.columnCreated = columnCreated;
    this.rowCreated = rowCreated;
    this.columnNames = columnNames;
    this.rowNames = rowNames;
    this.columnMap = columnMap;
    this.rowMap = rowMap;
    this.data = data;
    this.expressions = expressions;
  }

  public void switchState() {
    valueState = !valueState;
    fireTableDataChanged();
  }

  public void recalculateAll() throws EvaluationException {
    if (booleanState) {
      BooleanComputer computer = new BooleanComputer(data, expressions, columnMap, rowMap);
      for (int i = 0; i < rowCount; i++) {
        for (int j = 0; j < columnCount; j++) {
          if (!StringUtils.isBlank(expressions.get(i).get(j))) {
            computer.calculate(i, j);
          }
        }
      }
    }
    fireTableChanged(new TableModelEvent(this, 0, 0, 0, TableModelEvent.DELETE));
  }

  private boolean isBooleanEq(String s) {
    Pattern pattern = Pattern.compile("(&|&&|and|or|\\||\\|\\||!|not|false|true)");
    Matcher matcher = pattern.matcher(s);
    return matcher.find();
  }

  private String[][] toArray(ArrayList<ArrayList<String>> arr) {
    String[][] result = new String[arr.size()][];
    for (int i = 0; i < arr.size(); i++) {
      result[i] = (String[]) arr.get(i).toArray();
    }
    return result;
  }

  private void init() {
    columnNames = new ArrayList<>(Collections.nCopies(columnCount, null));
    rowNames = new ArrayList<>(Collections.nCopies(rowCount, null));
    data = new ArrayList<>(rowCount);
    expressions = new ArrayList<>(rowCount);
    columnMap = new HashMap<>();
    rowMap = new HashMap<>();
    for (int i = 0; i < rowCount; i++) {
      data.add(new ArrayList<>(Collections.nCopies(columnCount, null)));
      expressions.add(new ArrayList<>(Collections.nCopies(columnCount, null)));
    }
    generateColumnNames();
    generateIndexation();
  }

  private void generateColumnNames() {
    for (int i = 0; i < columnCount; i++) {
      String columnName = getNameForColumn(columnCreated);
      columnNames.set(i, columnName);
      columnMap.put(columnName, i);
      ++columnCreated;
    }
  }

  @NotNull
  public String getNameForColumn(int column) {
    if (column <= -1) {
      throw new IndexOutOfBoundsException("Negative index");
    }
    if (column == 0) {
      return "#";
    } else if (column == 1) {
      return "A";
    }
    --column;
    String result = "";
    while (column > 0) {
      int residue = column % ALPHABET_SIZE;
      column /= ALPHABET_SIZE;
      result = Character.toString((char) (65 + residue)) + result;
    }
    return result;
  }

  private void generateIndexation() {
    for (int i = 0; i < rowCount; i++) {
      ++rowCreated;
      String rowName = String.valueOf(rowCreated);
      rowNames.set(i, rowName);
      rowMap.put(rowName, i);
    }
  }

  public void removeColumn(int column) {
    columnNames.remove(column);
    for (int i = column; i < columnCount - 1; i++) {
      int index = columnMap.get(columnNames.get(i));
      columnMap.put(columnNames.get(i), index - 1);
    }
    for (ArrayList<String> row : data) {
      row.remove(column);
    }
    for (ArrayList<String> row : expressions) {
      row.remove(column);
    }
    --columnCount;
    fireTableStructureChanged();
  }

  public void removeRow(int row) {
    String rowName = rowNames.remove(row);
    rowMap.remove(rowName);
    for (int i = row; i < rowCount - 1; i++) {
      String rowN = rowNames.get(i);
      int index = rowMap.get(rowN);
      rowMap.put(rowN, index - 1);
    }
    data.remove(row);
    expressions.remove(row);
    --rowCount;
    fireTableRowsDeleted(row, row);
  }

  public void appendRow() {
    ArrayList<String> dataRow = new ArrayList<>(Collections.nCopies(columnCount, null));
    ArrayList<String> expressionRow = new ArrayList<>(Collections.nCopies(columnCount, null));
    String newRowName = getNextRowName();
    rowNames.add(newRowName);
    rowMap.put(newRowName, rowCount);
    data.add(dataRow);
    expressions.add(expressionRow);
    ++rowCount;
    fireTableRowsInserted(rowCount - 1, rowCount - 1);
  }

  public String appendColumn() {
    String newColumnName = getNextColumnName();
    columnNames.add(newColumnName);
    columnMap.put(newColumnName, columnCount);
    for (ArrayList<String> row : data) {
      row.add(null);
    }
    for (ArrayList<String> row : expressions) {
      row.add(null);
    }
    ++columnCount;
    fireTableStructureChanged();
    return newColumnName;
  }

  @NotNull
  private String getNextRowName() {
    return String.valueOf(++columnCreated);
  }

  @NotNull
  private String getNextColumnName() {
    return getNameForColumn(++columnCreated);
  }

  public ArrayList<String> getColumnNames() {
    return columnNames;
  }

  public ArrayList<String> getRowNames() {
    return rowNames;
  }

  public ArrayList<ArrayList<String>> getValues() {
    return data;
  }

  public ArrayList<ArrayList<String>> getExpressions() {
    return expressions;
  }

  public HashMap<String, Integer> getColumnMap() {
    return columnMap;
  }

  public HashMap<String, Integer> getRowMap() {
    return rowMap;
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
    if (valueState) {
      if (column == 0) {
        return rowNames.get(row);
      } else {
        return data.get(row).get(column);
      }
    } else {
      if (column == 0) {
        return rowNames.get(row);
      } else {
        return expressions.get(row).get(column);
      }
    }
  }

  @Override
  public void setValueAt(Object value, int row, int column) {
    if (valueState) {
      data.get(row).set(column, value.toString());
      fireTableCellUpdated(row, column);
    } else {
      expressions.get(row).set(column, value.toString());
      fireTableCellUpdated(row, column);
    }
  }

  public void switchBooleanState() {
    this.booleanState = !booleanState;
  }

  public String getExpression(int row, int column) {
    return expressions.get(row).get(column);
  }

  public void setExpression(String expression, int row, int column) {
    expressions.get(row).set(column, expression);
  }

  public int getColumnCreated() {
    return columnCreated;
  }

  public void setColumnCreated(int columnCreated) {
    this.columnCreated = columnCreated;
  }

  public int getRowCreated() {
    return rowCreated;
  }

  public void setRowCreated(int rowCreated) {
    this.rowCreated = rowCreated;
  }

  public boolean isValueState() {
    return valueState;
  }
}

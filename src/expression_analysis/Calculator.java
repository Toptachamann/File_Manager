package expression_analysis;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Calculator {
  protected ArrayList<ArrayList<String>> values;
  protected ArrayList<ArrayList<String>> expressions;
  protected HashMap<String, Integer> columnMap;
  protected HashMap<String, Integer> rowMap;
  protected AbstractLexicalAnalyzer analyzer;

  protected short[][] parent;
  protected byte[][] visited;

  public Calculator(
      ArrayList<ArrayList<String>> values,
      ArrayList<ArrayList<String>> expressions,
      HashMap<String, Integer> columnMap,
      HashMap<String, Integer> rowMap,
      AbstractLexicalAnalyzer analyzer) {
    this.values = values;
    this.expressions = expressions;
    this.columnMap = columnMap;
    this.rowMap = rowMap;
    this.analyzer = analyzer;
  }

  public void calculateAll() {
    int rowCount = values.size();
    if (rowCount == 0) {
      return;
    }
    int columnCount = values.get(0).size();
    if (columnCount == 0) {
      return;
    }
    initArrays(rowCount, columnCount);
    for (int i = 0; i < rowCount; i++) {
      for (int j = 0; j < columnCount; j++) {
        calculate(i, j);
      }
    }
  }

  public CellResult calculate(int row, int column) {
    if (visited[row][column] == 1) {
      values.get(row).set(column, "##Cycle##");
      return new CellResult("##Cycle##");
    } else if (visited[row][column] == 0) {
      String currentExpression = expressions.get(row).get(column);
      if (!StringUtils.isBlank(currentExpression)) {
        visited[row][column] = 1;
        CellResult result = evaluateExpressionAt(row, column);
        setValueAt(row, column, result);
        visited[row][column] = 2;
        return result;
      } else {
        visited[row][column] = 2;
        return evaluateValueAt(row, column);
      }
    } else {
      return evaluateValueAt(row, column);
    }
  }

  public CellResult evaluateRef(String ref) {
    Pattern pattern = Pattern.compile("^\\s*\\[(.+):(.*)\\]\\s*$");
    Matcher matcher = pattern.matcher(ref);
    if (matcher.matches()) {
      String strX = matcher.group(1);
      String strY = matcher.group(2);
      if (!(rowMap.containsKey(strY) && columnMap.containsKey(strX))) {
        return new CellResult("##Invalid reference##");
      }
      int row = rowMap.get(strY);
      int column = columnMap.get(strX);
      String expression = expressions.get(row).get(column);
      if (StringUtils.isBlank(expression)) {
        return evaluateValueAt(row, column);
      } else {
        return calculate(row, column);
      }
    } else {
      return new CellResult("##Invalid reference##");
    }
  }

  public abstract CellResult evaluateExpressionAt(int row, int column);

  public abstract CellResult evaluateValueAt(int row, int column);

  public abstract void setValueAt(int row, int column, CellResult result);

  public AbstractLexicalAnalyzer getAnalyzer() {
    return analyzer;
  }

  private void initArrays(int rowCount, int columnCount) {
    parent = new short[rowCount][columnCount];
    visited = new byte[rowCount][columnCount];
    for (int i = 0; i < rowCount; i++) {
      Arrays.fill(parent[i], (short) -1);
      Arrays.fill(visited[i], (byte) 0);
    }
  }

  public class CellResult {

    Object result;
    private String cause;
    private boolean invalid;

    public Object getResult() {
      return result;
    }

    public CellResult(Object result) {
      this.result = result;
      this.invalid = false;
    }

    public CellResult(String cause) {
      this.invalid = true;
      this.cause = cause;
    }

    public boolean isInvalid() {
      return this.invalid;
    }

    public String getCause() {
      return cause;
    }

    public void setInvalid(String cause) {
      this.invalid = true;
      this.cause = cause;

    }
  }
}

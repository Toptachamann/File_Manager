package expression_analyses;

import auxiliary.EvaluationException;
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
        try{
          calculate(i, j);
        } catch(EvaluationException ex){
          if(ex.getMessage().equals("Cycle")){
            values.get(i).set(j, "##Cycle##");
          }else{
            values.get(i).set(j, "##Invalid##");
          }
        }
      }
    }
  }

  private void initArrays(int rowCount, int columnCount){
    parent = new short[rowCount][columnCount];
    visited = new byte[rowCount][columnCount];
    for (int i = 0; i < rowCount; i++) {
      Arrays.fill(parent[i], (short) -1);
      Arrays.fill(visited[i], (byte) 0);
    }
  }

  public void calculate(int row, int column) throws EvaluationException {
    if(visited[row][column] == 1){
      throw new EvaluationException("Cycle");
    }else if(visited[row][column] == 0){
      String currentExpression = expressions.get(row).get(column);
      if (!StringUtils.isBlank(currentExpression)){
        visited[row][column] = 1;
        try{
          evaluateExpression(currentExpression, row, column);
          visited[row][column] = 2;
        } catch(EvaluationException ex){
          if(ex.getMessage().equals("Cycle")){
            values.get(row).set(column, "##Cycle##");
          }else{
            values.get(row).set(column, "##Invalid##");
          }
          throw ex;
        }
      }
    }
  }

  public abstract void evaluateExpression(String expression, int row, int column) throws EvaluationException;

  public String evaluateRef(String ref) throws EvaluationException {
    Pattern pattern = Pattern.compile("^\\s*\\[(.+):(.*)\\]\\s*$");
    Matcher matcher = pattern.matcher(ref);
    if(matcher.matches()){
      String strX = matcher.group(1);
      String strY = matcher.group(2);
      if(!(rowMap.containsKey(strY) && columnMap.containsKey(strX))){
        throw new EvaluationException("Invalid reference " + ref);
      }
      int row = rowMap.get(strY);
      int column = columnMap.get(strX);
      String expression = expressions.get(row).get(column);
      String value = values.get(row).get(column);
      if(StringUtils.isBlank(expression)){
        return value;
      }else{
        calculate(row, column);
        return values.get(row).get(column);
      }
    }else{
      throw new EvaluationException("Invalid reference " + ref);
    }
  }
}

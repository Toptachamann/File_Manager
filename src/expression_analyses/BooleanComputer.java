package expression_analyses;

import auxiliary.EvaluationException;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BooleanComputer {
  private ArrayList<ArrayList<String>> values;
  private ArrayList<ArrayList<String>> expressions;
  private HashMap<String, Integer> columnMap;
  private HashMap<String, Integer> rowMap;
  private byte[][] visited;
  LexicalAnalyzer analyzer;

  public BooleanComputer(){
    this.analyzer = new LexicalAnalyzer();
  }

  public BooleanComputer(
      ArrayList<ArrayList<String>> values,
      ArrayList<ArrayList<String>> expressions,
      HashMap<String, Integer> columnMap,
      HashMap<String, Integer> rowMap) {
    this.values = values;
    this.expressions = expressions;
    this.columnMap = columnMap;
    this.rowMap = rowMap;
    this.visited = new byte[values.size()][values.get(0).size()];
    for (int i = 0; i < visited.length; i++) {
      Arrays.fill(visited[i], (byte) 0);
    }
    this.analyzer = new LexicalAnalyzer();
  }

  public void calculate(int row, int column) throws EvaluationException {
    if(visited[row][column] == 1){
      throw new EvaluationException("Cycle in expression at [" + row + ":" + column + "]");
    }else if(visited[row][column] == 2){
      return;
    }else{
      if (!StringUtils.isBlank(expressions.get(row).get(column))){
        visited[row][column] = 1;
        boolean result = evaluateExpression(expressions.get(row).get(column));
        visited[row][column] = 2;
        values.get(row).set(column, String.valueOf(result));
      }
    }
  }

  public boolean evaluateExpression(String expression) throws EvaluationException {
    Node root = analyzer.buildTree(expression);
    return evaluateTree(root);
  }

  public boolean evaluateRef(String ref) throws EvaluationException {
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
        return Boolean.valueOf(value);
      }else{
        calculate(row, column);
        return Boolean.valueOf(values.get(row).get(column));
      }
    }else{
      throw new EvaluationException("Invalid reference " + ref);
    }
  }

  public boolean evaluateTree(Node currentNode) throws EvaluationException {
    switch (currentNode.token.type) {
      case OR:
        {
          boolean lRes = evaluateTree(currentNode.left);
          boolean rRes = evaluateTree(currentNode.right);
          return lRes | rRes;
        }
      case AND:
        {
          boolean lRes = evaluateTree(currentNode.left);
          boolean rRes = evaluateTree(currentNode.right);
          return lRes & rRes;
        }
      case NOT:
        {
          boolean lRes = evaluateTree(currentNode.left);
          return !lRes;
        }
      case REF:
        {
          boolean refResult = evaluateRef(currentNode.token.strToken);
          return refResult;
        }
      case TRUE:
        {
          return true;
        }
      case FALSE:
        {
          return false;
        }
      case LEFT_PAREN:
        {
          boolean result = evaluateTree(currentNode.left);
          return result;
        }
      default:
        throw new EvaluationException("Invalid token type");
    }
  }
}

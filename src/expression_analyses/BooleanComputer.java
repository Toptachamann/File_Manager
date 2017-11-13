package expression_analyses;

import auxiliary.EvaluationException;

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
  private boolean[][] visited;
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
    this.visited = new boolean[values.size()][values.get(0).size()];
    for (int i = 0; i < visited.length; i++) {
      Arrays.fill(visited[i], Boolean.FALSE);
    }
    this.analyzer = new LexicalAnalyzer();
  }

  public ArrayList<ArrayList<String>> compute(int x, int y) throws EvaluationException {
    evaluate(x, y);
    return values;
  }

  public boolean evaluate(int x, int y) throws EvaluationException {
    if(visited[x][y]){
      throw new EvaluationException("Cycle in expression");
    }
    visited[x][y] = true;
    boolean result = evaluateExpression(expressions.get(x).get(y));
    values.get(x).set(y, String.valueOf(result));
    return result;
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
      int x = columnMap.get(strX);
      int y = rowMap.get(strY);
      return evaluate(x, y);
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

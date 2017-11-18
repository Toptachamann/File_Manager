package expression_analysis;

import auxiliary.EvaluationException;

import java.util.ArrayList;
import java.util.HashMap;

public class BooleanCalculator extends Calculator {

  public BooleanCalculator(
      ArrayList<ArrayList<String>> values,
      ArrayList<ArrayList<String>> expressions,
      HashMap<String, Integer> columnMap,
      HashMap<String, Integer> rowMap,
      AbstractLexicalAnalyzer analyzer) {
    super(values, expressions, columnMap, rowMap, analyzer);
  }
  @Override
  public void evaluateExpression(String expression, int row, int column) throws EvaluationException {
    Node root = analyzer.buildTree(expression);
    boolean result = evaluate(root);
    values.get(row).set(column, String.valueOf(result));
  }

  public boolean evaluate(Node currentNode) throws EvaluationException {
    switch (currentNode.token.type) {
      case OR:
        {
          boolean lRes = evaluate(currentNode.left);
          boolean rRes = evaluate(currentNode.right);
          return lRes | rRes;
        }
      case AND:
        {
          boolean lRes = evaluate(currentNode.left);
          boolean rRes = evaluate(currentNode.right);
          return lRes & rRes;
        }
      case NOT:
        {
          boolean lRes = evaluate(currentNode.left);
          return !lRes;
        }
      case REF:
        {
          String result = evaluateRef(currentNode.token.strToken);
          return Boolean.valueOf(result);
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
          return evaluate(currentNode.left);
        }
      default:
        throw new EvaluationException("Invalid token type");
    }
  }
}

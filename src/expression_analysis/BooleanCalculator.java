package expression_analysis;

import auxiliary.EvaluationException;
import org.apache.commons.lang3.StringUtils;

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
  public CellResult evaluateExpressionAt(int row, int column) {
    String expression = expressions.get(row).get(column);
    try {
      Node root = analyzer.buildTree(expression);
      return evaluate(root);
    } catch (EvaluationException ex) {
      return new CellResult(ex.getMessage());
    }
  }

  @Override
  public CellResult evaluateValueAt(int row, int column) {
    String value = values.get(row).get(column);
    if (value == null) {
      return new CellResult("Cell is not initialized");
    } else if (value.equalsIgnoreCase("true")) {
      return new CellResult(true);
    } else if (value.equalsIgnoreCase("false")) {
      return new CellResult(false);
    } else if (StringUtils.isBlank(value)) {
      return new CellResult("Cell is blank");
    } else {
      return new CellResult(value);
    }
  }

  @Override
  public void setValueAt(int row, int column, CellResult result) {
    if(result.isInvalid()){
      values.get(row).set(column, result.getCause());
    }else{
      values.get(row).set(column, result.result.toString());
    }
  }

  public CellResult evaluate(Node currentNode) {
    switch (currentNode.token.type) {
      case AND:
      case OR:
        {
          CellResult leftResult = evaluate(currentNode.left);
          CellResult rightResult = evaluate(currentNode.right);
          if (leftResult.isInvalid()) {
            return leftResult;
          } else if (rightResult.isInvalid()) {
            return rightResult;
          } else {
            boolean left = (Boolean) leftResult.result;
            boolean right = (Boolean) rightResult.result;
            if (currentNode.token.type == TokenType.AND) {
              leftResult.result = left & right;
            } else {
              leftResult.result = left | right;
            }
            return leftResult;
          }
        }
      case NOT:
        {
          CellResult leftResult = evaluate(currentNode.left);
          if (leftResult.isInvalid()) {
            return leftResult;
          } else {
            leftResult.result = !((Boolean) leftResult.result);
            return leftResult;
          }
        }
      case REF:
        {
          return evaluateRef(currentNode.token.strToken);
        }
      case TRUE:
        {
          return new CellResult(Boolean.TRUE);
        }
      case FALSE:
        {
          return new CellResult(Boolean.FALSE);
        }
      case LEFT_PAREN:
        {
          return evaluate(currentNode.left);
        }
      default:
        return new CellResult("Invalid token type");
    }
  }
}

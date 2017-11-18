package expression_analysis;

import auxiliary.EvaluationException;
import org.nevec.rjm.BigDecimalMath;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;

public class ArithmeticCalculator extends Calculator {
  public static MathContext defaultRounder = new MathContext(4, RoundingMode.HALF_UP);
  private MathContext rounder;

  public ArithmeticCalculator(
      ArrayList<ArrayList<String>> values,
      ArrayList<ArrayList<String>> expressions,
      HashMap<String, Integer> columnMap,
      HashMap<String, Integer> rowMap,
      AbstractLexicalAnalyzer analyzer) {
    super(values, expressions, columnMap, rowMap, analyzer);
    this.rounder = defaultRounder;
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
    } else {
      try {
        BigDecimal result = new BigDecimal(value);
        return new CellResult(result);
      } catch (NumberFormatException e) {
        return new CellResult(value);
      }
    }
  }

  @Override
  public void setValueAt(int row, int column, CellResult result) {
    if(result.isInvalid()){
      values.get(row).set(column, result.getCause());
    }else{
      BigDecimal res = (BigDecimal) result.result;
      values.get(row).set(column, res.toString());
    }
  }

  public CellResult evaluate(Node node) {
    switch (node.token.type) {
      case MINUS:
      case PLUS:
        {
          CellResult leftResult = evaluate(node.left);
          CellResult rightResult = evaluate(node.right);
          if (leftResult.isInvalid()) {
            return leftResult;
          } else if (rightResult.isInvalid()) {
            return rightResult;
          } else {
            BigDecimal left = (BigDecimal) leftResult.result;
            BigDecimal right = (BigDecimal) rightResult.result;
            if (node.token.type == TokenType.MINUS) {
              leftResult.result = left.subtract(right, rounder);
            } else {
              leftResult.result = left.add(right, rounder);
            }
            return leftResult;
          }
        }
      case MULTIPLY:
      case DIV:
      case INT_DIV:
      case MOD:
        {
          CellResult leftResult = evaluate(node.left);
          CellResult rightResult = evaluate(node.right);
          if (leftResult.isInvalid()) {
            return leftResult;
          } else if (rightResult.isInvalid()) {
            return rightResult;
          } else {
            BigDecimal left = (BigDecimal) leftResult.result;
            BigDecimal right = (BigDecimal) rightResult.result;
            if (node.token.type == TokenType.MULTIPLY) {
              leftResult.result = left.multiply(right, rounder);
            } else if (node.token.type == TokenType.DIV) {
              if (right.compareTo(BigDecimal.ZERO) == 0) {
                leftResult.setInvalid("Division by zero");
              } else {
                leftResult.result = left.divide(right, rounder);
              }
            } else if (node.token.type == TokenType.INT_DIV) {
              if (right.compareTo(BigDecimal.ZERO) == 0) {
                leftResult.setInvalid("Integer division by zero");
              } else {
                leftResult.result = left.divideToIntegralValue(right);
              }
            } else {
              if (right.compareTo(BigDecimal.ZERO) == 0) {
                leftResult.setInvalid("Modulo is zero");
              } else {
                leftResult.result = left.remainder(right);
              }
            }
            return leftResult;
          }
        }
      case EXP:
        {
          CellResult leftResult = evaluate(node.left);
          CellResult rightResult = evaluate(node.right);
          if (leftResult.isInvalid()) {
            return leftResult;
          } else if (rightResult.isInvalid()) {
            return rightResult;
          } else {
            BigDecimal left = (BigDecimal) leftResult.result;
            BigDecimal right = (BigDecimal) rightResult.result;
            leftResult.result = BigDecimalMath.pow(left, right);
            return leftResult;
          }
        }
      case NUMBER:
        {
          return new CellResult(new BigDecimal(node.token.strToken));
        }
      case LEFT_PAREN:
        {
          return evaluate(node.left);
        }
      case REF:
        {
          String reference = node.token.strToken;
          return evaluateRef(reference);
        }
      default:
        {
          return new CellResult("Invalid token type");
        }
    }
  }

  public void setRounder(MathContext rounder) {
    this.rounder = rounder;
  }
}

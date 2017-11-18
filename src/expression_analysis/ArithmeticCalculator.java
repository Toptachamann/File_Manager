package expression_analysis;

import auxiliary.EvaluationException;

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
  public void evaluateExpression(String expression, int row, int column) throws EvaluationException {
    Node root = analyzer.buildTree(expression);
    String result = evaluate(root).toPlainString();
    values.get(row).set(column, result);
  }

  public BigDecimal evaluate(Node node) throws EvaluationException {
    switch (node.token.type) {
      case PLUS:
        {
          BigDecimal left = evaluate(node.left);
          BigDecimal right = evaluate(node.right);
          return left.add(right, rounder);
        }
      case MINUS:
        {
          if (node.right.equals(Node.EMPTY_NODE)) {
            return evaluate(node.left).negate();
          } else {
            BigDecimal left = evaluate(node.left);
            BigDecimal right = evaluate(node.right);
            return left.subtract(right, rounder);
          }
        }
      case MULTIPLY:
        {
          BigDecimal left = evaluate(node.left);
          BigDecimal right = evaluate(node.right);
          return left.multiply(right, rounder);
        }
      case DIV:
        {
          BigDecimal left = evaluate(node.left);
          BigDecimal right = evaluate(node.right);
          if (right.compareTo(BigDecimal.ZERO) == 0) {
            throw new EvaluationException("Division by zero");
          } else {
            return left.divide(right, rounder);
          }
        }
      case INT_DIV:
        {
          BigDecimal left = evaluate(node.left);
          BigDecimal right = evaluate(node.right);
          if (right.compareTo(BigDecimal.ZERO) == 0) {
            throw new EvaluationException("Division by zero");
          } else {
            return left.divideToIntegralValue(right, rounder);
          }
        }
      case MOD:
        {
          BigDecimal left = evaluate(node.left);
          BigDecimal right = evaluate(node.right);
          if (right.compareTo(BigDecimal.ZERO) == 0) {
            throw new EvaluationException("Division by zero");
          } else {
            return left.remainder(right, rounder);
          }
        }
      case EXP:
        {
          BigDecimal left = evaluate(node.left);
          BigDecimal right = evaluate(node.right);
          org.nevec.rjm.BigDecimalMath.pow(left, right);
        }
      case NUMBER:
        {
          return new BigDecimal(node.token.strToken);
        }
      case LEFT_PAREN:
        {
          return evaluate(node.left);
        }
      case REF:
        {
          String result = evaluateRef(node.token.toString());
          return new BigDecimal(result);
        }
      default:
        {
          throw new EvaluationException("Invalid node token " + node.token);
        }
    }
  }

  public void setRounder(MathContext rounder) {
    this.rounder = rounder;
  }
}

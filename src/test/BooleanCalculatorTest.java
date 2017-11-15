package test;

import auxiliary.EvaluationException;
import expression_analyses.BooleanCalculator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BooleanCalculatorTest {
  BooleanCalculator computer;

  public BooleanCalculatorTest() {
    computer = new BooleanCalculator();
  }

  @Test
  @DisplayName("Basic operations")
  public void expressionEvaluationTest1() throws EvaluationException {
    assertEquals(true, computer.evaluateExpression("true"));
    assertEquals(false, computer.evaluateExpression("false"));
    assertEquals(false, computer.evaluateExpression("!true"));
    assertEquals(true, computer.evaluateExpression("!false"));
    assertEquals(false, computer.evaluateExpression("true&&false"));
    assertEquals(false, computer.evaluateExpression("true&false"));
    assertEquals(true, computer.evaluateExpression("true||false"));
    assertEquals(true, computer.evaluateExpression("true|false"));
    assertEquals(true, computer.evaluateExpression("true or false"));
  }

  @Test
  @DisplayName("Complex expressions")
  public void expressionEvaluationTest2() throws EvaluationException {
    assertEquals(true, computer.evaluateExpression("!(false||true&&false)||true"));
    assertEquals(true, computer.evaluateExpression("true && (false or true) and true"));
    assertEquals(false, computer.evaluateExpression("(((true|false)&true)&!true)"));
  }
}

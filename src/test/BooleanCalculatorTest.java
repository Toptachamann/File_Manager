package test;

import auxiliary.EvaluationException;
import expression_analysis.AbstractLexicalAnalyzer;
import expression_analysis.BooleanCalculator;
import expression_analysis.BooleanLexicalAnalyzer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.AbstractAction;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BooleanCalculatorTest {
  BooleanCalculator computer;

  public BooleanCalculatorTest() {
    computer = new BooleanCalculator(null, null, null, null, new BooleanLexicalAnalyzer());
  }

  @Test
  @DisplayName("Basic operations")
  public void expressionEvaluationTest1() throws EvaluationException {
    AbstractLexicalAnalyzer analyzer = computer.getAnalyzer();
    assertEquals(true, computer.evaluate(analyzer.buildTree("true")));
    assertEquals(false, computer.evaluate(analyzer.buildTree("false")));
    assertEquals(false, computer.evaluate(analyzer.buildTree("!true")));
    assertEquals(true, computer.evaluate(analyzer.buildTree("!false")));
    assertEquals(false, computer.evaluate(analyzer.buildTree("true&&false")));
    assertEquals(false, computer.evaluate(analyzer.buildTree("true&false")));
    assertEquals(true, computer.evaluate(analyzer.buildTree("true||false")));
    assertEquals(true, computer.evaluate(analyzer.buildTree("true|false")));
    assertEquals(true, computer.evaluate(analyzer.buildTree("true or false")));
  }

  @Test
  @DisplayName("Complex expressions")
  public void expressionEvaluationTest2() throws EvaluationException {
    AbstractLexicalAnalyzer analyzer = computer.getAnalyzer();
    assertEquals(true, computer.evaluate(analyzer.buildTree("!(false||true&&false)||true")));
    assertEquals(true, computer.evaluate(analyzer.buildTree("true && (false or true) and true")));
    assertEquals(false, computer.evaluate(analyzer.buildTree("(((true|false)&true)&!true)")));
  }
}

package test;

import auxiliary.EvaluationException;
import expression_analysis.ArithmeticLexicalAnalyzer;
import expression_analysis.Node;
import expression_analysis.Token;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static expression_analysis.Node.EMPTY_NODE;
import static expression_analysis.Token.EXPONENT_TOKEN;
import static expression_analysis.Token.MINUS_TOKEN;
import static expression_analysis.Token.MULT_TOKEN;
import static expression_analysis.Token.PLUS_TOKEN;
import static expression_analysis.TokenType.NUMBER;
import static expression_analysis.TokenType.REF;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArithmeticLexicalAnalyzerTest {
  private ArithmeticLexicalAnalyzer analyzer;

  public ArithmeticLexicalAnalyzerTest() {
    analyzer = new ArithmeticLexicalAnalyzer();
  }

  @Test
  @DisplayName("Number distinction")
  public void distinctToken() throws EvaluationException {
    assertEquals(new Node(new Token(NUMBER, "1000")), analyzer.buildTree("1000"));
    assertEquals(
        new Node(new Token(NUMBER, "1000.10001")), analyzer.buildTree("1000.10001"));
    assertEquals(
        new Node(new Token(NUMBER, "1234567890.1234567890")),
        analyzer.buildTree("1234567890.1234567890"));
  }

  @Test
  @DisplayName("Tree building 1")
  public void treeBuilding1() throws EvaluationException {
    Node firstPlus = new Node(PLUS_TOKEN, new Node(new Token(NUMBER, "2")), new Node(new Token(NUMBER, "3")));
    Node secondPlus = new Node(PLUS_TOKEN, new Node(new Token(NUMBER, "1")), firstPlus);
    assertEquals(secondPlus, analyzer.buildTree("1+2+3"));
  }

  @Test
  @DisplayName("Tree building 2")
  public void treeBuilding2() throws EvaluationException {
    Node expNode = new Node(EXPONENT_TOKEN, new Node(new Token(NUMBER, "2")), new Node(new Token(NUMBER, "3")));
    Node multNode = new Node(Token.MULT_TOKEN, new Node(new Token(NUMBER, "1")), expNode);
    assertEquals(multNode, analyzer.buildTree("1*2^3"));
  }

  @Test
  @DisplayName("Tree building 3")
  public void treeBuilding3() throws EvaluationException {
    Node unaryMinus = new Node(MINUS_TOKEN, new Node(new Token(NUMBER, "3")), EMPTY_NODE);
    Node exp = new Node(EXPONENT_TOKEN, unaryMinus, new Node(new Token(NUMBER, "5")));
    Node mult = new Node(MULT_TOKEN, exp, new Node(new Token(NUMBER, "4")));
    Node minus = new Node(MINUS_TOKEN, mult, new Node(new Token(REF, "[A:1]")));
    assertEquals(minus, analyzer.buildTree("-+3^5*4-[A:1]"));
  }

}

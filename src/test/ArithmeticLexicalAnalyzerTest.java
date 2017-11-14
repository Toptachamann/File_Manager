package test;

import auxiliary.EvaluationException;
import expression_analyses.ArithmeticLexicalAnalyzer;
import expression_analyses.Node;
import expression_analyses.Token;
import expression_analyses.TokenType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ArithmeticLexicalAnalyzerTest {
  private ArithmeticLexicalAnalyzer analyzer;

  public ArithmeticLexicalAnalyzerTest() {
    analyzer = new ArithmeticLexicalAnalyzer();
  }

  @Test
  @DisplayName("Number distinction")
  public void distinctToken() throws EvaluationException {
    assertEquals(new Node(new Token(TokenType.NUMBER, "1000")), analyzer.buildTree("1000"));
    assertEquals(new Node(new Token(TokenType.NUMBER, "1000.10001")), analyzer.buildTree("1000.10001"));
  }
}

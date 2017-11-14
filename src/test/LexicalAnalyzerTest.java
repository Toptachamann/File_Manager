package test;

import auxiliary.EvaluationException;
import expression_analyses.LexicalAnalyzer;
import expression_analyses.Node;
import expression_analyses.Token;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static expression_analyses.Node.EMPTY_NODE;
import static expression_analyses.Node.FALSE_NODE;
import static expression_analyses.Node.TRUE_NODE;
import static expression_analyses.Token.AND_TOKEN;
import static expression_analyses.Token.LEFT_PAREN_TOKEN;
import static expression_analyses.Token.NOT_TOKEN;
import static expression_analyses.Token.OR_TOKEN;
import static expression_analyses.Token.TRUE_TOKEN;
import static expression_analyses.TokenType.AND;
import static expression_analyses.TokenType.REF;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Tests for lexical analyzer")
public class LexicalAnalyzerTest {
  private static LexicalAnalyzer analyzer;


  public LexicalAnalyzerTest(){
    analyzer = new LexicalAnalyzer();
  }

  @Test
  @DisplayName("Reference")
  public void reference() throws EvaluationException {
    Node reference = new Node(new Token(REF, "[A:1]"));
    assertEquals(reference, analyzer.buildTree("[A:1]"));
  }

  @Test
  @DisplayName("Simple test")
  public void simpleTest() throws EvaluationException {
    assertEquals(
        new Node(TRUE_TOKEN, EMPTY_NODE, EMPTY_NODE), analyzer.buildTree("true"));
    assertEquals(
        new Node(AND_TOKEN, TRUE_NODE, FALSE_NODE),
        analyzer.buildTree("true&false"));
    Node firstRef = new Node(new Token(REF, "[B:1]"), EMPTY_NODE, EMPTY_NODE);
    Node secondRef = new Node(new Token(REF, "[A:1]"), EMPTY_NODE, EMPTY_NODE);
    Node node = new Node(new Token(AND), firstRef, secondRef);
    assertEquals(node, analyzer.buildTree("[B:1]  && [A:1]"));
  }

  @Test
  @DisplayName("Exception test")
  public void exceptionsTest() {
    Throwable throwable1 =
        assertThrows(
            EvaluationException.class,
            () -> {
              analyzer.buildTree("&");
            });
    assertEquals("Invalid token and at position 0", throwable1.getMessage());
    Throwable throwable2 = assertThrows(EvaluationException.class, ()->{
      analyzer.buildTree("[A:1]&&");
    });
    assertEquals("Invalid token and at position 5", throwable2.getMessage());
    Throwable throwable3 = assertThrows(EvaluationException.class, ()->{
      analyzer.buildTree("[B:2] && ()");
    });
    assertEquals("Invalid token ) at position 10", throwable3.getMessage());
  }

  @Test
  @DisplayName("Parenthesis test")
  public void ParenTest() throws EvaluationException {
    Node orNode = new Node(OR_TOKEN, FALSE_NODE, TRUE_NODE);
    Node parenNode = new Node(LEFT_PAREN_TOKEN, orNode, EMPTY_NODE);
    Node secondAnd = new Node(AND_TOKEN, parenNode, TRUE_NODE);
    Node firstAnd = new Node(AND_TOKEN, TRUE_NODE, secondAnd);
    Node ans = analyzer.buildTree("true and (false or true) and true");
    assertEquals(firstAnd, ans);
  }

  @Test
  @DisplayName("Multiple parenthesis test")
  public void multipleParenthesis() throws EvaluationException {
    Node orNode = new Node(OR_TOKEN, TRUE_NODE, FALSE_NODE);
    Node firstParen = new Node(LEFT_PAREN_TOKEN, orNode, EMPTY_NODE);
    Node firstAndNode = new Node(AND_TOKEN, firstParen, TRUE_NODE);
    Node secondParen = new Node(LEFT_PAREN_TOKEN, firstAndNode, EMPTY_NODE);
    Node secondAndNode =
        new Node(
            AND_TOKEN,
            secondParen,
            new Node(NOT_TOKEN, FALSE_NODE, EMPTY_NODE));
    Node thirdParenNode = new Node(LEFT_PAREN_TOKEN, secondAndNode, EMPTY_NODE);
    Node ans = analyzer.buildTree("(((true|false)&true)&!false)");
    assertEquals(thirdParenNode, ans);
  }

  @Test
  @DisplayName("Reference test")
  public void referenceTest() throws EvaluationException {
    Node first = new Node(NOT_TOKEN, new Node(new Token(REF, "[C:1]")), EMPTY_NODE);
    Node second = new Node(AND_TOKEN, first, new Node(new Token(REF, "[K:300]")));
    Node third = new Node(OR_TOKEN, new Node(new Token(REF, "[B:2]")), second);
    Node fourth = new Node(LEFT_PAREN_TOKEN, third, EMPTY_NODE);
    Node fifth = new Node(NOT_TOKEN, fourth, EMPTY_NODE);
    Node sixth = new Node(NOT_TOKEN, new Node(new Token(REF, "[A:1]")), EMPTY_NODE);
    Node seventh = new Node(AND_TOKEN, sixth, fifth);
    Node ans = analyzer.buildTree("![A:1]&!([B:2]||![C:1]&&!![K:300])");
    assertEquals(seventh, ans);
  }
}

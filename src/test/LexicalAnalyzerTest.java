package test;

import auxiliary.EvaluationException;
import expression_analyses.LexicalAnalyzer;
import expression_analyses.Node;
import expression_analyses.Token;
import expression_analyses.TokenType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Tests for lexical analyzer")
public class LexicalAnalyzerTest {
  @Test
  @DisplayName("Simple test")
  public void simpleTest(){
    try{
      LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer();
      assertEquals(new Node(Token.TRUE_TOKEN, Node.EMPTY_NODE, Node.EMPTY_NODE), lexicalAnalyzer.buildTree("true"));
      assertEquals(new Node(Token.AND_TOKEN, Node.TRUE_NODE, Node.FALSE_NODE), lexicalAnalyzer.buildTree("true&false"));
      Node firstRef = new Node(new Token(TokenType.REF, "[B:1]"), Node.EMPTY_NODE, Node.EMPTY_NODE);
      Node secondRef = new Node(new Token(TokenType.REF, "[A:1]"), Node.EMPTY_NODE, Node.EMPTY_NODE);
      Node node = new Node(new Token(TokenType.AND), firstRef, secondRef);
      assertEquals(node, lexicalAnalyzer.buildTree("[B:1]  && [A:1]"));
    } catch (Exception e){
      e.printStackTrace();
    }
  }

  @Test
  @DisplayName("Exception test")
  public void exceptionsTest(){
    LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer();
    Throwable throwable = assertThrows(EvaluationException.class, ()->{lexicalAnalyzer.buildTree("&");});
    assertEquals("Invalid token and", throwable.getMessage());
  }
}

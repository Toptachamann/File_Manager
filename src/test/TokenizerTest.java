package test;

import auxiliary.EvaluationException;
import expression_analysis.AbstractTokenizer;
import expression_analysis.Token;
import expression_analysis.TokenType;
import expression_analysis.Tokenizer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Test for boolean tokenizer")
public class TokenizerTest {

  @Test
  @DisplayName("Basic token distinction")
  public void test() {
    String expression = "true false & && and | || or ! not ( ) [A:1] +-*/ //%";
    AbstractTokenizer tokenizer = new Tokenizer(expression);
    try {
      assertEquals(Token.TRUE_TOKEN, tokenizer.getToken());
      assertEquals(Token.FALSE_TOKEN, tokenizer.getToken());
      assertEquals(Token.AND_TOKEN, tokenizer.getToken());
      assertEquals(Token.AND_TOKEN, tokenizer.getToken());
      assertEquals(Token.AND_TOKEN, tokenizer.getToken());
      assertEquals(Token.OR_TOKEN, tokenizer.getToken());
      assertEquals(Token.OR_TOKEN, tokenizer.getToken());
      assertEquals(Token.OR_TOKEN, tokenizer.getToken());
      assertEquals(Token.NOT_TOKEN, tokenizer.getToken());
      assertEquals(Token.NOT_TOKEN, tokenizer.getToken());
      assertEquals(Token.LEFT_PAREN_TOKEN, tokenizer.getToken());
      assertEquals(Token.RIGHT_PAREN_TOKEN, tokenizer.getToken());
      assertEquals(new Token(TokenType.REF, "[A:1]"), tokenizer.getToken());
      assertEquals(Token.PLUS_TOKEN, tokenizer.getToken());
      assertEquals(Token.MINUS_TOKEN, tokenizer.getToken());
      assertEquals(Token.MULT_TOKEN, tokenizer.getToken());
      assertEquals(Token.DIV_TOKEN, tokenizer.getToken());
      assertEquals(Token.INT_DIV_TOKEN, tokenizer.getToken());
      assertEquals(Token.MOD_TOKEN, tokenizer.getToken());

    } catch (EvaluationException e) {
      e.printStackTrace();
    }
  }

  @Test
  @DisplayName("Number tokenizing")
  public void testNumbers() throws EvaluationException {
    Tokenizer tokenizer = new Tokenizer();
    assertEquals(new Token(TokenType.NUMBER, "100"), tokenizer.setExpression("100").getToken());
    assertEquals(
        new Token(TokenType.NUMBER, "1234567890"),
        tokenizer.setExpression("1234567890").getToken());
    assertEquals(
        new Token(TokenType.NUMBER, "1234567890.1234567890"),
        tokenizer.setExpression("1234567890.1234567890").getToken());
    Throwable throwable =
        assertThrows(EvaluationException.class, () -> tokenizer.setExpression("10.").getToken());
    assertEquals("Invalid syntax in 10. at position 2", throwable.getMessage());
  }
}

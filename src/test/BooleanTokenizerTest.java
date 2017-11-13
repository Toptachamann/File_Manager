package test;

import auxiliary.EvaluationException;
import expression_analyses.BooleanTokenizer;
import expression_analyses.Token;
import expression_analyses.TokenType;
import expression_analyses.Tokenizer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;



@DisplayName("Test for boolean tokenizer")
public class BooleanTokenizerTest {

  @Test
  @DisplayName("Basic token distinction")
  public void test() {
    String expression = "true false & && and | || or ! not ( ) [A:1]";
    Tokenizer tokenizer = new BooleanTokenizer(expression);
    try{
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


    } catch(EvaluationException e){
      e.printStackTrace();
    }
  }
}

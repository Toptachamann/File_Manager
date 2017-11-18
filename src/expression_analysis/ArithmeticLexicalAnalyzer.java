package expression_analysis;

import auxiliary.EvaluationException;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class ArithmeticLexicalAnalyzer implements AbstractLexicalAnalyzer {
  private int numOfOpenedParenthesis = 0;

  @Override
  public Node buildTree(String expression) throws EvaluationException {
    if (StringUtils.isBlank(expression)) {
      throw new EvaluationException("Expression is blank");
    }
    this.numOfOpenedParenthesis = 0;
    AbstractTokenizer tokenizer = new Tokenizer(expression);
    return buildAddendum(tokenizer);
  }
  @NotNull
  private Node buildAddendum(AbstractTokenizer tokenizer) throws EvaluationException {
    Node left = buildMultiplicand(tokenizer);
    Token token = tokenizer.getToken();
    switch (token.type) {
      case RIGHT_PAREN:
        {
          if (numOfOpenedParenthesis <= 0) {
            throw new EvaluationException("Parenthesis disbalance");
          }
          tokenizer.ungetToken();
          return left;
        }
      case PLUS:
      case MINUS:
        {
          Node right = buildAddendum(tokenizer);
          return new Node(token, left, right);
        }
      case EMPTY:
        {
          return left;
        }
      default:
        {
          tokenizer.ungetToken();
          throw new EvaluationException(
              "Invalid token " + token + " at position " + tokenizer.getCurrentPosition());
        }
    }
  }

  @NotNull
  private Node buildMultiplicand(AbstractTokenizer tokenizer) throws EvaluationException {
    Node left = buildExponent(tokenizer);
    Token token = tokenizer.getToken();
    switch (token.type) {
      case MULTIPLY:
      case DIV:
      case INT_DIV:
      case MOD:
        {
          Node right = buildMultiplicand(tokenizer);
          return new Node(token, left, right);
        }
      case PLUS:
      case MINUS:
        {
          tokenizer.ungetToken();
          return left;
        }
      case RIGHT_PAREN:
        {
          if (numOfOpenedParenthesis <= 0) {
            throw new EvaluationException("Parenthesis disbalance");
          }
          tokenizer.ungetToken();
          return left;
        }
      case EMPTY:
        {
          return left;
        }
      default:
        {
          tokenizer.ungetToken();
          throw new EvaluationException(
              "Invalid token " + token + " at position " + tokenizer.getCurrentPosition());
        }
    }
  }

  @NotNull
  private Node buildExponent(AbstractTokenizer tokenizer) throws EvaluationException {
    Node left = buildUnary(tokenizer);
    Token token = tokenizer.getToken();
    switch (token.type) {
      case PLUS:
      case MINUS:
      case MULTIPLY:
      case DIV:
      case INT_DIV:
      case MOD:
        {
          tokenizer.ungetToken();
          return left;
        }
      case EXP:
        {
          Node right = buildUnary(tokenizer);
          return new Node(token, left, right);
        }
      case RIGHT_PAREN:
        {
          if (numOfOpenedParenthesis <= 0) {
            throw new EvaluationException("Parenthesis disbalance");
          }
          tokenizer.ungetToken();
          return left;
        }
      case EMPTY:
        {
          return left;
        }
      default:
        {
          tokenizer.ungetToken();
          throw new EvaluationException(
              "Invalid token " + token + " at position " + tokenizer.getCurrentPosition());
        }
    }
  }

  @NotNull
  private Node buildUnary(AbstractTokenizer tokenizer) throws EvaluationException {
    Token token;
    int numOfMinus = 0;
    do {
      token = tokenizer.getToken();
      if (token.type == TokenType.MINUS) {
        ++numOfMinus;
      }
    } while (token.type == TokenType.MINUS || token.type == TokenType.PLUS);
    tokenizer.ungetToken();
    Node atom = buildAtom(tokenizer);
    if (numOfMinus % 2 == 1) {
      return new Node(Token.MINUS_TOKEN, atom, Node.EMPTY_NODE);
    } else {
      return atom;
    }
  }

  @NotNull
  private Node buildAtom(AbstractTokenizer tokenizer) throws EvaluationException {
    Token token = tokenizer.getToken();
    switch (token.type) {
      case REF:
        {
          return new Node(token);
        }
      case NUMBER:
        {
          return new Node(token);
        }
      case LEFT_PAREN:
        {
          ++numOfOpenedParenthesis;
          Node addendum = buildAddendum(tokenizer);
          Token rightParen = tokenizer.getToken();
          if (!rightParen.equals(Token.RIGHT_PAREN_TOKEN)) {
            throw new EvaluationException("Parenthesis disbalance");
          }
          --numOfOpenedParenthesis;
          return new Node(token, addendum, Node.EMPTY_NODE);
        }
      default:
        {
          tokenizer.ungetToken();
          throw new EvaluationException(
              "Invalid token " + token + " at position " + tokenizer.getCurrentPosition());
        }
    }
  }
}

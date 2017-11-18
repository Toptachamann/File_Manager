package expression_analysis;

import auxiliary.EvaluationException;
import org.jetbrains.annotations.NotNull;

public class BooleanLexicalAnalyzer implements AbstractLexicalAnalyzer {
  private int numOfOpenedParenthesis = 0;

  public Node buildTree(String expression) throws EvaluationException {
    this.numOfOpenedParenthesis = 0;
    AbstractTokenizer tokenizer = new Tokenizer(expression);
    Node root = buildDisjunct(tokenizer);
    return root;
  }

  private Node buildDisjunct(AbstractTokenizer tokenizer) throws EvaluationException {
    Node left = buildConjunct(tokenizer);
    Token token = tokenizer.getToken();
    switch (token.type) {
      case OR:
        {
          Node right = buildDisjunct(tokenizer);
          Node result = new Node(token, left, right);
          return result;
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

  private Node buildConjunct(AbstractTokenizer tokenizer) throws EvaluationException {
    Node left = buildNeg(tokenizer);
    Token token = tokenizer.getToken();
    switch (token.type) {
      case AND:
        {
          Node right = buildConjunct(tokenizer);
          Node result = new Node(token, left, right);
          return result;
        }
      case OR:
        {
          tokenizer.ungetToken();
          return left;
        }
      case EMPTY:
        {
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
      default:
        {
          tokenizer.ungetToken();
          throw new EvaluationException(
              "Invalid token " + token + " at position " + tokenizer.getCurrentPosition());
        }
    }
  }

  private Node buildNeg(AbstractTokenizer tokenizer) throws EvaluationException {
    int numOfNeg = 0;
    Token token;
    while ((token = tokenizer.getToken()).type == TokenType.NOT) {
      ++numOfNeg;
    }
    tokenizer.ungetToken();
    Node atom = buildAtom(tokenizer);
    if (numOfNeg % 2 == 1) {
      return new Node(Token.NOT_TOKEN, atom, Node.EMPTY_NODE);
    } else {
      return atom;
    }
  }

  @NotNull
  private Node buildAtom(AbstractTokenizer tokenizer) throws EvaluationException {
    Token token = tokenizer.getToken();
    switch (token.type) {
      case LEFT_PAREN:
        {
          ++numOfOpenedParenthesis;
          Node disNode = buildDisjunct(tokenizer);
          Token rightParen = tokenizer.getToken(); // getting )
          if (!rightParen.equals(Token.RIGHT_PAREN_TOKEN)) {
            throw new EvaluationException("Parenthesis disbalance");
          }
          --numOfOpenedParenthesis;
          return new Node(Token.LEFT_PAREN_TOKEN, disNode, Node.EMPTY_NODE);
        }
      case REF:
        {
          return new Node(token);
        }
      case TRUE:
        {
          return new Node(Token.TRUE_TOKEN);
        }
      case FALSE:
        {
          return new Node(Token.FALSE_TOKEN);
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

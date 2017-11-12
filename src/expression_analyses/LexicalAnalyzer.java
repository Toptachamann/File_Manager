package expression_analyses;

import auxiliary.EvaluationException;
import org.jetbrains.annotations.NotNull;

public class LexicalAnalyzer {
  public Node buildTree(String expression) throws EvaluationException {
    Tokenizer tokenizer = new BooleanTokenizer(expression);
    Node root = buildDisjunct(tokenizer);
    return root;
  }

  private Node buildDisjunct(Tokenizer tokenizer) throws EvaluationException {
    Node left = buildConjunct(tokenizer);
    Token token = tokenizer.getToken();
    switch (token.type) {
      case OR:
        {
          Node right = buildConjunct(tokenizer);
          Node result = new Node(token, left, right);
          return result;
        }
      case RIGHT_PAREN:{
        return left;
      }
      case EMPTY:
        {
          return left;
        }
      default:
        {
          throw new EvaluationException("Invalid token");
        }
    }
  }

  private Node buildConjunct(Tokenizer tokenizer) throws EvaluationException {
    Node left = buildNeg(tokenizer);
    Token token = tokenizer.getToken();
    switch (token.type) {
      case AND:
        {
          Node right = buildNeg(tokenizer);
          Node result = new Node(token, left, right);
          return result;
        }
        default:{
          tokenizer.ungetToken();
          return left;
        }
    }
  }

  private Node buildNeg(Tokenizer tokenizer) throws EvaluationException {
    int numOfNeg = 0;
    while (tokenizer.getToken().type == TokenType.NOT) {
      ++numOfNeg;
    }
    tokenizer.ungetToken();
    Node atom = buildAtom(tokenizer);
    if(numOfNeg%2 == 1){
      return new Node(Token.NOT_TOKEN, atom, Node.EMPTY_NODE);
    }else{
      return atom;
    }
  }

  @NotNull
  private Node buildAtom(Tokenizer tokenizer) throws EvaluationException {
    Token token = tokenizer.getToken();
    switch(token.type){
      case LEFT_PAREN:{
        Node disNode =  buildDisjunct(tokenizer);
        return new Node(Token.LEFT_PAREN_TOKEN, disNode, Node.EMPTY_NODE);
      }
      case REF:{
        return new Node(token, Node.EMPTY_NODE, Node.EMPTY_NODE);
      }default:{
        throw new EvaluationException("Invalid token " + token);
      }
    }
  }
}

package expression_analyses;

import java.util.Objects;

public class Node {
  public static final Node EMPTY_NODE = new Node(Token.EMPTY_TOKEN);
  public static final Node TRUE_NODE = new Node(Token.TRUE_TOKEN, EMPTY_NODE, EMPTY_NODE);
  public static final Node FALSE_NODE = new Node(Token.FALSE_TOKEN, EMPTY_NODE, EMPTY_NODE);
  public Node left = null;
  public Node right = null;
  public Token token;

  public Node(Token token) {
    this.token = token;
  }

  public Node(Token token, Node left, Node right) {
    this(token);
    this.left = left;
    this.right = right;
  }

  @Override
  public boolean equals(Object anotherObject) {
    if (anotherObject == this) {
      return true;
    } else if (anotherObject instanceof Node) {
      Node anotherNode = (Node) anotherObject;
      return Objects.equals(left, anotherNode.left)
          && Objects.equals(right, anotherNode.right)
          && token.equals(anotherNode.token);
    } else {
      return false;
    }
  }
}

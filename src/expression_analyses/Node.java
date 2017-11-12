package expression_analyses;

public class Node {
  public static final Node EMPTY_NODE = new Node(Token.EMPTY_TOKEN);
  public Node left = null;
  public Node right = null;
  public Token token;
  public Node(Token token){
    this.token = token;
  }
  public Node(Token token, Node left, Node right){
    this(token);
    this.left = left;
    this.right = right;
  }
}

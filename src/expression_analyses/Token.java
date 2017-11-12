package expression_analyses;

public class Token {
  public static final Token EMPTY_TOKEN = new Token(TokenType.EMPTY);
  public static final Token TRUE_TOKEN = new Token(TokenType.TRUE);
  public static final Token FALSE_TOKEN = new Token(TokenType.FALSE);
  public static final Token OR_TOKEN = new Token(TokenType.OR);
  public static final Token AND_TOKEN = new Token(TokenType.AND);
  public static final Token NOT_TOKEN = new Token(TokenType.NOT);
  public static final Token LEFT_PAREN_TOKEN = new Token(TokenType.LEFT_PAREN);
  TokenType type;
  String strToken = null;

  public Token(TokenType type) {
    this.type = type;
  }

  public Token(TokenType type, String token) {
    this(type);
    this.strToken = token;
  }

  @Override
  public String toString() {
    if (strToken != null) {
      return strToken;
    } else {
      return type.name;
    }
  }
}

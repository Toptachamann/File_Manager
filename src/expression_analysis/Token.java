package expression_analysis;


import java.util.Objects;

public class Token {
  public static final Token EMPTY_TOKEN = new Token(TokenType.EMPTY);
  public static final Token TRUE_TOKEN = new Token(TokenType.TRUE);
  public static final Token FALSE_TOKEN = new Token(TokenType.FALSE);
  public static final Token OR_TOKEN = new Token(TokenType.OR);
  public static final Token AND_TOKEN = new Token(TokenType.AND);
  public static final Token NOT_TOKEN = new Token(TokenType.NOT);
  public static final Token LEFT_PAREN_TOKEN = new Token(TokenType.LEFT_PAREN);
  public static final Token RIGHT_PAREN_TOKEN = new Token(TokenType.RIGHT_PAREN);
  public static final Token PLUS_TOKEN = new Token(TokenType.PLUS);
  public static final Token MINUS_TOKEN = new Token(TokenType.MINUS);
  public static final Token MULT_TOKEN = new Token(TokenType.MULTIPLY);
  public static final Token DIV_TOKEN = new Token(TokenType.DIV);
  public static final Token INT_DIV_TOKEN = new Token(TokenType.INT_DIV);
  public static final Token MOD_TOKEN = new Token(TokenType.MOD);
  public static final Token EXPONENT_TOKEN = new Token(TokenType.EXP);
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

  @Override
  public boolean equals(Object anotherObject){
    if(this == anotherObject){
      return true;
    }else if(anotherObject instanceof Token){
      Token anotherToken = (Token) anotherObject;
      return type == anotherToken.type && Objects.equals(strToken, anotherToken.strToken);
    }else{
      return false;
    }
  }
}

package expression_analyses;

public class Token {
  public String token;
  public TokenType type;
  public Token(TokenType type){
    this.type = type;
    this.token = "";
  }
  public Token(TokenType type, String token){
    this.type = type;
    this.token = token;
  }
}

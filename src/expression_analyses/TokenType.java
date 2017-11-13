package expression_analyses;

public enum TokenType {
  RIGHT_PAREN(")"),
  LEFT_PAREN("("),
  EMPTY("Empty token"),
  PLUS("+"),
  MINUS("-"),
  INT_DIV("//"),
  MOD("%"),
  MULTIPLY("*"),
  DIV("/"),
  NOT("not"),
  AND("and"),
  OR("or"),
  REF("Cell reference"),
  TRUE("true"),
  FALSE("false");
  public String name;

  TokenType(String name) {
    this.name = name;
  }
}

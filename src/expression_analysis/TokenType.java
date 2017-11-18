package expression_analysis;

public enum TokenType {
  RIGHT_PAREN(")"),
  LEFT_PAREN("("),
  EMPTY("Empty token"),
  PLUS("+"),
  MINUS("-"),
  MULTIPLY("*"),
  DIV("/"),
  INT_DIV("//"),
  MOD("%"),
  EXP("^"),
  NOT("not"),
  AND("and"),
  OR("or"),
  REF("Cell reference"),
  TRUE("true"),
  FALSE("false"),
  NUMBER("number");
  public String name;

  TokenType(String name) {
    this.name = name;
  }
}

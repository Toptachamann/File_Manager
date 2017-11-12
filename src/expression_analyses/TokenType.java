package expression_analyses;

public enum TokenType {
  RIGHT_PAREN("Right parenthesis"),
  LEFT_PAREN("Left parenthesis"),
  EMPTY("Empty token"),
  PLUS("Addition"),
  MINUS("Subtraction"),
  INT_DIV("Integer division"),
  MOD("Modulo"),
  MULTIPLY("Multiplication"),
  DIV("Division"),
  NOT("Negation"),
  AND("Conjunction"),
  OR("Disjunction"),
  REF("Cell reference"),
  TRUE("True"),
  FALSE("False");
  public String name;
  TokenType(String name){
    this.name = name;
  }
}

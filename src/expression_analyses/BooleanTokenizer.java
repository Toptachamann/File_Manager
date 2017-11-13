package expression_analyses;

import auxiliary.EvaluationException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class BooleanTokenizer implements Tokenizer {
  private int currentPosition = 0;
  private String expression;
  private int length;

  public BooleanTokenizer(String expression) {
    this.expression = expression;
    this.length = expression.length();
  }

  @NotNull
  public Token getToken() throws EvaluationException {
    for (;
        currentPosition < expression.length()
            && Character.isWhitespace(expression.charAt(currentPosition));
        ++currentPosition) {}
    if (currentPosition >= expression.length()) {
      return Token.EMPTY_TOKEN;
    }
    switch (expression.charAt(currentPosition)) {
      case 'F':
      case 'f':
        if (currentPosition + 5 > length) {
          invalid();
        }
        String falseStr = expression.substring(currentPosition, currentPosition + 5);
        if (falseStr.equalsIgnoreCase("false")) {
          currentPosition += 5;
          return Token.FALSE_TOKEN;
        } else {
          invalid();
        }
        break;
      case 'T':
      case 't':
        if (currentPosition + 4 > length) {
          invalid();
        }
        String trueStr = expression.substring(currentPosition, currentPosition + 4);
        if (trueStr.equalsIgnoreCase("true")) {
          currentPosition += 4;
          return Token.TRUE_TOKEN;
        } else {
          invalid();
        }
        break;
      case 'A':
      case 'a':
        if (currentPosition + 3 > length) {
          invalid();
        }
        String andStr = expression.substring(currentPosition, currentPosition + 3);
        if (andStr.equalsIgnoreCase("and")) {
          currentPosition += 3;
          return Token.AND_TOKEN;
        } else {
          invalid();
        }
        break;
      case '&':
        if (currentPosition + 1 < length && expression.charAt(currentPosition + 1) == '&') {
          currentPosition += 2;
        } else {
          currentPosition += 1;
        }
        return Token.AND_TOKEN;
      case 'O':
      case 'o':
        if (currentPosition + 2 > length) {
          invalid();
        }
        String orStr = expression.substring(currentPosition, currentPosition + 2);
        if (orStr.equalsIgnoreCase("or")) {
          currentPosition += 2;
          return Token.OR_TOKEN;
        } else {
          invalid();
        }
        break;
      case '|':
        if (currentPosition + 1 < length && expression.charAt(currentPosition + 1) == '|') {
          currentPosition += 2;
        } else {
          currentPosition += 1;
        }
        return Token.OR_TOKEN;
      case 'N':
      case 'n':
        if (currentPosition + 3 > length) {
          invalid();
        }
        String notStr = expression.substring(currentPosition, currentPosition + 3);
        if (notStr.equalsIgnoreCase("not")) {
          currentPosition += 3;
          return Token.NOT_TOKEN;
        } else {
          invalid();
        }
        break;
      case '!':
        currentPosition += 1;
        return Token.NOT_TOKEN;
      case '[':
        int closingIndex = currentPosition + 1;
        for (;
            closingIndex < expression.length() && expression.charAt(closingIndex) != ']';
            ++closingIndex) {}
        if (closingIndex >= expression.length()) {
          invalid();
        }
        String reference = expression.substring(currentPosition, closingIndex + 1);
        currentPosition = closingIndex + 1;
        return new Token(TokenType.REF, reference);
      case '(':
        currentPosition += 1;
        return Token.LEFT_PAREN_TOKEN;
      case ')':
        currentPosition += 1;
        return Token.RIGHT_PAREN_TOKEN;
      default:
        invalid();
    }
    return Token.EMPTY_TOKEN;
  }

  @Override
  public int getCurrentPosition(){
    return currentPosition;
  }

  @Contract("_ -> fail")
  private void invalid(int position) throws EvaluationException {
    throw new EvaluationException("Invalid syntax in " + expression + " at position " + position);
  }

  @Contract(" -> fail")
  private void invalid() throws EvaluationException {
    throw new EvaluationException(
        "Invalid syntax in " + expression + " at position " + currentPosition);
  }

  public void ungetToken() throws EvaluationException {
    --currentPosition;
    for (;
        currentPosition >= 0 && Character.isWhitespace(expression.charAt(currentPosition));
        --currentPosition) {}
    if (currentPosition == -1) {
      return;
    }
    switch (expression.charAt(currentPosition)) {
      case 'E':
      case 'e':
        char c = expression.charAt(currentPosition - 1);
        if (c == 's') {
          currentPosition -= 4;
        } else {
          currentPosition -= 3;
        }
        break;
      case 'D':
      case 'd':
        currentPosition -= 2;
        break;
      case '&':
        if (currentPosition - 1 >= 0 && expression.charAt(currentPosition - 1) == '&') {
          currentPosition -= 1;
        }
        break;
      case 'R':
      case 'r':
        currentPosition -= 1;
        break;
      case '|':
        if (currentPosition - 1 >= 0 && expression.charAt(currentPosition - 1) == '|') {
          currentPosition -= 1;
        }
        break;
      case 'T':
      case 't':
        currentPosition -= 2;
        break;
      case '!':
        break;
      case ']':
        int openingIndex = currentPosition - 1;
        for (; openingIndex >= 0 && expression.charAt(openingIndex) != '['; --openingIndex) {}
        currentPosition = openingIndex;
        break;
      case '(':
      case ')':
        break;
      default:
        invalid();
    }
  }

  @Override
  public boolean hasNext() {
    return currentPosition < expression.length();
  }
}

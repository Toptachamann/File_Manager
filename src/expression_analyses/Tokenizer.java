package expression_analyses;

import auxiliary.EvaluationException;

public interface Tokenizer {
  Token getToken() throws EvaluationException;
  void ungetToken() throws EvaluationException;
  boolean hasNext();
  int getCurrentPosition();
}

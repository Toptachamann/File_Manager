package expression_analysis;

import auxiliary.EvaluationException;

public interface AbstractTokenizer {
  Token getToken() throws EvaluationException;
  void ungetToken() throws EvaluationException;
  boolean hasNext();
  int getCurrentPosition();
}

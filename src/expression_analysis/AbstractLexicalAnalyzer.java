package expression_analysis;

import auxiliary.EvaluationException;

public interface AbstractLexicalAnalyzer {
  Node buildTree(String expression) throws EvaluationException;
}

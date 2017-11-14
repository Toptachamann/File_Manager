package expression_analyses;

import auxiliary.EvaluationException;

public interface AbstractLexicalAnalyzer {
  Node buildTree(String expression) throws EvaluationException;
}

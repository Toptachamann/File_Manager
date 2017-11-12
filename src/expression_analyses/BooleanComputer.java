package expression_analyses;

import java.util.Arrays;
import java.util.HashMap;

public class BooleanComputer {
  private String[][] values;
  private String[][] expressions;
  private HashMap<String, Integer> columnMap;
  private HashMap<String, Integer> rowMap;
  private boolean[][] visited;

  public BooleanComputer(
      String[][] values,
      String[][] expressions,
      HashMap<String, Integer> columnMap,
      HashMap<String, Integer> rowMap) {
    this.values = values;
    this.expressions = expressions;
    this.columnMap = columnMap;
    this.rowMap = rowMap;
    this.visited = new boolean[values.length][values[0].length];
    for(int i = 0; i < visited.length; i++){
      Arrays.fill(visited[i], Boolean.FALSE);
    }
  }

  public String[][] compute(int x, int y) {
    LexicalAnalyzer analyzer = new LexicalAnalyzer();
    Node root = analyzer.buildTree(expressions[x][y]);


  }
  public boolean compute(Node root){

  }
}

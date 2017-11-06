/** Created by Timofey on 9/17/2017. */
package auxiliary;

import javax.swing.tree.DefaultMutableTreeNode;

public class MyTreeNode extends DefaultMutableTreeNode {

  private boolean loaded;

  public MyTreeNode() {
    super();
    this.loaded = false;
  }

  public MyTreeNode(Object userObject) {
    super(userObject);
    this.loaded = false;
  }

  public MyTreeNode(Object userObject, boolean allowsChildren) {
    super(userObject, allowsChildren);
  }

  public boolean isLoaded() {
    return this.loaded;
  }

  public void setLoaded(boolean loaded) {
    this.loaded = loaded;
  }
}

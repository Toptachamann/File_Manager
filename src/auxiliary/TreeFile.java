/** Created by Timofey on 9/17/2017. */
package auxiliary;

import java.io.File;

public class TreeFile extends File {
  private static final File[] roots = File.listRoots();

  private String absolutePath;

  public TreeFile(String pathname) {
    super(pathname);
    this.absolutePath = pathname;
  }

  public TreeFile(File parent, String child) {
    super(parent, child);
    absolutePath = parent.toString() + "\\" + child;
  }

  public TreeFile(String parent, String child) {
    super(parent, child);
    absolutePath = parent + "\\" + child;
  }

  public String getAbsolutePath() {
    return this.absolutePath;
  }

  public boolean isRoot() {
    for (File file : roots) {
      if (absolutePath.equals(file.toString())) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    if (isRoot()) {
      return absolutePath;
    } else {
      return super.getName();
    }
  }
}

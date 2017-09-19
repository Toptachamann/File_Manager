import java.io.File;

/**
 * Created by Timofey on 9/17/2017.
 */
class TreeFile extends File {
    private static File[] roots = File.listRoots();
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

    @Override
    public String toString() {
        for (File file : roots) {
            if (absolutePath.equals(file.toString())) {
                return absolutePath;
            }
        }
        return this.getName();
    }
}

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created by Timofey on 9/17/2017.
 */
class MyTreeNode extends DefaultMutableTreeNode {
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

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public boolean isLoaded() {
        return this.loaded;
    }
}
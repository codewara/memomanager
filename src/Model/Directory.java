package Model;

import java.util.ArrayList;
import java.util.List;

public class Directory extends SystemNode {
    private List<SystemNode> children;

    public Directory(String name, Directory parent) {
        super(name, parent);
        this.children = new ArrayList<>();
    }

    public List<SystemNode> getChildren() { return children; }

    public void addChild(SystemNode node) {children.add(node); }

    public void removeChild(SystemNode node) { children.remove(node); }

    @Override
    public boolean isDirectory() { return true; }

    @Override
    public int getSize() {
        int size = 0;
        for (SystemNode child : children) {
            size += child.getSize();
        }
        return size;
    }
}

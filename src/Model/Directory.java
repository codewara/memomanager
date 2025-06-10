package Model;

import java.util.ArrayList;
import java.util.List;

public class Directory extends SystemNode {
    private final List<SystemNode> children;

    // Constructor for creating a new directory
    public Directory(String name, Directory parent, String modifiedTime) {
        super(name, parent, modifiedTime);
        this.children = new ArrayList<>();
    }

    // Getter and Setter for children and specific child nodes
    public List<SystemNode> getChildren() { return children; }
    public SystemNode findChild (String name) {
        for (SystemNode child : children) {
            if (child.getName().equals(name)) return child;
        } return null;
    }

    // Methods to add and remove child nodes
    public void addChild(SystemNode node) {children.add(node); }
    public void removeChild(SystemNode node) { children.remove(node); }

    // Override methods from SystemNode
    @Override public boolean isDirectory() { return true; }
    @Override public int getSize() {
        int size = 0;
        for (SystemNode child : children) size += child.getSize();
        return size;
    }
}

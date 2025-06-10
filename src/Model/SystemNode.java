package Model;

public abstract class SystemNode {
    protected String name;
    protected Directory parent;
    protected String modifiedTime;

    // Constructor for SystemNode
    public SystemNode(String name, Directory parent, String modifiedTime) {
        this.name = name;
        this.parent = parent;
        this.modifiedTime = modifiedTime;
    }

    // Getters and Setters
    public String getName() { return name; }

    public Directory getParent() { return parent; }

    public String getModifiedTime() { return modifiedTime; }
    public void setModifiedTime(String modifiedTime) {
        this.modifiedTime = modifiedTime;
        if (parent != null) parent.setModifiedTime(modifiedTime); // Update parent's modified time recursively
    }

    // Abstract methods to be implemented by subclasses
    public abstract boolean isDirectory();
    public abstract int getSize();
}

package Model;

public abstract class SystemNode {
    protected String name;
    protected Directory parent;
    protected String modifiedTime;

    public SystemNode(String name, Directory parent, String modifiedTime) {
        this.name = name;
        this.parent = parent;
        this.modifiedTime = modifiedTime;
    }

    public String getName() { return name; }

    public Directory getParent() { return parent; }
    public void setParent(Directory parent) { this.parent = parent; }

    public String getModifiedTime() { return modifiedTime; }
    public void setModifiedTime(String modifiedTime) {
        this.modifiedTime = modifiedTime;
        if (parent != null) parent.setModifiedTime(modifiedTime);
    }

    public abstract boolean isDirectory();

    public abstract int getSize();
}

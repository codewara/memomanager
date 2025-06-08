package Model;

public abstract class SystemNode {
    protected String name;
    protected Directory parent;
    protected long modifiedTime;

    public SystemNode(String name, Directory parent) {
        this.name = name;
        this.parent = parent;
    }

    public String getName() { return name; }

    public Directory getParent() { return parent; }
    public void setParent(Directory parent) { this.parent = parent; }

    public long getModifiedAt() { return modifiedTime; }
    public void setModifiedAt(long modifiedTime) { this.modifiedTime = modifiedTime; }

    public abstract boolean isDirectory();
    public abstract int getSize();
}

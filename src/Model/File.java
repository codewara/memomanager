package Model;

public class File extends SystemNode {
    private String content;

    public File(String name, Directory parent, String content) {
        super(name, parent);
        this.content = content;
    }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public int getSize() {
        return content != null ? content.length() : 0;
    }
}

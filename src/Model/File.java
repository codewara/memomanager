package Model;

import java.util.Date;
import java.text.SimpleDateFormat;

public class File extends SystemNode {
    private String content;
    private int size;

    public File(String name, Directory parent, String content, String modifiedTime) {
        super(name, parent, modifiedTime);
        this.content = content;
        this.size = content != null ? content.length() : 0;
    }

    public String getContent() { return content; }
    public void setContent(String content) {
        this.content = content;
        this.size = content != null ? content.length() : 0;
        setModifiedTime(new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss").format(new Date()));
    }

    @Override public boolean isDirectory() { return false; }

    @Override public int getSize() { return size; }
}

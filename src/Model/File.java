package Model;

import java.util.*;
import java.text.SimpleDateFormat;

public class File extends SystemNode {
    private List<Integer> allocatedBlocks = new ArrayList<>();
    private String content;
    private int size;

    public File(String name, Directory parent, String content, String modifiedTime, List<Integer> allocatedBlocks) {
        super(name, parent, modifiedTime);
        this.content = content;
        this.size = content != null ? content.length() : 0;
        this.allocatedBlocks = new ArrayList<>(allocatedBlocks);
    }

    public String getContent() { return content; }
    public void setContent(String content) {
        this.content = content;
        this.size = content != null ? content.length() : 0;
        setModifiedTime(new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss").format(new Date()));
    }

    public Set<Integer> getUsedBlocks() { return new HashSet<>(allocatedBlocks); }
    public void setUsedBlocks(List<Integer> blocks) {
        this.allocatedBlocks = new ArrayList<>(blocks);
    }

    @Override public boolean isDirectory() { return false; }

    @Override public int getSize() { return size; }
}

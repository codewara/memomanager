package Model;

import java.util.*;
import java.text.SimpleDateFormat;

public class File extends SystemNode {
    private List<Integer> allocatedBlocks;
    private String content;
    private int startBlock;
    private int endBlock;
    private int size;

    public File(String name, Directory parent, String content, String modifiedTime, List<Integer> allocatedBlocks) {
        super(name, parent, modifiedTime);
        this.content = content;
        this.size = content != null ? content.length() : 0;
        this.allocatedBlocks = new ArrayList<>(allocatedBlocks);
        this.startBlock = allocatedBlocks.isEmpty() ? -1 : allocatedBlocks.getFirst();
        this.endBlock = allocatedBlocks.isEmpty() ? -1 : allocatedBlocks.getLast();
    }

    public String getContent() { return content; }
    public void setContent(String content) {
        this.content = content;
        this.size = content != null ? content.length() : 0;
        setModifiedTime(new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(new Date()));
    }

    public Set<Integer> getUsedBlocks() { return new HashSet<>(allocatedBlocks); }
    public void setUsedBlocks(List<Integer> blocks) {
        this.allocatedBlocks = new ArrayList<>(blocks);
        this.startBlock = blocks.isEmpty() ? -1 : blocks.getFirst();
        this.endBlock = blocks.isEmpty() ? -1 : blocks.getLast();
    }

    public String getStartBlock() { return startBlock == -1 ? "N/A" : String.valueOf(startBlock); }
    public String getEndBlock() { return endBlock == -1 ? "N/A" : String.valueOf(endBlock); }

    @Override public boolean isDirectory() { return false; }

    @Override public int getSize() { return size; }
}

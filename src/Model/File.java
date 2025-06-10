package Model;

import java.util.*;
import java.text.SimpleDateFormat;

public class File extends SystemNode {
    private List<Integer> allocatedBlocks;
    private String content;
    private int startBlock;
    private int endBlock;
    private int size;

    // Constructor for creating a new file with content
    public File(String name, Directory parent, String content, String modifiedTime, List<Integer> allocatedBlocks) {
        super(name, parent, modifiedTime);
        this.content = content;
        this.size = content != null ? content.length() : 0; // Size is based on content length
        this.allocatedBlocks = new ArrayList<>(allocatedBlocks);
        this.startBlock = allocatedBlocks.isEmpty() ? -1 : allocatedBlocks.getFirst();
        this.endBlock = allocatedBlocks.isEmpty() ? -1 : allocatedBlocks.getLast();
    }

    // Setter and Getter for content
    public String getContent() { return content; }
    public void setContent(String content) {
        this.content = content;
        this.size = content != null ? content.length() : 0; // Update size based on content length
        setModifiedTime(new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(new Date())); // Update modified time on content change
    }

    // Setter and Getter for allocated blocks
    public Set<Integer> getUsedBlocks() { return new HashSet<>(allocatedBlocks); }
    public void setUsedBlocks(List<Integer> blocks) {
        this.allocatedBlocks = new ArrayList<>(blocks);
        this.startBlock = blocks.isEmpty() ? -1 : blocks.getFirst();
        this.endBlock = blocks.isEmpty() ? -1 : blocks.getLast();
    }

    // Getters for start and end blocks
    public String getStartBlock() { return startBlock == -1 ? "N/A" : String.valueOf(startBlock); }
    public String getEndBlock() { return endBlock == -1 ? "N/A" : String.valueOf(endBlock); }

    // Override methods from SystemNode
    @Override public boolean isDirectory() { return false; }
    @Override public int getSize() { return size; }
}

package Model;

import java.util.*;

public class DiskManager {
    private final int diskSize;
    private final Block[] blocks;

    // Constructor to initialize the disk with a given size
    public DiskManager(int diskSize) {
        this.diskSize = diskSize;
        this.blocks = new Block[diskSize];
        for (int i = 0; i < diskSize; i++) blocks[i] = new Block(i); // Initialize each block
    }

    // Allocate a single file to block(s) -> when created
    public List<Integer> allocateContiguous(String content) {
        int requiredBlocks = (int) Math.ceil((double) content.length() / 512); // Assuming each block can hold 512 bytes

        // Check if enough contiguous (required) blocks are available
        for (int i = 0; i <= diskSize - requiredBlocks; i++) {
            boolean available = true;
            for (int j = 0; j < requiredBlocks; j++) {
                if (blocks[i + j].isUsed()) {
                    available = false;
                    break;
                }
            }

            // If all required blocks are available, allocate them
            if (available) {
                List<Integer> allocated = new ArrayList<>();
                for (int j = 0; j < requiredBlocks; j++) {
                    blocks[i + j].setUsed(true);
                    blocks[i + j].setData(content.substring(j * 10, Math.min(content.length(), (j + 1) * 10)));
                    allocated.add(i + j);
                } return allocated;
            }
        } return new ArrayList<>(); // Return empty list if allocation fails
    }

    // Deallocate a file from block(s) -> when deleted
    public void deallocate(Set<Integer> blockIndices) {
        for (int index : blockIndices) {
            if (index >= 0 && index < diskSize) {
                blocks[index].setUsed(false);
                blocks[index].setData("");
            }
        }
    }

    // Getters for all blocks and individual block
    public Block[] getBlocks() { return blocks; }
    public Block getBlock(int index) { return blocks[index]; }
}

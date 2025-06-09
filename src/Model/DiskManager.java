package Model;

import java.util.*;

public class DiskManager {
    private final int diskSize;
    private final Block[] blocks;

    public DiskManager(int diskSize) {
        this.diskSize = diskSize;
        this.blocks = new Block[diskSize];
        for (int i = 0; i < diskSize; i++) {
            blocks[i] = new Block(i);
        }
    }

    public List<Integer> allocateContiguous(String content) {
        int requiredBlocks = (int) Math.ceil((double) content.length() / 10);
        for (int i = 0; i <= diskSize - requiredBlocks; i++) {
            boolean available = true;
            for (int j = 0; j < requiredBlocks; j++) {
                if (blocks[i + j].isUsed()) {
                    available = false;
                    break;
                }
            }
            if (available) {
                List<Integer> allocated = new ArrayList<>();
                for (int j = 0; j < requiredBlocks; j++) {
                    blocks[i + j].setUsed(true);
                    blocks[i + j].setData(content.substring(j * 10, Math.min(content.length(), (j + 1) * 10)));
                    allocated.add(i + j);
                } return allocated;
            }
        } return new ArrayList<>();
    }

    public Block[] getBlocks() { return blocks; }

    public void reset() {
        for (Block block : blocks) {
            block.setUsed(false);
            block.setData("");
        }
    }
}

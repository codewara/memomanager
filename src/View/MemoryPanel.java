package View;

import Model.*;

import java.awt.*;
import java.util.*;
import javax.swing.*;

public class MemoryPanel extends JPanel {
    private Set<Integer> highlightedBlocks = new HashSet<>();
    private final DiskManager diskManager;

    // Constructor that initializes the MemoryPanel with a DiskManager
    public MemoryPanel(DiskManager diskManager) {
        this.diskManager = diskManager;
        setLayout(new GridLayout(0, 16, 2, 2));
        renderBlocks();
        revalidate();
        repaint();
    }

    // Method to render the blocks in the MemoryPanel
    private void renderBlocks() {
        int blockSize = 5; // Size of each block in pixels

        removeAll(); // Clear existing components
        for (Block block : diskManager.getBlocks()) {
            Color blockColor = highlightedBlocks.contains(block.getIndex()) ? Color.YELLOW : Color.BLACK; // Highlight color for selected blocks
            JPanel blockPanel = new JPanel();
            blockPanel.setMaximumSize(new Dimension(blockSize, blockSize));
            blockPanel.setBackground(block.isUsed() ? Color.GREEN : Color.LIGHT_GRAY); // Color green for used blocks, light gray for free blocks
            blockPanel.setBorder(BorderFactory.createLineBorder(blockColor));
            add(blockPanel);
        }
    }

    // Method to update the MemoryPanel, re-rendering the blocks
    public void update() {
        renderBlocks();
        revalidate();
        repaint();
    }

    // Method to highlight specific blocks based on their indexes
    public void highlightBlocks(Set<Integer> indexes) {
        this.highlightedBlocks = indexes;
        update();
    }
}

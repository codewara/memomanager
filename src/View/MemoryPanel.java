package View;

import Model.*;

import java.awt.*;
import java.util.*;
import javax.swing.*;

public class MemoryPanel extends JPanel {
    private Set<Integer> highlightedBlocks = new HashSet<>();
    private final DiskManager diskManager;

    public MemoryPanel(DiskManager diskManager) {
        this.diskManager = diskManager;
        setLayout(new GridLayout(0, 16, 2, 2));
        renderBlocks();
        revalidate();
        repaint();
    }

    private void renderBlocks() {
        int blockSize = 5; // Size of each block in pixels

        removeAll();
        for (Block block : diskManager.getBlocks()) {
            Color blockColor = highlightedBlocks.contains(block.getIndex()) ? Color.YELLOW : Color.BLACK;
            JPanel blockPanel = new JPanel();
            blockPanel.setMaximumSize(new Dimension(blockSize, blockSize));
            blockPanel.setBackground(block.isUsed() ? Color.GREEN : Color.LIGHT_GRAY);
            blockPanel.setBorder(BorderFactory.createLineBorder(blockColor));
            add(blockPanel);
        }
    }

    public void update() {
        renderBlocks();
        revalidate();
        repaint();
    }

    public void highlightBlocks(Set<Integer> indexes) {
        this.highlightedBlocks = indexes;
        update();
    }
}

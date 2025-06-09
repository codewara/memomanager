package View;

import Model.*;

import javax.swing.*;
import java.awt.*;

public class MemoryPanel extends JPanel {
    private final DiskManager diskManager;

    public MemoryPanel(DiskManager diskManager) {
        this.diskManager = diskManager;
        setLayout(new GridLayout(0, 16, 2, 2));
        renderBlocks();
    }

    private void renderBlocks() {
        int blockSize = 5; // Size of each block in pixels

        removeAll();
        for (Block block : diskManager.getBlocks()) {
            JPanel blockPanel = new JPanel();
            blockPanel.setMaximumSize(new Dimension(blockSize, blockSize));
            blockPanel.setBackground(block.isUsed() ? Color.GREEN : Color.LIGHT_GRAY);
            blockPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            add(blockPanel);
        }
    }

    public void update() {
        renderBlocks();
        revalidate();
        repaint();
    }
}

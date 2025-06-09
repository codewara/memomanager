package View;

import Model.*;

import javax.swing.*;
import java.awt.*;

public class MemoryPanel extends JPanel {
    private final DiskManager diskManager;

    public MemoryPanel(DiskManager diskManager) {
        this.diskManager = diskManager;
        setLayout(new GridLayout(0, 64, 2, 2));
        renderBlocks();
    }

    private void renderBlocks() {
        removeAll();
        for (Block block : diskManager.getBlocks()) {
            JPanel blockPanel = new JPanel();
            blockPanel.setPreferredSize(new Dimension(20, 20));
            blockPanel.setBackground(block.isUsed() ? Color.GREEN : Color.LIGHT_GRAY);
            blockPanel.setToolTipText(block.isUsed()
                ? ("[" + block.getIndex() + "] " + block.getData())
                : ("[" + block.getIndex() + "] Free"));
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

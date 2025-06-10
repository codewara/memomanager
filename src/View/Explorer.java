package View;

import Controller.SystemController;
import Model.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class Explorer extends JFrame {
    private final JTextField pathField;
    private final JTextField CLIField;
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final ImageIcon dirIcon = new ImageIcon(
        new ImageIcon("src/Assets/directory.png").getImage()
        .getScaledInstance(25, 25, Image.SCALE_SMOOTH)
    );
    private final ImageIcon fileIcon = new ImageIcon(
        new ImageIcon("src/Assets/file.png").getImage()
        .getScaledInstance(25, 25, Image.SCALE_SMOOTH)
    );

    private final SystemController controller;
    private final MemoryPanel memoryPanel;
    private Directory currentDir;

    // Constructor
    public Explorer (SystemController controller, Directory root, DiskManager diskManager) {
        super("File Explorer");
        this.controller = controller;
        this.currentDir = root;

        // Frame setup
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Path field
        JPanel topPanel = new JPanel(new BorderLayout());
        pathField = new JTextField();
        pathField.setEditable(false);
        topPanel.add(pathField, BorderLayout.NORTH);
        add(topPanel, BorderLayout.NORTH);

        // Table model
        tableModel = new DefaultTableModel(new Object[]{"", "Name", "Modified", "Size"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
            @Override public Class<?> getColumnClass(int columnIndex) { return columnIndex == 0 ? Icon.class : String.class; }
        };

        // Create table with custom layout
        table = new JTable(tableModel) {
            @Override public void doLayout() {
                super.doLayout();
                if (getColumnModel().getColumnCount() > 0) {
                    getColumnModel().getColumn(0).setPreferredWidth(25);
                    getColumnModel().getColumn(0).setMinWidth(25);
                    getColumnModel().getColumn(0).setMaxWidth(25);
                }
            }
        };

        // Table properties
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        // Event: click on table rows
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int row = table.getSelectedRow();
                    String name = (String) table.getValueAt(row, 1);
                    if (!name.equals("..")) controller.showMemoryUsage(name);
                } else if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    String name = (String) table.getValueAt(row, 1);
                    controller.open(name);
                }
            }
        });

        // Scroll pane for table
        JScrollPane tableScroll = new JScrollPane(table);

        // Right panel: MemoryPanel
        this.memoryPanel = new MemoryPanel(diskManager);
        memoryPanel.setBorder(BorderFactory.createTitledBorder("Memory View - 2MB"));

        // Wrap MemoryPanel in a scroll pane
        JScrollPane memoryScrollPane = new JScrollPane(memoryPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScroll, memoryScrollPane);
        splitPane.setEnabled(false);
        splitPane.setDividerSize(0);

        // Set initial divider location and update it on resize
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                splitPane.setDividerLocation(getWidth() - 275);
            }
        });
        add(splitPane, BorderLayout.CENTER);

        // CLI field
        CLIField = new JTextField();
        CLIField.addActionListener(_ -> {
            controller.handleCommand(CLIField.getText()); // Process command from CLI
            CLIField.setText(""); // Clear CLI field after processing
        });
        add(CLIField, BorderLayout.SOUTH);

        setVisible(true); // Make the frame visible
    }

    // Update the table with current directory contents
    public void updateTable(Directory dir) {
        this.controller.updateJSON(); // Update JSON file with current state
        this.currentDir = dir; // Set current directory
        pathField.setText(getFullPath(currentDir)); // Update path field
        tableModel.setRowCount(0);

        // Add ".." for parent directory if it exists
        if (currentDir.getParent() != null) tableModel.addRow(new Object[]{dirIcon, "..", "", ""});

        // Add directories in the current directory first
        for (SystemNode node : currentDir.getChildren()) if (node.isDirectory()) {
            String size = calculateSize(node.getSize());
            String modified = String.valueOf(node.getModifiedTime());
            tableModel.addRow(new Object[]{dirIcon, node.getName(), modified, size});
        }

        // Add files in the current directory after directories
        for (SystemNode node : currentDir.getChildren()) if (!node.isDirectory()) {
            String size = calculateSize(node.getSize());
            String modified = String.valueOf(node.getModifiedTime());
            tableModel.addRow(new Object[]{fileIcon, node.getName(), modified, size});
        }

        // If the directory is empty, add a placeholder row
        if (currentDir.getChildren().isEmpty()) tableModel.addRow(new Object[]{null, "(Empty Directory)", "", ""});
    }

    // Show message dialog if operation is error
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Show content of a file in a dialog
    public void showContent(File file) {
        // Create a dialog window
        JDialog dialog = new JDialog(this, file.getName(), true); // modal dialog
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Text area with file content
        JTextArea textArea = new JTextArea(file.getContent());
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Add text area inside a scroll pane
        JScrollPane scrollPane = new JScrollPane(textArea);
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Store original content
        String originalContent = file.getContent();

        // Handle window close event
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                String newContent = textArea.getText();

                // Confirm if content has changed
                if (!newContent.equals(originalContent)) {
                    int option = JOptionPane.showConfirmDialog(dialog,
                        "Do you want to save the changes?",
                        "Save Confirmation", JOptionPane.YES_NO_OPTION
                    );

                    // Save or discard changes based on user choice
                    if (option == JOptionPane.YES_OPTION) {
                        file.setContent(newContent); // Save the changes
                        updateTable(currentDir); // Refresh the table
                        dialog.dispose();
                    } else if (option == JOptionPane.NO_OPTION) dialog.dispose(); // Close without saving
                } else dialog.dispose(); // Close directly if no changes
            }
        });

        dialog.setVisible(true);
    }

    // Getter for MemoryPanel
    public MemoryPanel getMemoryPanel() { return memoryPanel; }

    // Get full path of the directory
    private String getFullPath(Directory dir) {
        StringBuilder path = new StringBuilder();
        Directory current = dir;

        // Traverse up to the root directory
        while (current != null) {
            if (!current.getName().isEmpty()) path.insert(0, current.getName() + "/"); // Add directory name to the front
            current = current.getParent(); // Move to parent directory
        }
        return path.isEmpty() ? "/" : path.toString(); // Ensure root path is "/"
    }

    // Calculate size in human-readable format
    private String calculateSize(int size) {
        if (size < 1024) return size + " B"; // Bytes
        else if (size < 1024 * 1024) return String.format("%.2f KB", size / 1024.0); // Kilobytes
        else if (size < 1024 * 1024 * 1024) return String.format("%.2f MB", size / (1024.0 * 1024)); // Megabytes
        else return String.format("%.2f GB", size / (1024.0 * 1024 * 1024)); // Gigabytes
    }
}

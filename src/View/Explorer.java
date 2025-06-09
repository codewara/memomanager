package View;

import Model.Directory;
import Model.SystemNode;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Explorer extends JFrame {
    private Directory currentDir;
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

    public Explorer (Directory root) {
        super("File Explorer");
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

        // Event: double-click directory to open
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    String name = (String) table.getValueAt(row, 1);
                    for (SystemNode node : currentDir.getChildren()) {
                        if (node.getName().equals(name) && node.isDirectory()) {
                            currentDir = (Directory) node;
                            updateTable();
                            break;
                        }
                    }
                }
            }
        });

        JScrollPane tableScroll = new JScrollPane(table);

        // Right panel (Memory structure visualization)
        JPanel memoryPanel = new JPanel();
        memoryPanel.setPreferredSize(new Dimension(300, 0));
        memoryPanel.setBorder(BorderFactory.createTitledBorder("Memory View"));
        memoryPanel.add(new JLabel("(Memory visualization here)"));

        // SplitPane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScroll, memoryPanel);
        add(splitPane, BorderLayout.CENTER);

        // CLI field
        CLIField = new JTextField();
        CLIField.addActionListener(_ -> {
            handleCommand(CLIField.getText());
            CLIField.setText("");
        });
        add(CLIField, BorderLayout.SOUTH);

        updateTable();
        setVisible(true);
    }

    private void updateTable() {
        pathField.setText(getFullPath(currentDir));
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (SystemNode node : currentDir.getChildren()) {
            ImageIcon icon = node.isDirectory() ? dirIcon : fileIcon;
            String size = calculateSize(node.getSize());
            String modified = String.valueOf(node.getModifiedTime());
            tableModel.addRow(new Object[]{icon, node.getName(), modified, size});
        }

        if (currentDir.getChildren().isEmpty()) {
            tableModel.addRow(new Object[]{null, "(Empty Directory)", "", ""});
        }
    }

    private void handleCommand (String command) {
        if (command.startsWith("cd ")) {
            String dirName = command.substring(3).trim();
            if (dirName.equals("..")) {
                if (currentDir.getParent() != null) {
                    currentDir = currentDir.getParent();
                    updateTable();
                }
            } else {
                for (SystemNode node : currentDir.getChildren()) {
                    if (node.isDirectory() && node.getName().equals(dirName)) {
                        currentDir = (Directory) node;
                        updateTable();
                        return;
                    }
                }
                JOptionPane.showMessageDialog(this, "Directory not found " + dirName, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else JOptionPane.showMessageDialog(this, "Unknown command: " + command, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private String getFullPath(Directory dir) {
        StringBuilder path = new StringBuilder();
        Directory current = dir;
        while (current != null) {
            if (!current.getName().isEmpty()) path.insert(0, current.getName() + "/");
            current = current.getParent();
        }
        return path.isEmpty() ? "/" : path.toString();
    }

    private String calculateSize(int size) {
        if (size < 1024) return size + " B";
        else if (size < 1024 * 1024) return (size / 1024.0) + " KB";
        else if (size < 1024 * 1024 * 1024) return (size / (1024.0 * 1024)) + " MB";
        else return (size / (1024.0 * 1024 * 1024)) + " GB";
    }
}

package Controller;

import Model.*;
import View.Explorer;

import javax.swing.*;
import java.util.*;
import java.io.File;
import java.text.SimpleDateFormat;

public class SystemController {
    private Explorer explorer;
    private Directory currentDir;
    private final DiskManager diskManager;

    public SystemController() { this.diskManager = new DiskManager(4096); }

    public void start (File json) {
        try {
            Directory root = FileLoader.loadFromJSON(json, diskManager);
            this.currentDir = root;
            this.explorer = new Explorer(this, root, diskManager);
            this.explorer.updateTable(currentDir);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to load directory structure", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void handleCommand (String command) {
        String[] cmd = command.toLowerCase().trim().split("\\s+");
        if (cmd.length == 0) return;

        switch (cmd[0]) {
            case "mkdir" -> {
                if (cmd.length < 2) {
                    explorer.showError("Usage: mkdir <directory>");
                    return;
                } mkdir(cmd[1]);
            }
            case "cd" -> {
                if (cmd.length < 2) {
                    explorer.showError("Usage: cd <directory>");
                    return;
                } cd(cmd[1]);
            }
            case "rmdir" -> {
                if (cmd.length < 2) {
                    explorer.showError("Usage: rmdir <directory>");
                    return;
                } rmdir(cmd[1]);
            }
            case "touch" -> {
                if (cmd.length < 2) {
                    explorer.showError("Usage: touch <filename>");
                    return;
                } touch(cmd[1]);
            }
            case "nano" -> {
                if (cmd.length < 2) {
                    explorer.showError("Usage: nano <filename>");
                    return;
                } nano(cmd[1]);
            }
            case "rm" -> {
                if (cmd.length < 2) {
                    explorer.showError("Usage: rm <filename>");
                    return;
                } rm(cmd[1]);
            }
        }
    }

    public void open (String name) {
        if (name.equals("..") && currentDir.getParent() != null) {
            currentDir = currentDir.getParent();
            explorer.updateTable(currentDir);
            return;
        }
        SystemNode node = currentDir.findChild(name);
        if (node != null) {
            if (node.isDirectory()) {
                currentDir = (Directory) node;
                explorer.updateTable(currentDir);
            } else {
                Model.File file = (Model.File) node;
                explorer.showContent(file);
            } return;
        } explorer.showError("File or Directory not found: " + name);
    }

    private void mkdir(String dirName) {
        // Check if the directory name is valid
        if (currentDir.findChild(dirName) != null) {
            explorer.showError("Directory already exists: " + dirName);
            return;
        }

        // Validate directory name before creating
        else if (dirName.contains("/") || dirName.contains("\\") || dirName.equals("..")) {
            explorer.showError("Invalid directory name: " + dirName);
            return;
        }

        // Create a new directory with the current time as modified time
        Directory newDir = new Directory(
            dirName, currentDir,
            new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(new Date())
        );

        // Add the new directory to the current directory
        currentDir.addChild(newDir);
        explorer.updateTable(currentDir);
    }

    private void cd(String dirName) {
        // Check if the directory name is valid
        if (dirName.equals("..")) {
            if (currentDir.getParent() != null) {
                currentDir = currentDir.getParent();
                explorer.updateTable(currentDir);
            } else explorer.showError("Already at root directory!"); // Show error if trying to go above root
        }

        else { // Check if the directory exists in the current directory
            SystemNode node = currentDir.findChild(dirName);
            if (node != null && node.isDirectory()) {
                currentDir = (Directory) node; // Change current directory to the found directory
                explorer.updateTable(currentDir);
                return;
            } explorer.showError("Directory not found: " + dirName); // Show error if directory doesn't exist
        }
    }

    private void rmdir(String dirName) {
        // Check if directory exists
        SystemNode node = currentDir.findChild(dirName);
        if (node != null) {
            // Validate that it's a directory
            if (!node.isDirectory()) {
                explorer.showError("Not a directory: " + dirName);
                return;
            }

            // Check if directory is empty
            Directory dir = (Directory) node;
            if (dir.getChildren().isEmpty()) {
                currentDir.removeChild(node); // Remove the directory if it's empty
                explorer.updateTable(currentDir);
            } else explorer.showError("Directory not empty: " + dirName); // Show error if directory is not empty
        } else explorer.showError("Directory not found: " + dirName); // Show error if directory doesn't exist
    }

    private void touch (String fileName) {
        // Check if file already exists
        SystemNode node = currentDir.findChild(fileName);
        if (node != null && !node.isDirectory()) {
            Model.File file = (Model.File) node;
            file.setModifiedTime(new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(new Date())); // Update modified time
            explorer.updateTable(currentDir);
            return;
        }

        // Validate file name before creating
        if (fileName.contains("/") || fileName.contains("\\") || fileName.equals("..")) {
            explorer.showError("Invalid file name: " + fileName);
            return;
        }

        // If file doesn't exist, create a new one
        Model.File newFile = new Model.File(
            fileName, currentDir, "",
            new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(new Date()),
            diskManager.allocateContiguous("")
        );

        // Add the new file to the current directory
        currentDir.addChild(newFile);
        explorer.updateTable(currentDir);
    }

    private void nano(String fileName) {
        // Check if file already exists
        SystemNode node = currentDir.findChild(fileName);
        if (node != null && !node.isDirectory()) {
            Model.File file = (Model.File) node;
            explorer.showContent(file);
            return;
        }

        // Validate file name before creating
        if (fileName.contains("/") || fileName.contains("\\") || fileName.equals("..")) {
            explorer.showError("Invalid file name: " + fileName);
            return;
        }

        // If file doesn't exist, create a new one
        Model.File newFile = new Model.File(
            fileName, currentDir, "",
            new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(new Date()),
            diskManager.allocateContiguous("")
        );

        // Add the new file to the current directory
        currentDir.addChild(newFile);
        explorer.updateTable(currentDir);
        explorer.showContent(newFile); // Open the new file in the editor
    }

    private void rm(String fileName) {
        // Check if file exists
        SystemNode node = currentDir.findChild(fileName);
        if (node != null) {
            // Validate that it's a file, not a directory
            if (node.isDirectory()) {
                explorer.showError("Cannot remove directory: " + fileName);
                return;
            }

            // Remove the file
            currentDir.removeChild(node);
            explorer.updateTable(currentDir);
        } else explorer.showError("File not found: " + fileName); // Show error if file doesn't exist
    }
}

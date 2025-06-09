package Controller;

import Model.*;
import View.Explorer;

import javax.swing.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        if (currentDir.findChild(dirName) != null) {
            explorer.showError("Directory already exists: " + dirName);
            return;
        }
        Directory newDir = new Directory(dirName, currentDir, new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(new Date()));
        currentDir.addChild(newDir);
        explorer.updateTable(currentDir);
    }

    private void cd(String dirName) {
        if (dirName.equals("..")) {
            if (currentDir.getParent() != null) {
                currentDir = currentDir.getParent();
                explorer.updateTable(currentDir);
            } else explorer.showError("Already at root directory!");
        } else {
            SystemNode node = currentDir.findChild(dirName);
            if (node != null && node.isDirectory()) {
                currentDir = (Directory) node;
                explorer.updateTable(currentDir);
                return;
            } explorer.showError("Directory not found: " + dirName);
        }
    }

    private void rmdir(String dirName) {
        SystemNode node = currentDir.findChild(dirName);
        if (node != null) {
            if (!node.isDirectory()) {
                explorer.showError("Not a directory: " + dirName);
                return;
            }
            Directory dir = (Directory) node;
            if (dir.getChildren().isEmpty()) {
                currentDir.removeChild(node);
                explorer.updateTable(currentDir);
            } else explorer.showError("Directory not empty: " + dirName);
        } else explorer.showError("Directory not found: " + dirName);
    }

    private void touch (String fileName) {
        SystemNode node = currentDir.findChild(fileName);
        if (node != null && !node.isDirectory()) {
            Model.File file = (Model.File) node;
            file.setModifiedTime(new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(new Date()));
            explorer.updateTable(currentDir);
            return;
        }
        // If file doesn't exist, create a new one
        Model.File newFile = new Model.File(fileName, currentDir, "", new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(new Date()));
        currentDir.addChild(newFile);
        explorer.updateTable(currentDir);
    }

    private void nano(String fileName) {
        SystemNode node = currentDir.findChild(fileName);
        if (node != null && !node.isDirectory()) {
            Model.File file = (Model.File) node;
            explorer.showContent(file);
            return;
        }
        // If file doesn't exist, create a new one
        Model.File newFile = new Model.File(fileName, currentDir, "", new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(new Date()));
        currentDir.addChild(newFile);
        explorer.updateTable(currentDir);
        explorer.showContent(newFile); // Open the new file in the editor
    }

    private void rm(String fileName) {
        SystemNode node = currentDir.findChild(fileName);
        if (node != null) {
            if (node.isDirectory()) {
                explorer.showError("Cannot remove directory: " + fileName);
                return;
            }
            currentDir.removeChild(node);
            explorer.updateTable(currentDir);
        } else explorer.showError("File not found: " + fileName);
    }
}

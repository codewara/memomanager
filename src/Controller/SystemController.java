package Controller;

import Model.*;
import View.Explorer;

import javax.swing.*;
import java.io.File;

public class SystemController {
    private Directory root;
    private Explorer view;

    public void start (File json) {
        try {
            root = FileLoader.loadFromJSON(json);
            view = new Explorer(root);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to load directory structure", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

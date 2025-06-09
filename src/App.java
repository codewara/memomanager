import Controller.SystemController;

import java.io.File;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                File json = new File("src/resources/structure.json");
                new SystemController().start(json);
            } catch (Exception e) { e.printStackTrace(); }
        });
    }
}

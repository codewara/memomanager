import Controller.SystemController;

import java.io.File;
import java.util.Objects;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                File json = new File(Objects.requireNonNull(App.class.getClassLoader().getResource("resources/structure.json")).toURI());
                new SystemController().start(json);
            } catch (Exception e) { e.printStackTrace(); }
        });
    }
}

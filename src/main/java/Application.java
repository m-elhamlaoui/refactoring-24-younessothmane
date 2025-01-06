
import javax.swing.SwingUtilities;

import Views.MainFrame;

public class Application {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.start();
        });
    }
}

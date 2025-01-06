package Utils;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import java.util.Locale;

public class ResourceLoader {
    public static ResourceBundle loadMessages(String language) {
        try {
            String baseName = "messages";

            return ResourceBundle.getBundle("messages", new Locale("eng"));
        } catch (MissingResourceException e) {
            throw new RuntimeException("Missing resource bundle for language: " + language, e);
        }
    }
}

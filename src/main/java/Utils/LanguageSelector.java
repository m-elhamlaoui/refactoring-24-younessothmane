package Utils;

import javax.swing.*;

public class LanguageSelector {
    public static String selectLanguage() {
        Object[] options = {"English", "Français"};
        int choice = JOptionPane.showOptionDialog(
            null,
            "Select your language",
            "Language Selection",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            options,
            options[0]
        );

        return choice == 1 ? "fr" : "eng";
    }
}

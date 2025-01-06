package Utils;

import javax.swing.JOptionPane;

public class Validator {
    public static boolean validateString(String input, String fieldName) {
        if (input == null || input.trim().isEmpty()) {
            showError(fieldName + " cannot be empty.");
            return false;
        }
        return true;
    }

    public static boolean validateIntegerRange(int value, int min, int max, String fieldName) {
        if (value < min || value > max) {
            showError(fieldName + " must be between " + min + " and " + max + ".");
            return false;
        }
        return true;
    }

    public static boolean validatePositiveInteger(String input, String fieldName) {
        try {
            int value = Integer.parseInt(input);
            if (value < 0) {
                showError(fieldName + " must be a positive number.");
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            showError(fieldName + " must be a valid integer.");
            return false;
        }
    }

    public static boolean validateSelection(Object selectedItem, String fieldName) {
        if (selectedItem == null) {
            showError("Please select a " + fieldName + ".");
            return false;
        }
        return true;
    }

    private static void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }
}

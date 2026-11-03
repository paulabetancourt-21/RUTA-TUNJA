package co.edu.uptc.util;

import java.awt.Color;

public final class ColorUtil {

    private ColorUtil() {
    }

    public static Color fromHex(String value) {
        if (value == null || value.isBlank()) {
            return Color.BLACK;
        }
        try {
            return Color.decode(value.trim());
        } catch (NumberFormatException exception) {
            return Color.BLACK;
        }
    }
}

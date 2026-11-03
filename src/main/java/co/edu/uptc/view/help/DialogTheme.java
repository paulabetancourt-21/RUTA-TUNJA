package co.edu.uptc.view.help;

import co.edu.uptc.config.ConfigService;
import co.edu.uptc.util.ColorUtil;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.util.List;

public final class DialogTheme {

    private static final String PREFIX = "ui.dialog.";
    private final ConfigService config;

    public DialogTheme(ConfigService config) {
        this.config = config;
    }

    public Color accent() {
        return color("accentColor");
    }

    public Color text() {
        return color("textColor");
    }

    public Color muted() {
        return color("mutedColor");
    }

    public Color cardBackground() {
        return color("cardBackground");
    }

    public Color cardBorder() {
        return color("cardBorder");
    }

    public Font font(int style, int size) {
        String family = config.getString(PREFIX + "fontFamily");
        return new Font(family, style, size);
    }

    public Insets insets(String key) {
        List<String> values = config.getList(key);
        if (values.size() < 4) {
            return new Insets(0, 0, 0, 0);
        }
        return buildInsets(values);
    }

    public int intValue(String key) {
        return config.getInt(PREFIX + key);
    }

    private Insets buildInsets(List<String> values) {
        return new Insets(parse(values.get(0)), parse(values.get(1)),
                parse(values.get(2)), parse(values.get(3)));
    }

    private int parse(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    private Color color(String key) {
        return ColorUtil.fromHex(config.getString(PREFIX + key));
    }
}

package co.edu.uptc.logging;

import java.time.LocalDate;

public final class LogPaths {

    private LogPaths() {
    }

    public static String buildFileName(String pattern, LocalDate date) {
        return pattern.replace("yyyy", String.valueOf(date.getYear()))
                .replace("MM", String.format("%02d", date.getMonthValue()))
                .replace("dd", String.format("%02d", date.getDayOfMonth()));
    }

    public static boolean matches(String name, String pattern, String extension) {
        return name.startsWith(prefix(pattern)) && name.endsWith(extension);
    }

    public static String prefix(String pattern) {
        int index = pattern.indexOf("yyyy");
        return index > 0 ? pattern.substring(0, index) : "";
    }
}

package io.masterkun.ai.grpc;

/**
 * Utility class for string manipulation operations. Provides methods for formatting strings in a
 * consistent way.
 */
public class StringUtils {
    /**
     * Converts various string formats to a space-separated string. This method handles CamelCase,
     * snake_case, and kebab-case formats, converting them to a human-readable space-separated
     * format.
     *
     * @param str The input string to convert
     * @return A space-separated string
     */
    public static String toDescStr(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        // Replace underscores and hyphens with spaces
        String result = str.replace('_', ' ')
                .replace('-', ' ');

        // Insert space before uppercase letters (for CamelCase)
        result = result.replaceAll("([a-z])([A-Z])", "$1 $2");

        // Remove any extra spaces that might have been created
        result = result.replaceAll("\\s+", " ").trim();

        return result;
    }
}

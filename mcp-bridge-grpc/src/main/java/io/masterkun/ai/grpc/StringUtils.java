package io.masterkun.ai.grpc;

public class StringUtils {
    /**
     * 将CamelCase、下划线、中划线都转换为空格分隔
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

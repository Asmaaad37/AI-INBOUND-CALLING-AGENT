package com.example.rag_ingestion_app.util;

public class TextBuilderUtil {

    public static String buildText(String[] headers, String[] row) {
        StringBuilder text = new StringBuilder();

        // Loop through headers, but only go as far as the current row's length
        for (int i = 0; i < headers.length; i++) {
            if (i < row.length && row[i] != null && !row[i].trim().isEmpty()) {
                text.append(headers[i])
                        .append(": ")
                        .append(row[i].trim())
                        .append(". ");
            }
        }

        return text.toString();
    }
}
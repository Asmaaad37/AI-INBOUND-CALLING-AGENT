package com.example.rag_ingestion_app.service;

import com.example.rag_ingestion_app.util.TextBuilderUtil;
import com.opencsv.CSVReader;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileReader;
import java.util.List;

@Service
public class IngestionService {

    private final EmbeddingService embeddingService;
    private final PgVectorService pgVectorService;

    public IngestionService(EmbeddingService embeddingService, PgVectorService pgVectorService) {
        this.embeddingService = embeddingService;
        this.pgVectorService = pgVectorService;
    }

    public void ingestFolder(String folderPath) throws Exception {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();

        if (files == null) {
            System.err.println(" Folder not found: " + folder.getAbsolutePath());
            return;
        }

        for (File file : files) {
            if (!file.getName().endsWith(".csv")) continue;

            System.out.println("📂 Ingesting file: " + file.getName());

            try (CSVReader reader = new CSVReader(new FileReader(file))) {
                String[] headers = reader.readNext();
                if (headers == null) continue;

                String[] row;
                int count = 0;
                while ((row = reader.readNext()) != null) {
                    try {
                        String content = TextBuilderUtil.buildText(headers, row);
                        List<Float> embedding = embeddingService.generateEmbedding(content);
                        String category = detectCategory(file.getName());

                        pgVectorService.save(content, embedding, category, file.getName());
                        count++;
                    } catch (Exception e) {
                        System.err.println("   Failed row in " + file.getName() + ": " + e.getMessage());
                    }
                }
                System.out.println("Finished " + file.getName() + ". Total rows: " + count);
            }
        }
    }

    private String detectCategory(String fileName) {
        String name = fileName.toLowerCase();
        if (name.contains("fee")) return "fee";
        if (name.contains("program")) return "program";
        if (name.contains("faculty")) return "faculty";
        return "general";
    }
}
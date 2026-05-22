package com.example.rag_ingestion_app.runner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.example.rag_ingestion_app.service.IngestionService;

@Component
public class IngestionRunner implements CommandLineRunner {

    private final IngestionService ingestionService;

    public IngestionRunner(
            IngestionService ingestionService
    ) {
        this.ingestionService = ingestionService;
    }

    @Override
    public void run(String... args) throws Exception {

        ingestionService.ingestFolder("data");

        System.out.println("ALL CSV FILES INGESTED");
    }
}
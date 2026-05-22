package com.example.rag_ingestion_app.service;
import com.opencsv.CSVReader;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvReaderService {

    public List<String[]> readCsv(String filePath) throws Exception {

        List<String[]> rows = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {

            String[] line;

            reader.readNext(); // skip header

            while ((line = reader.readNext()) != null) {
                rows.add(line);
            }
        }

        return rows;
    }
}
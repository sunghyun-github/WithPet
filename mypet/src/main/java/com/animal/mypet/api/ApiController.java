package com.animal.mypet.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
public class ApiController {

    private final ApiService dataService;

    @Autowired
    public ApiController(ApiService dataService) {
        this.dataService = dataService;
    }

    @GetMapping("/fetchAndSaveData")
    public ResponseEntity<String> fetchAndSaveData() {
        try {
            dataService.fetchDataAndSave();
            return ResponseEntity.ok("Data fetched and saved successfully");
        } catch (Exception e) {
            log.error("Error fetching and saving data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch and save data: " + e.getMessage());
        }
    }

    @GetMapping("/getAllAnimals")
    public ResponseEntity<List<ApiEntity>> getAllAnimals() {
        try {
            List<ApiEntity> animals = dataService.getAllAnimals();
            return ResponseEntity.ok(animals);
        } catch (Exception e) {
            log.error("Error getting all animals", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

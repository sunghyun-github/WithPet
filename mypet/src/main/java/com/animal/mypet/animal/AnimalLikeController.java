package com.animal.mypet.animal;

import com.animal.mypet.api.ApiEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/animals")
public class AnimalLikeController {

    private static final Logger logger = LoggerFactory.getLogger(AnimalLikeController.class);
    private final AnimalLikeService animalLikeService;

    public AnimalLikeController(AnimalLikeService animalLikeService) {
        this.animalLikeService = animalLikeService;
    }

    @PostMapping("/{animalId}/like")
    public ResponseEntity<String> addLike(@PathVariable("animalId") Long animalId, @RequestParam("userIdx") Integer userIdx) {
        if (userIdx == null || userIdx <= 0) {
            logger.error("Invalid userIdx: " + userIdx);
            return ResponseEntity.badRequest().body("Invalid userIdx");
        }
        
        boolean added = animalLikeService.addLike(animalId, userIdx);
        if (added) {
            return ResponseEntity.ok("Like added successfully");
        } else {
            logger.error("Failed to add like for animalId: " + animalId + ", userIdx: " + userIdx);
            return ResponseEntity.badRequest().body("Failed to add like");
        }
    }

    @DeleteMapping("/{animalId}/like")
    public ResponseEntity<String> removeLike(@PathVariable("animalId") Long animalId, @RequestParam("userIdx") Integer userIdx) {
        if (userIdx == null || userIdx <= 0) {
            logger.error("Invalid userIdx: " + userIdx);
            return ResponseEntity.badRequest().body("Invalid userIdx");
        }

        boolean removed = animalLikeService.removeLike(animalId, userIdx);
        if (removed) {
            return ResponseEntity.ok("Like removed successfully");
        } else {
            logger.error("Failed to remove like for animalId: " + animalId + ", userIdx: " + userIdx);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/likes")
    public ResponseEntity<List<ApiEntity>> getUserLikes(@RequestParam("userIdx") Integer userIdx) {
        if (userIdx == null || userIdx <= 0) {
            logger.error("Invalid userIdx: " + userIdx);
            return ResponseEntity.badRequest().build();
        }

        try {
            List<ApiEntity> likedAnimals = animalLikeService.getUserLikes(userIdx);
            if (likedAnimals.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(likedAnimals);
        } catch (Exception e) {
            logger.error("Error getting user likes for userIdx: " + userIdx, e);
            return ResponseEntity.status(500).build();
        }
    }
}

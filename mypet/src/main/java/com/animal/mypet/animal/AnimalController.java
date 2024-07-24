package com.animal.mypet.animal;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.animal.mypet.api.ApiEntity;
import com.animal.mypet.api.ApiRepository;
import com.animal.mypet.api.ApiService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/animal")
public class AnimalController {

    private final ApiService apiService;
    private final ApiRepository apiRepository; // BoardRepository 추가

    public AnimalController(ApiService apiService, ApiRepository apiRepository) {
        this.apiService = apiService;
        this.apiRepository = apiRepository;
    }

    @GetMapping("/animalBoard")
    public String fetchData(@RequestParam(value = "type", required = false) String type, Model model) {
        List<ApiEntity> seoulAnimals = apiService.getSeoulAnimals();
        List<ApiEntity> gyeonggiAnimals = apiService.getGyeonggiAnimals();

        if (type != null) {
            seoulAnimals = filterSeoulAnimalsByType(seoulAnimals, type);
            gyeonggiAnimals = filterGyeonggiAnimalsByType(gyeonggiAnimals, type);
        }

        model.addAttribute("seoulAnimals", seoulAnimals);
        model.addAttribute("gyeonggiAnimals", gyeonggiAnimals);
        return "/animal/animalBoard";
    }

    private List<ApiEntity> filterSeoulAnimalsByType(List<ApiEntity> animals, String type) {
        if (type.equalsIgnoreCase("dog")) {
            return animals.stream().filter(animal -> animal.getSpcs() != null && animal.getSpcs().equalsIgnoreCase("dog")).collect(Collectors.toList());
        } else if (type.equalsIgnoreCase("cat")) {
            return animals.stream().filter(animal -> animal.getSpcs() != null && animal.getSpcs().equalsIgnoreCase("cat")).collect(Collectors.toList());
        }
        return animals;
    }

    private List<ApiEntity> filterGyeonggiAnimalsByType(List<ApiEntity> animals, String type) {
        if (type.equals("개")) {
            return animals.stream().filter(animal -> animal.getSpeciesNm() != null && animal.getSpeciesNm().contains("개")).collect(Collectors.toList());
        } else if (type.equals("고양이")) {
            return animals.stream().filter(animal -> animal.getSpeciesNm() != null && animal.getSpeciesNm().contains("고양이")).collect(Collectors.toList());
        } else if (type.equals("기타")) {
            return animals.stream().filter(animal -> animal.getSpeciesNm() != null && !animal.getSpeciesNm().contains("개") && !animal.getSpeciesNm().contains("고양이")).collect(Collectors.toList());
        }
        return animals;
    }

    @GetMapping("/animal-details/{animalNo}")
    public String showAnimalDetails(@PathVariable("animalNo") String animalNo, Model model) {
        if (animalNo == null || animalNo.isEmpty()) {
            return "animal/animalNotFound";
        }

        List<ApiEntity> seoulAnimals = apiService.getSeoulAnimals();
        Optional<ApiEntity> seoulAnimal = seoulAnimals.stream()
                .filter(animal -> animalNo.equals(animal.getAnimalNo()))
                .findFirst();

        List<ApiEntity> gyeonggiAnimals = apiService.getGyeonggiAnimals();
        Optional<ApiEntity> gyeonggiAnimal = gyeonggiAnimals.stream()
                .filter(animal -> animalNo.equals(animal.getAbdmIdntfyNo()))
                .findFirst();

        if (seoulAnimal.isPresent()) {
            model.addAttribute("animal", seoulAnimal.get());
            model.addAttribute("location", "Seoul");
        } else if (gyeonggiAnimal.isPresent()) {
            model.addAttribute("animal", gyeonggiAnimal.get());
            model.addAttribute("location", "Gyeonggi");
        } else {
            return "animal/animalNotFound";
        }

        return "animal/animal-details";
    }

    // 동물 등록 번호 검색 페이지 반환
    @GetMapping("/animalNum")
    public String showAnimalNumPage() {
        return "/animal/animalNum";
    }

    // animalBoard로 이동
    @GetMapping("/animal-details")
    public String showAnimalBoard() {
        return "/animal/animalBoard";
    }
    
    @GetMapping("/animalShelters")
    public String showAnimalShelters() {
    	return "/animal/animalShelters";
    }
    
    @GetMapping("{abdmId}")
    public ResponseEntity<ApiEntity> getAnimalByAbdmId(@PathVariable("abdmId") String abdmId) {
    	ApiEntity animal = apiRepository.findByAbdmIdntfyNoOrAnimalNo(abdmId, abdmId);
        if (animal != null) {
            return ResponseEntity.ok(animal);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    
}

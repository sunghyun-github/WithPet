package com.animal.mypet.animal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.animal.mypet.api.ApiEntity;
import com.animal.mypet.api.ApiService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/adopt")
public class AnimalAdoptionController {

    private final AnimalAdoptionService animalAdoptionService;
    private final ApiService apiService;

    @Autowired
    public AnimalAdoptionController(AnimalAdoptionService animalAdoptionService, ApiService apiService) {
        this.animalAdoptionService = animalAdoptionService;
        this.apiService = apiService;
    }

    @GetMapping("/adoptForm")
    public ModelAndView showAdoptionForm(@RequestParam("animalId") Long animalId) {
        ModelAndView mav = new ModelAndView("animal/animalAdoption");
        ApiEntity animal = apiService.getAnimalDetails(animalId);
        mav.addObject("animal", animal);
        mav.addObject("application", new AnimalAdoptionApplication()); // 빈 신청서 객체 추가
        return mav;
    }

    @PostMapping("/submit")
    public ModelAndView submitAdoptionApplication(
            @ModelAttribute @Valid AnimalAdoptionApplication application, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            ModelAndView mav = new ModelAndView("animal/animalAdoption");
            ApiEntity animal = apiService.getAnimalDetails(application.getAnimalId());
            mav.addObject("animal", animal);
            return mav;
        }

        boolean success = animalAdoptionService.processAdoptionApplication(application);

        if (success) {
            ModelAndView mav = new ModelAndView("animal/adoptionSuccess");
            mav.addObject("application", application); // 제출한 신청서의 내용을 전달
            return mav;
        } else {
            return new ModelAndView("animal/adoptionError");
        }
    }
    
    @GetMapping("/animalBoard")
    public ModelAndView showAnimalBoard() {
        // 실제로는 동물 리스트를 가져와서 모델에 추가해야 합니다.
        ModelAndView mav = new ModelAndView("animal/animalBoard");
        // 예: mav.addObject("animals", animalService.getAllAnimals());
        return mav;
    }
}

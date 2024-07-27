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
        mav.addObject("application", new AnimalAdoptionApplication()); // Add empty entity object
        return mav;
    }

    @PostMapping("/submit")
    public ModelAndView submitAdoptionApplication(
            @ModelAttribute @Valid AnimalAdoptionApplication application, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("animal/animalAdoption");
        }

        boolean success = animalAdoptionService.processAdoptionApplication(application);

        if (success) {
            return new ModelAndView("animal/adoptionSuccess");
        } else {
            return new ModelAndView("animal/adoptionError");
        }
    }
}

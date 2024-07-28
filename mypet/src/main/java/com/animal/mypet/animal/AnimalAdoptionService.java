package com.animal.mypet.animal;

import org.springframework.stereotype.Service;

@Service
public class AnimalAdoptionService {
 
    private final AnimalAdoptionRepository repository;

    public AnimalAdoptionService(AnimalAdoptionRepository repository) {
        this.repository = repository;
    }

    public boolean processAdoptionApplication(
           String animalNo, String abdmIdntfyNo,
            String applicantName, String applicantEmail, 
            String applicantPhone, String applicantAddress,
            String adoptionReason, String adoptionExperience) {
     
        try {
            AnimalAdoptionApplication animalAdoptionApplication = new AnimalAdoptionApplication();
            animalAdoptionApplication.setAnimalNo(animalNo);
            animalAdoptionApplication.setAbdmIdntfyNo(abdmIdntfyNo);
            animalAdoptionApplication.setApplicantName(applicantName);
            animalAdoptionApplication.setApplicantEmail(applicantEmail);
            animalAdoptionApplication.setApplicantPhone(applicantPhone);
            animalAdoptionApplication.setApplicantAddress(applicantAddress);
            animalAdoptionApplication.setAdoptionReason(adoptionReason);
            animalAdoptionApplication.setAdoptionExperience(adoptionExperience);

            repository.save(animalAdoptionApplication);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean processAdoptionApplication(AnimalAdoptionApplication application) {
        try {
            repository.save(application);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
   
}

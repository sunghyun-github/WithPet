package com.animal.mypet.animal;

import org.springframework.stereotype.Service;

@Service
public class AnimalAdoptionService {

    private final AnimalAdoptionRepository repository;

    public AnimalAdoptionService(AnimalAdoptionRepository repository) {
        this.repository = repository;
    }

    public boolean processAdoptionApplication(
            Long animalId, String animalNo, String abdmIdntfyNo,
            String applicantName, String applicantEmail, 
            String applicantPhone, String applicantAddress,
            String adoptionReason, String adoptionExperience) {

        try {
            AnimalAdoptionApplication application = new AnimalAdoptionApplication();
            application.setAnimalId(animalId);
            application.setAnimalNo(animalNo);
            application.setAbdmIdntfyNo(abdmIdntfyNo);
            application.setApplicantName(applicantName);
            application.setApplicantEmail(applicantEmail);
            application.setApplicantPhone(applicantPhone);
            application.setApplicantAddress(applicantAddress);
            application.setAdoptionReason(adoptionReason);
            application.setAdoptionExperience(adoptionExperience);

            repository.save(application);
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

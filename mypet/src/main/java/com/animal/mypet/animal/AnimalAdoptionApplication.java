package com.animal.mypet.animal;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "animalAdoption")
public class AnimalAdoptionApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long animalId;
    
    // 유기고유번호
    private String animalNo;
    private String abdmIdntfyNo;

    private String applicantName;
    private String applicantEmail;
    private String applicantPhone;
    private String applicantAddress;
    private String adoptionReason;
    
    @NotNull(message = "Please specify your adoption experience")
    private String adoptionExperience;
}

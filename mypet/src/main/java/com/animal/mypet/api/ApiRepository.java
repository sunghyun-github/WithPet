package com.animal.mypet.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApiRepository extends JpaRepository<ApiEntity, Long> {
//    Animal findByAnimalNo(String animalNo);
//    Animal findByAbdmIdntfyNo(String abdmIdntfyNo);
    Optional<ApiEntity> findByAbdmIdntfyNoOrAnimalNo(String abdmIdntfyNo, String animalNo);

}

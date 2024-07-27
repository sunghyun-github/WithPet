package com.animal.mypet.animal;

import com.animal.mypet.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnimalLikeRepository extends JpaRepository<AnimalLike, Long> {

    // 특정 사용자에 대한 좋아요 목록을 찾습니다.
    List<AnimalLike> findByUser(User user);

    // 특정 사용자와 동물 ID에 대해 이미 좋아요가 있는지 확인합니다.
    boolean existsByUserAndAnimalId(User user, Long animalId);
    
    // 동물 ID와 사용자 인덱스에 대해 좋아요를 찾습니다.
    Optional<AnimalLike> findByAnimalIdAndUserIdx(Long animalId, Integer userIdx);

    // 특정 사용자와 동물 ID에 대한 좋아요 목록을 찾습니다.
    List<AnimalLike> findByUserAndAnimalId(User user, Long animalId);
    
    // 동물 ID에 대한 모든 좋아요를 찾습니다.
    List<AnimalLike> findByAnimalId(Long animalId);

    // 사용자 인덱스에 대한 모든 좋아요를 찾습니다.
    List<AnimalLike> findByUserIdx(Integer userIdx);
}

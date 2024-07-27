package com.animal.mypet.animal;

import com.animal.mypet.api.ApiEntity;
import com.animal.mypet.api.ApiRepository;
import com.animal.mypet.user.User;
import com.animal.mypet.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AnimalLikeService {

    private final AnimalLikeRepository animalLikeRepository;
    private final UserRepository userRepository;
    private final ApiRepository apiRepository;

    public AnimalLikeService(AnimalLikeRepository animalLikeRepository, UserRepository userRepository, ApiRepository apiRepository) {
        this.animalLikeRepository = animalLikeRepository;
        this.userRepository = userRepository;
        this.apiRepository = apiRepository;
    }

    @Transactional(readOnly = true)
    public List<ApiEntity> getUserLikes(Integer userIdx) {
        // 사용자 정보를 찾습니다.
        Optional<User> userOptional = userRepository.findByUserIdx(userIdx);
        if (userOptional.isEmpty()) {
            return List.of(); // 사용자가 없으면 빈 리스트를 반환합니다.
        }

        User user = userOptional.get();
        // 사용자의 좋아요 정보를 가져옵니다.
        List<AnimalLike> likes = animalLikeRepository.findByUser(user);

        // 좋아요한 동물 정보를 ApiRepository를 통해 가져옵니다.
        return likes.stream()
                .map(like -> apiRepository.findByAbdmIdntfyNoOrAnimalNo(
                        like.getAnimalId().toString(), 
                        like.getAnimalId().toString()
                ))
                .filter(Optional::isPresent) // 존재하는 ApiEntity만 필터링합니다.
                .map(Optional::get) // Optional을 ApiEntity로 변환합니다.
                .collect(Collectors.toList()); // 리스트로 수집합니다.
    }

    @Transactional
    public boolean addLike(Long animalId, Integer userIdx) {
        // 사용자의 정보를 가져옵니다.
       System.out.println("@@@@@@@@@@@@@@@");
        Optional<User> userOptional = userRepository.findByUserIdx(userIdx);
        if (userOptional.isEmpty()) {
            return false; // 사용자 존재하지 않음
        }
        User user = userOptional.get();

        // 동물 정보 확인
        Optional<ApiEntity> apiEntityOptional = apiRepository.findByAbdmIdntfyNoOrAnimalNo(animalId.toString(), animalId.toString());
        if (apiEntityOptional.isEmpty()) {
            return false; // 동물 존재하지 않음
        }

        // 이미 좋아요가 있는지 확인
        boolean alreadyLiked = animalLikeRepository.existsByUserAndAnimalId(user, animalId);
        if (alreadyLiked) {
            return false; // 이미 좋아요가 있음
        }

        // 새로운 좋아요 추가
        AnimalLike animalLike = new AnimalLike();
        animalLike.setUser(user);
        animalLike.setAnimalId(animalId);
        animalLike.setUserIdx(userIdx); // userIdx 설정
        animalLikeRepository.save(animalLike);

        return true; // 성공적으로 추가됨
    }

    @Transactional
    public boolean removeLike(Long animalId, Integer userIdx) {
        Optional<User> userOptional = userRepository.findByUserIdx(userIdx);
        if (userOptional.isEmpty()) {
            return false; // 사용자 존재하지 않음
        }
        User user = userOptional.get();

        // 좋아요를 찾고 삭제
        List<AnimalLike> likes = animalLikeRepository.findByUserAndAnimalId(user, animalId);
        if (likes.isEmpty()) {
            return false; // 좋아요가 없음
        }

        animalLikeRepository.deleteAll(likes);
        return true; // 성공적으로 삭제됨
    }
}

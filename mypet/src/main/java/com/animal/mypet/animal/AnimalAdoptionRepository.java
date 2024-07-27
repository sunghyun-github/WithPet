package com.animal.mypet.animal;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AnimalAdoptionRepository extends JpaRepository<AnimalAdoptionApplication, Long> {
    // 추가적인 쿼리 메서드는 필요에 따라 정의할 수 있습니다.
}

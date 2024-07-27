package com.animal.mypet.animal;

import java.io.Serializable;
import java.util.Objects;

public class AnimalLikeId implements Serializable {
    private static final long serialVersionUID = 1L;


    private Integer userIdx;
    private Long animalId;

    public AnimalLikeId() {}

    public AnimalLikeId(Integer userIdx, Long animalId) {
        this.userIdx = userIdx;
        this.animalId = animalId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnimalLikeId that = (AnimalLikeId) o;
        return Objects.equals(userIdx, that.userIdx) && Objects.equals(animalId, that.animalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userIdx, animalId);
    }
}

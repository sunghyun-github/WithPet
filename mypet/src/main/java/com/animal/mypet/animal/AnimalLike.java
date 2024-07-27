package com.animal.mypet.animal;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import com.animal.mypet.api.ApiEntity;
import com.animal.mypet.user.User;

@Entity
@Getter
@Setter
@Table(name = "animalLike")
public class AnimalLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer userIdx;
    private Long animalId;

    @ManyToOne
    @JoinColumn(name = "userIdx", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "animalId", insertable = false, updatable = false)
    private ApiEntity animal;

    @Column(nullable = false)
    private LocalDateTime likedAt;

    @PrePersist
    protected void onCreate() {
        this.likedAt = LocalDateTime.now();
    }
}

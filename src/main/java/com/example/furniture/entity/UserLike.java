package com.example.furniture.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_likes")
@NoArgsConstructor
@AllArgsConstructor
public class UserLike {

    @EmbeddedId
    private UserLikeId id;

}
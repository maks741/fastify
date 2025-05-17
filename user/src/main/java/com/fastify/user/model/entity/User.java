package com.fastify.user.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "_user")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class User {

    @Id
    private Long id;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<Music> playlist;

    public User(Long id) {
        this.id = id;
        this.playlist = new ArrayList<>();
    }
}

package com.fastify.musicdownload.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "music")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Music {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String url;

    @ManyToOne
    private User user;

}

package com.fastify.user.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "music")
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString(exclude = "user")
@Builder
public class Music {

    @Id
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String videoId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private User user;
}

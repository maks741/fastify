package com.fastify.user.repository;

import com.fastify.user.model.entity.Music;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MusicRepository extends JpaRepository<Music, Long> {
    @Query(nativeQuery = true, value = "SELECT * FROM music WHERE user_id=:userId")
    List<Music> findAllByUserId(Long userId);
}

package com.example.roadmap.repository;

import com.example.roadmap.model.RoadMapItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoadMapItemRepository extends JpaRepository<RoadMapItem, Long> {

    List<RoadMapItem> findByStatus(String status);
    List<RoadMapItem> findByPriority(String priority);
    List<RoadMapItem> findByRoadMapId(Long roadMapId);

    // ✅ ИСПРАВЛЕНО: Добавлена аннотация @Query с JPQL
    @EntityGraph(attributePaths = {"tags"})
    @Query("SELECT DISTINCT r FROM RoadMapItem r")
    List<RoadMapItem> findAllWithTags();

    // ✅ Альтернатива через LEFT JOIN FETCH
    @Query("SELECT DISTINCT r FROM RoadMapItem r LEFT JOIN FETCH r.tags")
    List<RoadMapItem> findAllWithTagsFetchJoin();

    // ✅ Для комментариев
    @EntityGraph(attributePaths = {"comments"})
    @Query("SELECT DISTINCT r FROM RoadMapItem r")
    List<RoadMapItem> findAllWithComments();
}
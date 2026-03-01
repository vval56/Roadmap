package com.example.roadmap.repository;

import com.example.roadmap.model.RoadMapItem;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository for roadmap items.
 */
public interface RoadMapItemRepository extends JpaRepository<RoadMapItem, Long> {
  @EntityGraph(attributePaths = "tags")
  @Query("select i from RoadMapItem i")
  List<RoadMapItem> findAllWithTagsEntityGraph();

  @Query("select distinct i from RoadMapItem i left join fetch i.tags")
  List<RoadMapItem> findAllWithTagsFetchJoin();
}

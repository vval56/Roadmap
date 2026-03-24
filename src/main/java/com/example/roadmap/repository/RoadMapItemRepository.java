package com.example.roadmap.repository;

import com.example.roadmap.model.ItemStatus;
import com.example.roadmap.model.RoadMapItem;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoadMapItemRepository extends JpaRepository<RoadMapItem, Long> {
  @EntityGraph(attributePaths = "tags")
  @Query("select i from RoadMapItem i")
  List<RoadMapItem> findAllWithTagsEntityGraph();

  @Query("select distinct i from RoadMapItem i left join fetch i.tags")
  List<RoadMapItem> findAllWithTagsFetchJoin();

  @Query(
      """
          select distinct i
          from RoadMapItem i
          join i.roadMap r
          join r.owner o
          left join i.parentItem p
          left join i.tags t
          where (:ownerEmail = '' or lower(coalesce(o.email, '')) = lower(:ownerEmail))
            and (:roadMapTitle = '' or lower(coalesce(r.title, '')) like lower(concat('%', :roadMapTitle, '%')))
            and (:parentTitle = '' or lower(coalesce(p.title, '')) like lower(concat('%', :parentTitle, '%')))
            and (:tagName = '' or lower(coalesce(t.name, '')) = lower(:tagName))
            and (:status is null or i.status = :status)
          order by i.id
          """
  )
  List<RoadMapItem> searchByNestedFiltersJpql(
      @Param("ownerEmail") String ownerEmail,
      @Param("roadMapTitle") String roadMapTitle,
      @Param("parentTitle") String parentTitle,
      @Param("tagName") String tagName,
      @Param("status") ItemStatus status);

  @Query(
      value = """
          select distinct i
          from RoadMapItem i
          join i.roadMap r
          join r.owner o
          left join i.parentItem p
          left join i.tags t
          where (:ownerEmail = '' or lower(coalesce(o.email, '')) = lower(:ownerEmail))
            and (:roadMapTitle = '' or lower(coalesce(r.title, '')) like lower(concat('%', :roadMapTitle, '%')))
            and (:parentTitle = '' or lower(coalesce(p.title, '')) like lower(concat('%', :parentTitle, '%')))
            and (:tagName = '' or lower(coalesce(t.name, '')) = lower(:tagName))
            and (:status is null or i.status = :status)
          """,
      countQuery = """
          select count(distinct i.id)
          from RoadMapItem i
          join i.roadMap r
          join r.owner o
          left join i.parentItem p
          left join p.roadMap pr
          left join i.tags t
          where (:ownerEmail = '' or lower(coalesce(o.email, '')) = lower(:ownerEmail))
            and (:roadMapTitle = '' or lower(coalesce(r.title, '')) like lower(concat('%', :roadMapTitle, '%')))
            and (:parentTitle = '' or lower(coalesce(p.title, '')) like lower(concat('%', :parentTitle, '%')))
            and (:tagName = '' or lower(coalesce(t.name, '')) = lower(:tagName))
            and (:status is null or i.status = :status)
          """
  )
  Page<RoadMapItem> searchByNestedFiltersJpql(
      @Param("ownerEmail") String ownerEmail,
      @Param("roadMapTitle") String roadMapTitle,
      @Param("parentTitle") String parentTitle,
      @Param("tagName") String tagName,
      @Param("status") ItemStatus status,
      Pageable pageable);

  @Query(
      value = """
          select distinct i.*
          from roadmap_items i
          join roadmaps r on r.id = i.roadmap_id
          join app_users u on u.id = r.owner_id
          left join roadmap_items p on p.id = i.parent_item_id
          left join roadmaps pr on pr.id = p.roadmap_id
          left join roadmap_item_tag rit on rit.roadmap_item_id = i.id
          left join tags t on t.id = rit.tag_id
          where (:ownerEmail is null or lower(u.email) = lower(:ownerEmail))
            and (:roadMapTitle is null or lower(r.title) like lower(concat('%', :roadMapTitle, '%')))
            and (:parentTitle is null or lower(p.title) like lower(concat('%', :parentTitle, '%')))
            and (:tagName is null or lower(t.name) = lower(:tagName))
            and (:status is null or i.status = :status)
          order by i.id
          """,
      nativeQuery = true
  )
  List<RoadMapItem> searchByNestedFiltersNative(
      @Param("ownerEmail") String ownerEmail,
      @Param("roadMapTitle") String roadMapTitle,
      @Param("parentTitle") String parentTitle,
      @Param("tagName") String tagName,
      @Param("status") String status);

  @Query(
      value = """
          select distinct i.*
          from roadmap_items i
          join roadmaps r on r.id = i.roadmap_id
          join app_users u on u.id = r.owner_id
          left join roadmap_items p on p.id = i.parent_item_id
          left join roadmaps pr on pr.id = p.roadmap_id
          left join roadmap_item_tag rit on rit.roadmap_item_id = i.id
          left join tags t on t.id = rit.tag_id
          where (:ownerEmail is null or lower(u.email) = lower(:ownerEmail))
            and (:roadMapTitle is null or lower(r.title) like lower(concat('%', :roadMapTitle, '%')))
            and (:parentTitle is null or lower(p.title) like lower(concat('%', :parentTitle, '%')))
            and (:tagName is null or lower(t.name) = lower(:tagName))
            and (:status is null or i.status = :status)
          order by i.id
          """,
      countQuery = """
          select count(distinct i.id)
          from roadmap_items i
          join roadmaps r on r.id = i.roadmap_id
          join app_users u on u.id = r.owner_id
          left join roadmap_items p on p.id = i.parent_item_id
          left join roadmaps pr on pr.id = p.roadmap_id
          left join roadmap_item_tag rit on rit.roadmap_item_id = i.id
          left join tags t on t.id = rit.tag_id
          where (:ownerEmail is null or lower(u.email) = lower(:ownerEmail))
            and (:roadMapTitle is null or lower(r.title) like lower(concat('%', :roadMapTitle, '%')))
            and (:parentTitle is null or lower(p.title) like lower(concat('%', :parentTitle, '%')))
            and (:tagName is null or lower(t.name) = lower(:tagName))
            and (:status is null or i.status = :status)
          """,
      nativeQuery = true
  )
  Page<RoadMapItem> searchByNestedFiltersNative(
      @Param("ownerEmail") String ownerEmail,
      @Param("roadMapTitle") String roadMapTitle,
      @Param("parentTitle") String parentTitle,
      @Param("tagName") String tagName,
      @Param("status") String status,
      Pageable pageable);
}

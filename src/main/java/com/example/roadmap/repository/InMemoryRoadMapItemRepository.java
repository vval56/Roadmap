package com.example.roadmap.repository;

import com.example.roadmap.model.RoadMapItem;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

/**
 * In-memory implementation of RoadMapItemRepository.
 */
@Repository
public class InMemoryRoadMapItemRepository implements RoadMapItemRepository {

  private final Map<Long, RoadMapItem> items = new LinkedHashMap<>();
  private final AtomicLong idGenerator = new AtomicLong(0);

  /**
   * Creates repository and fills it with sample data.
   */
  public InMemoryRoadMapItemRepository() {
    save(new RoadMapItem(
        null,
        "Learn Spring Basics",
        "Finish core Spring Boot topics and REST controllers",
        "IN_PROGRESS",
        LocalDate.now().plusWeeks(2)
    ));
    save(new RoadMapItem(
        null,
        "Complete Java Collections",
        "Practice List, Set, Map and streams",
        "PLANNED",
        LocalDate.now().plusWeeks(1)
    ));
  }

  @Override
  public List<RoadMapItem> findAll() {
    List<RoadMapItem> result = new ArrayList<>();
    for (RoadMapItem item : items.values()) {
      result.add(copy(item));
    }
    return result;
  }

  @Override
  public Optional<RoadMapItem> findById(Long id) {
    RoadMapItem item = items.get(id);
    if (item == null) {
      return Optional.empty();
    }
    return Optional.of(copy(item));
  }

  @Override
  public List<RoadMapItem> findByStatus(String status) {
    List<RoadMapItem> result = new ArrayList<>();
    for (RoadMapItem item : items.values()) {
      if (item.getStatus().equalsIgnoreCase(status)) {
        result.add(copy(item));
      }
    }
    return result;
  }

  @Override
  public RoadMapItem save(RoadMapItem item) {
    if (item.getId() == null) {
      item.setId(idGenerator.incrementAndGet());
    }

    RoadMapItem copy = copy(item);
    items.put(copy.getId(), copy);
    return copy(copy);
  }

  @Override
  public void deleteById(Long id) {
    items.remove(id);
  }

  private RoadMapItem copy(RoadMapItem item) {
    return new RoadMapItem(
        item.getId(),
        item.getTitle(),
        item.getDescription(),
        item.getStatus(),
        item.getTargetDate()
    );
  }
}

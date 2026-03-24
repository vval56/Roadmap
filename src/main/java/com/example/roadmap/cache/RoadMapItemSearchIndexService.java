package com.example.roadmap.cache;

import com.example.roadmap.dto.RoadMapItemDto;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class RoadMapItemSearchIndexService {

  private static final Logger log = LoggerFactory.getLogger(RoadMapItemSearchIndexService.class);

  private final Map<RoadMapItemSearchKey, Page<RoadMapItemDto>> cache = new HashMap<>();

  public synchronized Optional<Page<RoadMapItemDto>> get(RoadMapItemSearchKey key) {
    Page<RoadMapItemDto> value = cache.get(key);
    if (value == null) {
      log.info("Search index MISS: key={}, size={}", key, cache.size());
      return Optional.empty();
    }
    log.info("Search index HIT: key={}, size={}", key, cache.size());
    return Optional.of(value);
  }

  public synchronized void put(RoadMapItemSearchKey key, Page<RoadMapItemDto> value) {
    cache.put(key, value);
    log.info("Search index STORE: key={}, size={}", key, cache.size());
  }

  public synchronized void invalidateAll() {
    log.info("Search index INVALIDATE_ALL: removedEntries={}", cache.size());
    cache.clear();
  }
}

package com.example.roadmap.cache;

import com.example.roadmap.dto.RoadMapItemDto;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class RoadMapItemSearchIndexService {

  private final Map<RoadMapItemSearchKey, Page<RoadMapItemDto>> cache = new HashMap<>();

  public synchronized Optional<Page<RoadMapItemDto>> get(RoadMapItemSearchKey key) {
    return Optional.ofNullable(cache.get(key));
  }

  public synchronized void put(RoadMapItemSearchKey key, Page<RoadMapItemDto> value) {
    cache.put(key, value);
  }

  public synchronized void invalidateAll() {
    cache.clear();
  }
}

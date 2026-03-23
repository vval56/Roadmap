package com.example.roadmap.service;

import com.example.roadmap.dto.TransactionDemoRequestDto;
import com.example.roadmap.exception.ResourceNotFoundException;
import com.example.roadmap.model.ItemStatus;
import com.example.roadmap.model.RoadMap;
import com.example.roadmap.model.RoadMapItem;
import com.example.roadmap.model.User;
import com.example.roadmap.repository.RoadMapItemRepository;
import com.example.roadmap.repository.RoadMapRepository;
import com.example.roadmap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionWorkerServiceImpl implements TransactionWorkerService {

  private final UserRepository userRepository;
  private final RoadMapRepository roadMapRepository;
  private final RoadMapItemRepository roadMapItemRepository;

  @Override
  public void saveWithoutTransactionalAndFail(TransactionDemoRequestDto requestDto) {
    User owner = getOwner(requestDto.getOwnerId());

    RoadMap roadMap = new RoadMap();
    roadMap.setTitle(requestDto.getRoadMapTitle());
    roadMap.setDescription("Scenario without @Transactional");
    roadMap.setOwner(owner);
    roadMapRepository.save(roadMap);

    RoadMapItem firstItem = new RoadMapItem();
    firstItem.setTitle(requestDto.getFirstItemTitle());
    firstItem.setDetails("Saved before exception");
    firstItem.setStatus(ItemStatus.IN_PROGRESS);
    firstItem.setRoadMap(roadMap);
    roadMapItemRepository.save(firstItem);

    throw new IllegalStateException("Forced error without @Transactional");
  }

  @Override
  @Transactional
  public void saveWithTransactionalAndFail(TransactionDemoRequestDto requestDto) {
    User owner = getOwner(requestDto.getOwnerId());

    RoadMap roadMap = new RoadMap();
    roadMap.setTitle(requestDto.getRoadMapTitle());
    roadMap.setDescription("Scenario with @Transactional");
    roadMap.setOwner(owner);
    roadMapRepository.save(roadMap);

    RoadMapItem firstItem = new RoadMapItem();
    firstItem.setTitle(requestDto.getFirstItemTitle());
    firstItem.setDetails("Will be rolled back");
    firstItem.setStatus(ItemStatus.PLANNED);
    firstItem.setRoadMap(roadMap);
    roadMapItemRepository.save(firstItem);

    RoadMapItem secondItem = new RoadMapItem();
    secondItem.setTitle(requestDto.getSecondItemTitle());
    secondItem.setDetails("Will be rolled back too");
    secondItem.setStatus(ItemStatus.PLANNED);
    secondItem.setRoadMap(roadMap);
    roadMapItemRepository.save(secondItem);

    throw new IllegalStateException("Forced error with @Transactional");
  }

  private User getOwner(Long ownerId) {
    return userRepository.findById(ownerId)
        .orElseThrow(() -> new ResourceNotFoundException(
            "User with id=" + ownerId + " not found"));
  }
}

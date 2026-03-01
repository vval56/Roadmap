package com.example.roadmap.bootstrap;

import com.example.roadmap.model.Comment;
import com.example.roadmap.model.ItemStatus;
import com.example.roadmap.model.RoadMap;
import com.example.roadmap.model.RoadMapItem;
import com.example.roadmap.model.Tag;
import com.example.roadmap.model.User;
import com.example.roadmap.repository.CommentRepository;
import com.example.roadmap.repository.RoadMapItemRepository;
import com.example.roadmap.repository.RoadMapRepository;
import com.example.roadmap.repository.TagRepository;
import com.example.roadmap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


/**
 * DataInitializer component.
 */
@Component
@Profile("!test")
@RequiredArgsConstructor
@Transactional
public class DataInitializer implements CommandLineRunner {

  private final UserRepository userRepository;
  private final TagRepository tagRepository;
  private final RoadMapRepository roadMapRepository;
  private final RoadMapItemRepository roadMapItemRepository;
  private final CommentRepository commentRepository;

  @Override
  public void run(String... args) {
    if (userRepository.count() > 0) {
      return;
    }

    User user = new User();
    user.setFullName("Vladislav Mogilny");
    user.setEmail("vladislav@example.com");
    user = userRepository.save(user);

    Tag springTag = new Tag();
    springTag.setName("spring");
    springTag = tagRepository.save(springTag);

    Tag sqlTag = new Tag();
    sqlTag.setName("sql");
    sqlTag = tagRepository.save(sqlTag);

    RoadMap roadMap = new RoadMap();
    roadMap.setTitle("Java Backend Roadmap");
    roadMap.setDescription("Preparation roadmap for semester labs");
    roadMap.setOwner(user);
    roadMap = roadMapRepository.save(roadMap);

    RoadMapItem firstItem = new RoadMapItem();
    firstItem.setTitle("Learn JPA basics");
    firstItem.setDetails("Entity mapping, repositories, relationships");
    firstItem.setStatus(ItemStatus.IN_PROGRESS);
    firstItem.setRoadMap(roadMap);
    firstItem.getTags().add(springTag);
    firstItem.getTags().add(sqlTag);
    firstItem = roadMapItemRepository.save(firstItem);

    Comment comment = new Comment();
    comment.setContent("Need extra practice with ManyToMany.");
    comment.setItem(firstItem);
    comment.setAuthor(user);
    commentRepository.save(comment);
  }
}

package com.example.roadmap.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class UserTest {

  @Test
  void syncFullNameShouldCombineTrimmedFirstAndLastNames() {
    User user = new User();
    user.setFirstName("  Vladislav ");
    user.setLastName(" Mogilny  ");

    user.syncFullName();

    assertEquals("Vladislav Mogilny", user.getFullName());
  }
}

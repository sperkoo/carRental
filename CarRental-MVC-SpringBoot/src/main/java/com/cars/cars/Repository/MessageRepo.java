// src/main/java/com/cars/cars/Repository/MessageRepo.java
package com.cars.cars.Repository;

import com.cars.cars.Model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepo extends JpaRepository<Message, Integer> {
    List<Message> findAllByOrderByIdAsc();
}
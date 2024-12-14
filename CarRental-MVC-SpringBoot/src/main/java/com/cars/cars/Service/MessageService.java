// src/main/java/com/cars/cars/Service/MessageService.java
package com.cars.cars.Service;

import com.cars.cars.Model.Message;
import com.cars.cars.Repository.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageRepo messageRepo;

    public List<Message> getAllMessages() {
        return messageRepo.findAllByOrderByIdAsc();
    }

    public List<Message> getMessagesByUserId(int userId) {
        return messageRepo.findByUserIdOrderByIdAsc(userId);
    }

    public void saveMessage(Message message) {
        messageRepo.save(message);
    }
}
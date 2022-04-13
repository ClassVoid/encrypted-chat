package com.example.ChatServer.repos;

import com.example.ChatServer.models.ChatKeysModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepoChatKeysModel extends JpaRepository<ChatKeysModel, Long> {
    void deleteAllByChat_Id(Long chatId);
}

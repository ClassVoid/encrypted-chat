package com.example.ChatServer.repos;

import com.example.ChatServer.models.ChatKeysModel;
import com.example.ChatServer.models.ChatModel;
import com.example.ChatServer.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepoChatKeysModel extends JpaRepository<ChatKeysModel, Long> {
    void deleteAllByChat_Id(Long chatId);
    Optional<ChatKeysModel> getChatKeysModelByChatAndUser(ChatModel chatModel, UserModel userModel);
}

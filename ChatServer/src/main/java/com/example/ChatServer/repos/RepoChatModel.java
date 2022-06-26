package com.example.ChatServer.repos;

import com.example.ChatServer.models.ChatModel;
import com.example.ChatServer.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepoChatModel extends JpaRepository<ChatModel, Long> {
    Optional<ChatModel> findChatModelByChatName(String chatName);
    void deleteAllByOwner(UserModel userModel);
}

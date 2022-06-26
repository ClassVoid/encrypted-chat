package com.example.ChatServer.repos;

import com.example.ChatServer.models.ChatModel;
import com.example.ChatServer.models.MessagesModel;
import com.example.ChatServer.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RepoMessageModel extends JpaRepository<MessagesModel, Long> {
    Optional<List< MessagesModel>> findMessagesModelsByDateAfterAndChat(LocalDateTime dateTime, ChatModel chatModel);
    void deleteAllByChat_Id(Long chatId);
    void deleteAllByUser(UserModel userModel);
}

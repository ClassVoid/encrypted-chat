package com.example.ChatServer.repos;

import com.example.ChatServer.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepoUserModel extends JpaRepository<UserModel, Long> {
    Optional<UserModel> findUserModelByUsername(String username);
}

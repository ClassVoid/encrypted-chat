package com.example.ChatServer.services;

import com.example.ChatServer.data.UserData;
import com.example.ChatServer.models.UserModel;
import com.example.ChatServer.repos.RepoUserModel;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final RepoUserModel repoUserModel;

    public ResponseEntity<?> createUser(UserData userData){

        // Validate user data
        if(userData!=null && userData.getUsername()!=null && userData.getUsername().length()>0 &&
                userData.getPubKey()!=null && userData.getPubKey().length()>0
                && userData.getEncryptedPriKey()!=null && userData.getEncryptedPriKey().length()>0) {
            // Check to see if the username is unique
            Optional<UserModel> optionalUserModel=repoUserModel.findUserModelByUsername(userData.getUsername());

            if(optionalUserModel.isEmpty()) {
                // Just save the user data, nothing more
                UserModel userModel = new UserModel(0L, userData.getUsername(), userData.getPubKey(), userData.getEncryptedPriKey());
                repoUserModel.save(userModel);
                return new ResponseEntity<>(HttpStatus.CREATED);
            }else
                return new ResponseEntity<>("ERROR, USERNAME ALREADY TAKEN",HttpStatus.CONFLICT);
        }
            return new ResponseEntity<>("ERROR, INCORRECT USER CREDENTIALS",HttpStatus.NOT_ACCEPTABLE);
    }

    public ResponseEntity<?> getPublicKey(String username){
        // Validate
        if(username!=null && username.length()>0){
            Optional<UserModel> optionalUserModel=repoUserModel.findUserModelByUsername(username);

            return optionalUserModel.map(userModel -> new ResponseEntity<>(userModel.getPubKey(), HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>("ERROR, USERNAME NOT FOUND", HttpStatus.NOT_FOUND));
        }else
            return new ResponseEntity<>("ERROR, USERNAME IS NULL",HttpStatus.BAD_REQUEST);
    }
}

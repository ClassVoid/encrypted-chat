package com.example.ChatServer.services;

import com.example.ChatServer.data.UserData;
import com.example.ChatServer.models.ChatModel;
import com.example.ChatServer.models.UserModel;
import com.example.ChatServer.repos.RepoUserModel;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public ResponseEntity<?> getUserData(String username){
        if(username!=null && username.length()>0){
            Optional<UserModel> optionalUserModel=repoUserModel.findUserModelByUsername(username);

            if(optionalUserModel.isEmpty())
                return new ResponseEntity<>("ERROR, USERNAME NOT FOUND", HttpStatus.NOT_FOUND);

            UserModel userModel=optionalUserModel.get();
            return new ResponseEntity<>(new UserData(userModel.getUsername(), userModel.getPubKey(), userModel.getEncryptedPriKey()), HttpStatus.OK);
        }else
            return new ResponseEntity<>("ERROR, USERNAME IS NULL",HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<?> getUserChats(String username){
        if(username!=null && username.length()>0){
            Optional<UserModel> optionalUserModel=repoUserModel.findUserModelByUsername(username);

            if(optionalUserModel.isEmpty())
                return new ResponseEntity<>("ERROR, USERNAME NOT FOUND", HttpStatus.NOT_FOUND);

            UserModel userModel=optionalUserModel.get();

            List<ChatModel> chatModelList=userModel.getChats();
            List<String> chatNamesList=chatModelList.stream().sequential().map(ChatModel::getChatName).collect(Collectors.toList());

            return new ResponseEntity<>(chatNamesList, HttpStatus.OK);

        }else
            return new ResponseEntity<>("ERROR, USERNAME IS NULL", HttpStatus.BAD_REQUEST);
    }
}

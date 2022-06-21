package com.example.ChatServer.services;

import com.example.ChatServer.data.UserEncrMessage;
import com.example.ChatServer.data.UserPubKeyData;
import com.example.ChatServer.models.UserModel;
import com.example.ChatServer.repos.RepoUserModel;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class IdentityService {
    private final RepoUserModel repoUserModel;
    private final RestTemplate restTemplate;

    public IdentityService(RestTemplate restTemplate, RepoUserModel repoUserModel){
        this.restTemplate=restTemplate;
        this.repoUserModel=repoUserModel;
    }

    public ResponseEntity<?> checkSecret(UserEncrMessage userEncrMessage)
    {
        HttpHeaders httpHeaders= new HttpHeaders();
        HttpEntity<UserEncrMessage> httpEntity= new HttpEntity<>(userEncrMessage, httpHeaders);
        return restTemplate.exchange("http://127.0.0.1:8081/api/validation-test", HttpMethod.POST, httpEntity, String.class);
    }

    public ResponseEntity<?> getTicket(String username){

        if(username!=null && username.length()>0) {
            Optional<UserModel> optionalUserModel = repoUserModel.findUserModelByUsername(username);

            if(optionalUserModel.isEmpty())
                return new ResponseEntity<>("ERROR, USERNAME "+username+" NOT FOUND",HttpStatus.NOT_FOUND);

            UserModel userModel= optionalUserModel.get();
            HttpHeaders httpHeaders = new HttpHeaders();
            UserPubKeyData userPubKeyData = new UserPubKeyData();
            userPubKeyData.setUsername(username);
            userPubKeyData.setPubKey(userModel.getPubKey());
            HttpEntity<UserPubKeyData> httpEntity = new HttpEntity<UserPubKeyData>(userPubKeyData, httpHeaders);

            return restTemplate.exchange("http://127.0.0.1:8081/api/generate-secret", HttpMethod.POST, httpEntity, String.class);
        } else
            return new ResponseEntity<>("ERROR, INCORRECT USERNAME",HttpStatus.BAD_REQUEST);
    }

}

package com.example.ChatServer.services;

import com.example.ChatServer.data.MessageData;
import com.example.ChatServer.data.MessageUploadData;
import com.example.ChatServer.models.ChatModel;
import com.example.ChatServer.models.MessagesModel;
import com.example.ChatServer.models.UserModel;
import com.example.ChatServer.repos.RepoChatModel;
import com.example.ChatServer.repos.RepoMessageModel;
import com.example.ChatServer.repos.RepoUserModel;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MessageService {
    private final RepoChatModel repoChatModel;
    private final RepoMessageModel repoMessageModel;
    private final RepoUserModel repoUserModel;


    // Returns all the messages from a date to present
    public ResponseEntity<?> getMessages(String chatName, LocalDateTime dateTime)
    {
        if(chatName==null || chatName.length()==0)
            return new ResponseEntity<>("ERROR, INCORRECT CHAT NAME",HttpStatus.BAD_REQUEST);

        Optional<ChatModel> optionalChatModel= repoChatModel.findChatModelByChatName(chatName);

        if(optionalChatModel.isPresent())
        {
            // I can search for the messages at the db level or at the app level
            //let's try db level
            Optional<List<MessagesModel>> optionalMessageModels=
                    repoMessageModel.findMessagesModelsByDateAfterAndChat(dateTime, optionalChatModel.get());

            if(optionalMessageModels.isPresent()){
                List<MessagesModel> messagesModels=optionalMessageModels.get();

                List<MessageData> messageData=messagesModels.stream()
                        .map(elem ->
                    new MessageData(elem.getUser().getUsername(), elem.getEncryptedMsg(), elem.getDate()))
                        .collect(Collectors.toList());

                return new ResponseEntity<List<MessageData>>(messageData, HttpStatus.OK);
            } else {
                //Return an empty list
                return new ResponseEntity<List<MessageData>>(new ArrayList<MessageData>(), HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>("ERROR, CHAT "+chatName+" NOT FOUND",HttpStatus.NOT_FOUND);
        }
    }


    public ResponseEntity<?> uploadMessage(MessageUploadData messageUploadData){

        Optional<ChatModel> optionalChatModel;
        Optional<UserModel> optionalUserModel;
        // Validate the author name and chat name
        if(messageUploadData.getEncryptedMsg()!=null || messageUploadData.getEncryptedMsg().length()>0) {

            if (messageUploadData.getChatName() != null || messageUploadData.getChatName().length()>0)
                optionalChatModel = repoChatModel.findChatModelByChatName(messageUploadData.getChatName());
            else
                return new ResponseEntity<>("ERROR, CHAT NAME IS NULL",HttpStatus.NOT_ACCEPTABLE);

            if (messageUploadData.getAuthorName() != null && messageUploadData.getAuthorName().length()>0)
                optionalUserModel = repoUserModel.findUserModelByUsername(messageUploadData.getAuthorName());
            else
                return new ResponseEntity<>("ERROR, AUTHOR NAME IS NULL",HttpStatus.NOT_ACCEPTABLE);

            if (optionalUserModel.isPresent() && optionalChatModel.isPresent()) {
                LocalDateTime dateTime=LocalDateTime.now();
                MessagesModel messagesModel=new MessagesModel(0L, messageUploadData.getEncryptedMsg(), dateTime);
                messagesModel.setUser(optionalUserModel.get());
                messagesModel.setChat(optionalChatModel.get());
                repoMessageModel.save(messagesModel);
                return new ResponseEntity<>(HttpStatus.CREATED);
            }
            else
                return new ResponseEntity<>("ERROR, USER/CHAT COULD NOT BE FOUND",HttpStatus.NOT_FOUND);
        }
        else
            return new ResponseEntity<>("ERROR, ENCRYPTED MESSAGE IS NULL",HttpStatus.NOT_ACCEPTABLE);
    }


}

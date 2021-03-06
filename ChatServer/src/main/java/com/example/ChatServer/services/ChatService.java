package com.example.ChatServer.services;

import com.example.ChatServer.data.AddUserData;
import com.example.ChatServer.data.AddUsersData;
import com.example.ChatServer.data.UserEncrMessage;
import com.example.ChatServer.models.ChatKeysModel;
import com.example.ChatServer.models.ChatModel;
import com.example.ChatServer.models.MessagesModel;
import com.example.ChatServer.models.UserModel;
import com.example.ChatServer.repos.RepoChatKeysModel;
import com.example.ChatServer.repos.RepoChatModel;
import com.example.ChatServer.repos.RepoMessageModel;
import com.example.ChatServer.repos.RepoUserModel;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ChatService {
    private final RepoChatModel repoChatModel;
    private final RepoUserModel repoUserModel;
    private final RepoMessageModel repoMessageModel;
    private final RepoChatKeysModel repoChatKeysModel;
    private final IdentityService identityService;


    public ResponseEntity<?> createChat(String chatName, String owner, UserEncrMessage userEncrMessage){

        // validate identity
        ResponseEntity<?> responseEntity=identityService.checkSecret(userEncrMessage);
        if(responseEntity.getStatusCode()!=HttpStatus.OK)
            return responseEntity;

        // Validate data
        if(chatName!=null && chatName.length()>0 && owner!=null && owner.length()>0){

            if(chatName.length()>20 || owner.length()>20)
                return new ResponseEntity<>("ERROR, CHAT/OWNER NAME EXCEEDS THE SIZE LIMIT", HttpStatus.INSUFFICIENT_STORAGE);

            Optional<UserModel> optionalUserModel=repoUserModel.findUserModelByUsername(owner);

            if(optionalUserModel.isEmpty())
                return new ResponseEntity<>("ERROR, THE OWNER "+owner+" IS NOT REGISTERED", HttpStatus.NOT_FOUND);

            UserModel userModel=optionalUserModel.get();

            ChatModel chatModel=new ChatModel(0L, chatName);
            Optional<ChatModel> optionalChatModel= repoChatModel.findChatModelByChatName(chatName);
            if(optionalChatModel.isEmpty()) {

                chatModel= repoChatModel.save(chatModel);
                chatModel.setOwner(userModel);
                repoChatModel.save(chatModel);
                /*
                userModel.getChats().add(chatModel);
                userModel.getChatsOwned().add(chatModel);
                chatModel.getUsers().add(userModel);
                chatModel.setOwner(userModel);
                repoUserModel.save(userModel);

                 */

                return new ResponseEntity<>(HttpStatus.CREATED);
            }else{
                return new ResponseEntity<String>("ERROR, CHAT NAME IS TAKEN",HttpStatus.CONFLICT);
            }
        }
        else
            return new ResponseEntity<String>("ERROR, INVALID CHAT/OWNER NAME",HttpStatus.BAD_REQUEST);
    }

    // You can add multiple users
    public ResponseEntity<?> addUsers(String chatName, AddUsersData addUsersData){
        // Validate
        if(chatName!=null && chatName.length()>0 &&
                addUsersData!=null && addUsersData.getAddUserDataList()!=null){

            // Check if this user has the privilege
            if(addUsersData.getUserEncrMessage()==null)
                return new ResponseEntity<>("ERROR, NON EXISTENT PRIORITY TICKET",HttpStatus.NOT_ACCEPTABLE);

            UserEncrMessage userEncrMessage=addUsersData.getUserEncrMessage();
            ResponseEntity<?> validationResponse=identityService.checkSecret(userEncrMessage);
            if(validationResponse.getStatusCode()!=HttpStatus.OK)
                return validationResponse;

            // Check if the chat name is existent
            Optional<ChatModel> optionalChatModel=repoChatModel.findChatModelByChatName(chatName);
            if(optionalChatModel.isEmpty())
                return new ResponseEntity<String>("ERROR, INCORRECT CHAT NAME",HttpStatus.NOT_FOUND);

            // Check if every user exists and add it to a list
            List<UserModel> userModelList=new ArrayList<>();
            for (AddUserData addUserData: addUsersData.getAddUserDataList()) {
                Optional<UserModel> optionalUserModel=repoUserModel.findUserModelByUsername(addUserData.getUsername());

                if(optionalUserModel.isEmpty())
                    return new ResponseEntity<>("ERROR, USER "+addUserData.getUsername()+" NOT FOUND",HttpStatus.NOT_FOUND);

                userModelList.add(optionalUserModel.get());
            }

            // Iterate through the users and add them to the chat and update their keys
            ChatModel chatModel=optionalChatModel.get();
            for(int i=0; i<userModelList.size();i++){
                // Check to see if the user is already added
                if(!userModelList.get(i).getChats().contains(chatModel)) {
                    // Bind chat and user
                    userModelList.get(i).getChats().add(chatModel);
                    chatModel.getUsers().add(userModelList.get(i));
                    repoUserModel.save(userModelList.get(i));
                    // Add the key
                    ChatKeysModel chatKeysModel = new ChatKeysModel(0L, addUsersData.getAddUserDataList().get(i).getEncryptedKey());
                    chatKeysModel.setUser(userModelList.get(i));
                    chatKeysModel.setChat(chatModel);
                    repoChatKeysModel.save(chatKeysModel);
                }
            }
            return new ResponseEntity<>(HttpStatus.CREATED);
        }else
            return new ResponseEntity<>("ERROR, INCORRECT REQUEST",HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<?> getChatOwner(String chatName){
        Optional<ChatModel> optionalChatModel= repoChatModel.findChatModelByChatName(chatName);

        if(optionalChatModel.isEmpty())
            return new ResponseEntity<>("ERROR, THE "+chatName+" CHAT CANNOT BE FOUND", HttpStatus.NOT_FOUND);

        ChatModel chatModel=optionalChatModel.get();
        UserModel userModel= chatModel.getOwner();

        return new ResponseEntity<>(userModel.getUsername(), HttpStatus.OK);
    }
    @Transactional
    public ResponseEntity<?> deleteChat(String chatName, UserEncrMessage userEncrMessage){
        // Check to see if the user has the privilege to delete the chat

        ResponseEntity<?> responseEntity=identityService.checkSecret(userEncrMessage);
        if(responseEntity.getStatusCode()!=HttpStatus.OK)
            return responseEntity;


        if(chatName==null || chatName.length()==0)
            return new ResponseEntity<>("ERROR, INVALID CHAT NAME", HttpStatus.BAD_REQUEST);

        Optional<ChatModel> optionalChatModel=repoChatModel.findChatModelByChatName(chatName);
        if(optionalChatModel.isEmpty())
            return new ResponseEntity<>("ERROR, THE CHAT "+chatName+" CANNOT BE FOUND", HttpStatus.NOT_FOUND);

        ChatModel chatModel=optionalChatModel.get();

        /* Deletion of the chat needs to be done in the correct order
            - Messages
            - Keys
            - Users
            - Chat itself
         */

        // Message deletion
        List<MessagesModel> messagesModelList=chatModel.getMessages();
        chatModel.getMessages().removeAll(messagesModelList);
        repoMessageModel.deleteAllByChat_Id(chatModel.getId());

        // Key deletion
        List<ChatKeysModel> chatKeysModelList=chatModel.getChatKeys();
        chatModel.getChatKeys().removeAll(chatKeysModelList);
        repoChatKeysModel.deleteAllByChat_Id(chatModel.getId());

        // User deletion
        List<UserModel> userModelList=chatModel.getUsers();
        chatModel.getUsers().removeAll(userModelList);

        // Delete the chat
        repoChatModel.delete(chatModel);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    public ResponseEntity<?> getChatKey(String chatName, String username){
        // Validate
        if(chatName!=null && username!=null && chatName.length()>0 && username.length()>0){
            Optional<UserModel> optionalUserModel=repoUserModel.findUserModelByUsername(username);

            if(optionalUserModel.isEmpty())
                return new ResponseEntity<>("ERROR, USER NOT FOUND", HttpStatus.NOT_FOUND);

            Optional<ChatModel> optionalChatModel=repoChatModel.findChatModelByChatName(chatName);

            if(optionalChatModel.isEmpty())
                return new ResponseEntity<>("ERROR, CHAT NOT FOUND",HttpStatus.NOT_FOUND);


            Optional<ChatKeysModel> optionalChatKeysModel=repoChatKeysModel
                    .getChatKeysModelByChatAndUser(optionalChatModel.get(), optionalUserModel.get());

            if(optionalChatKeysModel.isEmpty())
                return new ResponseEntity<>("ERROR, KEY NOT FOUND",HttpStatus.NOT_FOUND);

            return new ResponseEntity<>(optionalChatKeysModel.get().getEncryptedKey(),HttpStatus.OK);
        }
        return new ResponseEntity<>("ERROR, INVALID CHAT/USERNAME",HttpStatus.NOT_ACCEPTABLE);
    }

    public ResponseEntity<?> getChatUsers(String chatName, UserEncrMessage userEncrMessage){
        ResponseEntity<?> responseEntity=identityService.checkSecret(userEncrMessage);
        if(responseEntity.getStatusCode()==HttpStatus.OK){

            if(chatName!=null && chatName.length()>0){
                 // verific daca exista chatul cu numele respectiv
                Optional<ChatModel> optionalChatModel=repoChatModel.findChatModelByChatName(chatName);

                if(optionalChatModel.isPresent()) {
                    // extrag numele utilizatorilor din acel chat intr-o lista
                    ChatModel chatModel=optionalChatModel.get();
                    List<String> chatUsers=chatModel.getUsers().stream().map(UserModel::getUsername).collect(Collectors.toList());
                    // verific daca utilizatorul face parte din lista
                    if(!chatUsers.contains(userEncrMessage.getUsername()))
                        return new ResponseEntity<>("ERROR, YOU MUST BE PART OF THE CHAT", HttpStatus.FORBIDDEN);
                    // trimit lista
                    return new ResponseEntity<List<String>>(chatUsers, HttpStatus.OK);
                }
                else
                    return new ResponseEntity<>("ERROR, CHAT NOT FOUND", HttpStatus.NOT_FOUND);
            }
            else
                return new ResponseEntity<>("ERROR, CHAT NAME IS NULL", HttpStatus.NOT_ACCEPTABLE);
        }
        else return responseEntity;
    }
}

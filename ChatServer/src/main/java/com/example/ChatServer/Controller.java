package com.example.ChatServer;

import com.example.ChatServer.data.AddUsersData;
import com.example.ChatServer.data.MessageUploadData;
import com.example.ChatServer.data.UserData;
import com.example.ChatServer.data.UserEncrMessage;
import com.example.ChatServer.services.ChatService;
import com.example.ChatServer.services.IdentityService;
import com.example.ChatServer.services.MessageService;
import com.example.ChatServer.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@AllArgsConstructor
public class Controller {
    private final MessageService messageService;
    private final UserService userService;
    private final ChatService chatService;
    private final IdentityService identityService;

    

    @GetMapping("/api/{chatName}/owner")
    ResponseEntity<?> getChatOwner(@PathVariable("chatName") String chatName){
        return chatService.getChatOwner(chatName);
    }

    /* Messages:
            -Get the latest messages (old_date, present_date)
            -Upload a message
            -Delete a message?? hard to prove you are the author
     */
    // Functional
    @GetMapping("/api/messages")
    ResponseEntity<?> getMessages(
            @RequestParam(name="chat_name") String chatName,
            @RequestParam(name = "date_time")String dateTimeString// all messages from dateTime to present
            ){
        LocalDateTime dateTime=LocalDateTime.parse(dateTimeString);
        return messageService.getMessages(chatName, dateTime);
    }

    // Functional
    @PostMapping("/api/messages")
    ResponseEntity<?> uploadMessage(@RequestBody MessageUploadData messageUploadData)
    {
        return messageService.uploadMessage(messageUploadData);
    }


    /*
        Users:
            -Create new user
            -Get a user's public key
     */

    // Functional
    @PostMapping("/api/users")
    ResponseEntity<?> createUser(@RequestBody UserData userData){
        return userService.createUser(userData);
    }

    // Functional
    @GetMapping("/api/users/{username}")
    ResponseEntity<?> getPublicKey(@PathVariable("username") String username){
        return userService.getPublicKey(username);
    }

    /*
        Chats:
            -Create a chat between you and other users
            -Add users to the chat
            -Remove a user from chat
     */

    // Functional
    @PostMapping("/api/chats/{chatName}/{username}")
    ResponseEntity<?> createChat(@PathVariable("chatName") String chatName,
                                 @PathVariable("username") String owner){
        return chatService.createChat(chatName, owner);
    }

    // Functional
    @PostMapping("/api/chats/{chatName}/users")
    ResponseEntity<?> addUsers(@PathVariable("chatName") String chatName,
                               @RequestBody AddUsersData addUsersData){
        return chatService.addUsers(chatName, addUsersData);
    }
    // Functional
    @DeleteMapping("/api/chats/{chatName}")
    ResponseEntity<?> deleteChat(@PathVariable("chatName") String chatName,
                                 @RequestBody UserEncrMessage userEncrMessage){
        return chatService.deleteChat(chatName, userEncrMessage);
    }
    // Functional
    @GetMapping("/api/ticket/{username}")
    ResponseEntity<?> getTicket(@PathVariable("username") String username){
        return identityService.getTicket(username);
    }
}

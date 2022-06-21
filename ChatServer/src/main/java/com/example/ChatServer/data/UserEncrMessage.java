package com.example.ChatServer.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/*
    This data structure is used to validate a user identity
    it contains the username along with the
    encrypted secret for the identity server
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEncrMessage {
    private String username;
    private String encryptedMsg;
}

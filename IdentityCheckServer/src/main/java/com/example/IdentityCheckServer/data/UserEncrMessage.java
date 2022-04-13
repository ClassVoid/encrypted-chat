package com.example.IdentityCheckServer.data;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
    The user will send his username along with the secret message encrypted using
    this server's public key
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEncrMessage {
    private String username;
    private String encryptedMsg;
}

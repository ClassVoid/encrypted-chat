package com.example.ChatServer.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserData {
    private String username;
    private String pubKey;
    private String encryptedPriKey;
}


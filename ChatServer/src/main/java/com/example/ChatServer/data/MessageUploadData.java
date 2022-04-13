package com.example.ChatServer.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageUploadData {
    private String authorName;
    private String chatName;
    private String encryptedMsg;
}

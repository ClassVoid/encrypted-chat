package com.example.ChatServer.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageData {
    private String author;
    private String encryptedMsg;
    private LocalDateTime date;
}

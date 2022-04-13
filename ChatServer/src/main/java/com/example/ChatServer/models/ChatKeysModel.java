package com.example.ChatServer.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Entity
@Table(name = "chat_keys")
@AllArgsConstructor
@NoArgsConstructor
public class ChatKeysModel {
    private @Id
    @Column(name = "chat_key_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator( name="native", strategy = "native") Long id;

    /*
    @Column(nullable = false)
    private Long userId;
    */

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name= "user_id", nullable = true)
    private UserModel user;

    /*
    @Column(nullable = false)
    private Long chatId;
    */

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "chats_id", nullable = true)
    private ChatModel chat;

    @Column(nullable = false, length = 1024)
    private String encryptedKey;

    public ChatKeysModel(Long id, String encryptedKey) {
        this.id = id;
        this.encryptedKey = encryptedKey;
    }
}

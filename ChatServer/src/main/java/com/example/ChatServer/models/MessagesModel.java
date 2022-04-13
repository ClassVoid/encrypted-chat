package com.example.ChatServer.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "messages")
@AllArgsConstructor
@NoArgsConstructor
public class MessagesModel {
    private @Id
    @Column(name = "message_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator( name="native", strategy = "native") Long  id;

    /*
    @Column(nullable = false)
    private Long userId;
    */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "user_id", nullable = true)
    private UserModel user;

    /*
    @Column(nullable = false)
    private Long chatId;
    */

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "chat_id", nullable = true)
    private ChatModel chat;

    @Column(nullable = false, length = 1024)
    private String encryptedMsg;

    @Column(nullable = false)
    private LocalDateTime date;

    public MessagesModel(Long id, String encryptedMsg, LocalDateTime date) {
        this.id = id;
        this.encryptedMsg = encryptedMsg;
        this.date = date;
    }
}

package com.example.ChatServer.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "chats")
@AllArgsConstructor
@NoArgsConstructor
public class ChatModel {
    private @Id
    @Column(name = "chat_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator( name="native", strategy = "native")
    Long id;

    @Column(nullable = false)
    private String chatName;

    @JsonIgnore
    @OneToMany(mappedBy = "chat", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ChatKeysModel> chatKeys=new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "chat", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MessagesModel> messages=new ArrayList<>();

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "chat_user", joinColumns = { @JoinColumn(name = "chat_id")}, inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private List<UserModel> users = new ArrayList<>();


    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "user_id", nullable = true)// cheap solution
    private UserModel owner;

    public ChatModel(Long id, String chatName) {
        this.id = id;
        this.chatName = chatName;
    }
}

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
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class UserModel {
    private @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator( name="native", strategy = "native")
    Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, length = 1024)
    private String pubKey;

    @Column(nullable = false, length = 8192)
    private String encryptedPriKey;



    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ChatKeysModel> chatKeys=new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MessagesModel> messages=new ArrayList<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "users", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ChatModel> chats=new ArrayList<>();

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ChatModel> chatsOwned=new ArrayList<>();

    public UserModel(Long id, String username, String pubKey, String encryptedPriKey){
        this.id=id;
        this.username=username;
        this.pubKey=pubKey;
        this.encryptedPriKey=encryptedPriKey;
    }

}

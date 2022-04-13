package com.example.IdentityCheckServer.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/*
    User will send his username and his public key
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPubKeyData {
    private String username;
    private String pubKey;
}

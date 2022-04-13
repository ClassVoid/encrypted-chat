package com.example.IdentityCheckServer.data;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketData {
    private String encryptedSecret;
    private String serverPublicKey;
}

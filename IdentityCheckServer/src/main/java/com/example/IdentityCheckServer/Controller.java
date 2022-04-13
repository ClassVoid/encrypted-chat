package com.example.IdentityCheckServer;

import com.example.IdentityCheckServer.data.UserEncrMessage;
import com.example.IdentityCheckServer.data.UserPubKeyData;
import com.example.IdentityCheckServer.services.TicketService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class Controller {
    private TicketService ticketService;

    @PostMapping("/api/generate-secret")
    ResponseEntity<?> generateSecret(@RequestBody UserPubKeyData userPubKeyData) throws Exception{
        return ticketService.generateSecret(userPubKeyData);
    }

    // this only validates the identity and removes the ticket from db, nothing else
    @PostMapping("/api/validation-test")
    ResponseEntity<?> validateIdentity(@RequestBody UserEncrMessage userEncrMessage)throws Exception{
        return ticketService.validateIdentity(userEncrMessage);
    }
}

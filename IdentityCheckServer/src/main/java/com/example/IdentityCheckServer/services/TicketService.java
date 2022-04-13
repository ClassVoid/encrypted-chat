package com.example.IdentityCheckServer.services;

import com.example.IdentityCheckServer.data.TicketData;
import com.example.IdentityCheckServer.data.UserEncrMessage;
import com.example.IdentityCheckServer.data.UserPubKeyData;
import com.example.IdentityCheckServer.models.TicketModel;
import com.example.IdentityCheckServer.repos.RepoTicket;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class TicketService {
    private final EncryptionService encryptionService;
    private final RepoTicket repoTicket;


    public ResponseEntity<?> generateSecret(UserPubKeyData userPubKeyData) throws Exception{

        // Validate
        if(userPubKeyData !=null && userPubKeyData.getUsername()!=null && userPubKeyData.getUsername().length()>0
        && userPubKeyData.getPubKey()!=null && userPubKeyData.getPubKey().length()>0){
            /*
                1.Generate a secret message
                2.Encrypt the message with RSA using user's public key
                3.Encrypt the message using your public key and save it in the db
                4.Send back the message encrypted with the user's public key
            */
            String message=generateRandomString(20);

            String encryptedWithServerKey=encryptionService.encryptWithOwnKey(message);
            String encryptedWithUserKey= encryptionService.encryptWithGivenKey(message, userPubKeyData.getPubKey());


            // Save the ticket in the DB
            TicketModel ticketModel=new TicketModel(0L, userPubKeyData.getUsername(), encryptedWithServerKey);
            repoTicket.save(ticketModel);

            // Create a response ticket
            TicketData ticketData= new TicketData(encryptedWithUserKey, encryptionService.getPublicKey());

            return new ResponseEntity<>(ticketData,HttpStatus.CREATED);

        }else
            return new ResponseEntity<>("ERROR, INVALID USERNAME OR PUBLIC KEY",HttpStatus.BAD_REQUEST);
    }

    @Transactional
    public ResponseEntity<?> validateIdentity(UserEncrMessage userEncrMessage) throws Exception{
        // Validate
        if( userEncrMessage!=null && userEncrMessage.getUsername()!=null && userEncrMessage.getUsername().length()>0
        && userEncrMessage.getEncryptedMsg()!=null && userEncrMessage.getEncryptedMsg().length()>0)
        {
            /*
            1.Check if there is at least one ticket for this user in the database
            2.Decrypt the messages both from user and database
              and if you find the same message the identity checks
            3.Remove the message that checks from the database ?!
            4.Return the identity confirmation
             */

            Optional<List<TicketModel>> optionalTicketModelList=repoTicket.findTicketModelByUsername(userEncrMessage.getUsername());

            if(optionalTicketModelList.isEmpty() || optionalTicketModelList.get().size()==0)
                return new ResponseEntity<>("ERROR, NO TICKET FOUND FOR USER "+userEncrMessage.getUsername(),HttpStatus.NOT_FOUND);

            List<TicketModel> ticketModelList=optionalTicketModelList.get();
            String clientMsg=encryptionService.decryptWithOwnKey(userEncrMessage.getEncryptedMsg());

            for (TicketModel ticket: ticketModelList) {
                String ticketMsg=encryptionService.decryptWithOwnKey(ticket.getEncrMessage());

                if(clientMsg.equals(ticketMsg)){
                    repoTicket.deleteByEncrMessage(ticket.getEncrMessage());
                    return new ResponseEntity<>(HttpStatus.OK);
                }
            }
            return new ResponseEntity<>("ERROR, NO SECRET MESSAGE FOUND",HttpStatus.NOT_FOUND);
        }else
            return new ResponseEntity<>("ERROR, INCORRECT USERNAME OR MESSAGE",HttpStatus.BAD_REQUEST);
    }


    private String generateRandomString(int length){
        int leftLimit='0';// '0'
        int rightLimit='z';// 'z'

        Random random=new Random();

        return random.ints(leftLimit, rightLimit+1)
                .filter(i-> (i<='9' || i>='A') && (i<='Z' || i>='a'))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}

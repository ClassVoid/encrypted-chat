package com.example.IdentityCheckServer.services;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class EncryptionService {
    private final PublicKey publicKey;
    private final PrivateKey privateKey;
    // "RSA/ECB/OAEPWITHSHA-512ANDMGF1PADDING"
    private final String cipherType="RSA/ECB/OAEPWITHSHA-1ANDMGF1PADDING";

    public EncryptionService() throws NoSuchAlgorithmException, NullPointerException {

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(4096);

        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        publicKey= keyPair.getPublic();
        privateKey= keyPair.getPrivate();

        // Don't forget to delete this when realeasing the app
        System.out.println("Public key:\n"+Base64.getEncoder().encodeToString(publicKey.getEncoded()));
        System.out.println("Private key:\n"+Base64.getEncoder().encodeToString(privateKey.getEncoded()));
    }

    public String encryptWithOwnKey(String message) throws Exception{
        Cipher cipher;
        cipher=Cipher.getInstance(cipherType);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] cipherText= cipher.doFinal(message.getBytes());

        return Base64.getEncoder().encodeToString(cipherText);
    }

    public String encryptWithGivenKey(String message, String pubKey) throws Exception{
        KeyFactory factory=KeyFactory.getInstance("RSA");

        byte[] data=Base64.getDecoder().decode(pubKey);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(data);

        PublicKey publicKey=(PublicKey) factory.generatePublic(spec);

        Cipher cipher;
        cipher=Cipher.getInstance(cipherType);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] cipherText= cipher.doFinal(message.getBytes());

        return Base64.getEncoder().encodeToString(cipherText);

    }

    public String decryptWithOwnKey(String encryptedMsg) throws Exception{
        Cipher cipher=Cipher.getInstance(cipherType);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] cipherTextArray=Base64.getDecoder().decode(encryptedMsg);
        byte[] decryptedText=cipher.doFinal(cipherTextArray);

        return new String(decryptedText);
    }

    public String getPublicKey() {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }
}

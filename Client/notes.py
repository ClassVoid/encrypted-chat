'''
    Must be implemented!

    AES stuff:
        - Create AES key for message encryption                         OK
        - endcrypt the AES key and upload it to the server              OK
        - you must be able to ask the server for your encrypted AES key OK

    Messages:
        - You need to encrypt the messages with the AES key             OK
            before sending them to the server

    Login:
        - Your password is used to encrypt your RSA private key         OK
            before sending it to the server


    HOW IT WORKS:
        -Every chat has a password
        -Each user has access to that password's encrypted version on the server,
            the password is encrypted with the public key of that user

        -You need to decrypt the chat password with the private key,
            after you can use the password to encrypt/decrypt messages
            that correspond to the chat


    remove user
    delete user
'''
from typing import Dict

from client_utils import *


def basicFunctionality():
    username = "userTest1"
    password = "password123"
    current_chat = "chat1"
    my_chat = "my_chat"

    # time
    x = datetime.datetime.now()
    dateTime = x.strftime("%Y-%m-%dT%H:%M:%S")

    # create account
    print("create account")
    createAccount(username, password)

    # get your credentials from the server
    credentials_res = getUserData(username)
    print(f"private key from server\n{credentials_res.json()['encryptedPriKey']}")

    privateKey=decryptAES(f"{credentials_res.json()['encryptedPriKey']}", password)
    print(f"private key from server\n{privateKey}")

    credentials: Dict[str, str] = {"username": f"{credentials_res.json()['username']}",
                   "pubKey": f"{credentials_res.json()['pubKey']}",
                   "PriKey": f"{privateKey}"}

    # create chat <<<<< i am here
    print("create chat")
    createChat(credentials, my_chat)

    # add myself to the chat
    print("add user")
    addUser(credentials, credentials['username'], my_chat)

    # get the aes key
    encrKeyAES= getChatKey(credentials['username'], my_chat)
    keyAES=decryptRSA(encrKeyAES, credentials['PriKey'])

    # send message
    print("send message")
    sendMessage(credentials['username'], my_chat, keyAES, f"hello my name is {credentials['username']}")

    # get all messages
    print("get messages")
    res=getMessages(my_chat, dateTime)
    message= decryptAES(res.json()[0]['encryptedMsg'], keyAES)
    print(f"message: {message}")

    # delete chat
    print("delete chat")
    deleteChat(credentials, my_chat)


if __name__ == '__main__':
    username = "userTest1"
    password = "password123"
    current_chat = "chat1"

    '''
    createAccount(username, password)
    getUserData(username)
    sendMessage(username, current_chat, "simple message")
    '''

    basicFunctionality()

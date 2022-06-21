import sys
import time

import requests
from PyQt5 import QtWidgets, uic
from PyQt5.QtCore import QObject, QThread, QRunnable, QThreadPool, pyqtSignal
from workers import *


class UI(QtWidgets.QMainWindow):
    def __init__(self):
        super(UI, self).__init__()
        uic.loadUi("chat_gui.ui", self)
        self.__extract_components()
        self.__configure_signals()

        self.can_get_msg = True
        self.stream_on=False

        self.show()

    def __extract_components(self):
        self.central_widget = self.findChild(QtWidgets.QWidget, "centralwidget")

        self.stacked_widget = self.central_widget.findChild(QtWidgets.QStackedWidget, "stackedWidget")
        self.stacked_widget.setCurrentIndex(0)
        # Login Page

        self.page_1 = self.stacked_widget.widget(0)
        self.login_btn = self.page_1.findChild(QtWidgets.QPushButton, "pushButton")
        self.create_user_btn = self.page_1.findChild(QtWidgets.QPushButton, "pushButton_3")
        self.username_txt = self.page_1.findChild(QtWidgets.QLineEdit, "lineEdit")
        self.password_txt = self.page_1.findChild(QtWidgets.QLineEdit, "lineEdit_2")

        # Main Page
        self.page_2 = self.stacked_widget.widget(1)
        self.current_chat = self.page_2.findChild(QtWidgets.QLabel, "label_3")
        self.current_user = self.page_2.findChild(QtWidgets.QLabel, "label_4")
        self.new_chat_btn = self.page_2.findChild(QtWidgets.QPushButton, "pushButton_5")
        self.send_btn = self.page_2.findChild(QtWidgets.QPushButton, "pushButton_6")
        self.add_user_btn = self.page_2.findChild(QtWidgets.QPushButton, "pushButton_7")
        self.logout_btn = self.page_2.findChild(QtWidgets.QPushButton, "pushButton_8")
        self.delete_chat_btn = self.page_2.findChild(QtWidgets.QPushButton, "pushButton_9")
        self.chat_list = self.page_2.findChild(QtWidgets.QListWidget, "listWidget")
        self.chat_browser = self.page_2.findChild(QtWidgets.QTextBrowser, "textBrowser")
        self.message_box = self.page_2.findChild(QtWidgets.QTextEdit, "textEdit")
        self.stream_btn=self.page_2.findChild(QtWidgets.QPushButton, "pushButton_2")

        self.preferences_act=QtWidgets.QAction("Preferences", self)
        self.findChild(QtWidgets.QMenuBar, "menubar")\
            .findChild(QtWidgets.QMenu,"menuSettings")\
            .addAction(self.preferences_act)
        print(f"Preferances= {self.preferences_act}")
        # self.chat_list.addItem("chat_1")
        # self.chat_list.addItem("chat_2")
        # self.chat_list.clear()

    def __configure_signals(self):
        # Login page
        self.login_btn.clicked.connect(self.__login_pressed)
        self.create_user_btn.clicked.connect(self.__create_user_pressed)

        # Main Page
        self.new_chat_btn.clicked.connect(self.__new_chat_pressed)
        self.send_btn.clicked.connect(self.__send_pressed)
        self.add_user_btn.clicked.connect(self.__add_user_pressed)
        self.logout_btn.clicked.connect(self.__logout_pressed)
        self.delete_chat_btn.clicked.connect(self.__delete_chat_pressed)
        self.chat_list.itemClicked.connect(self.__select_chat_pressed)
        self.stream_btn.clicked.connect(self.__stream_pressed)
        self.preferences_act.triggered.connect(self.__preferences_pressed)


    def __login_pressed(self):
        username = self.username_txt.text()
        password = self.password_txt.text()

        print(f"Login\nUsername: {username}\nPassword: {password}")
        '''
            Send the credentials to the server
            If everything works fine then save the data from the server
        '''
        self.login_btn.setEnabled(False)
        self.login_thread = QThread()
        self.login_worker = LoginWorker()
        self.login_worker.moveToThread(self.login_thread)

        self.login_thread.started.connect(lambda: self.login_worker.run(username, password))
        self.login_worker.finished.connect(self.login_thread.quit)
        self.login_worker.finished.connect(self.__login_callback)

        self.login_worker.finished.connect(self.login_worker.deleteLater)
        self.login_thread.finished.connect(self.login_thread.deleteLater)

        self.login_thread.start()

        self.login_thread.finished.connect(lambda: self.login_btn.setEnabled(True))

    def __login_callback(self, res: requests.Response, password: str):
        '''
        You must check the status to make sure everything went fine
        :param res:
        :param password:
        :return:
        '''
        print(res.status_code)
        # TO_DO add a try catch block, in case the password does not match
        privateKey = decryptAES(f"{res.json()['encryptedPriKey']}", password)

        self.credentials: Dict[str, str] = {"username": f"{res.json()['username']}",
                                            "pubKey": f"{res.json()['pubKey']}",
                                            "PriKey": f"{privateKey}"}

        print(self.credentials)
        if res.status_code == 200:
            self.current_user.setText(self.credentials['username'])
            self.send_btn.setEnabled(False)
            self.add_user_btn.setEnabled(False)
            self.delete_chat_btn.setEnabled(False)
            self.stream_btn.setEnabled(False)
            self.menuBar().setEnabled(True)
            self._refresh_chats()
            self._switch(1)

    def __create_user_pressed(self):
        username = self.username_txt.text()
        password = self.password_txt.text()
        print(f"Create Account\nUsername: {username}\nPassword: {password}")
        '''
            Create the credentials and send them to the server
            Check for errors
        '''
        self.create_user_btn.setEnabled(False)
        self.signup_thread = QThread()
        self.signup_worker = SignUpWorker()

        self.signup_worker.moveToThread(self.signup_thread)
        self.signup_thread.started.connect(lambda: self.signup_worker.run(username, password))
        self.signup_worker.finished.connect(self.signup_thread.quit)
        self.signup_worker.finished.connect(self.__signup_callback)
        self.signup_worker.finished.connect(self.signup_worker.deleteLater)
        self.signup_thread.finished.connect(self.signup_thread.deleteLater)
        self.signup_thread.start()
        self.signup_thread.finished.connect(lambda: self.create_user_btn.setEnabled(True))

    def __signup_callback(self, res: requests.Response.__class__):
        '''
        You have to check if the response is good
        :param res:
        :return:
        '''
        print(res)

    def __new_chat_pressed(self):
        chat_name, ok = QtWidgets.QInputDialog().getText(self, "Create chat", "Enter chat name")
        if ok:
            print(f"chat name is {chat_name}")
            self.new_chat_btn.setEnabled(False)
            self.new_chat_thread = QThread()
            self.new_chat_worker = NewChatWorker()
            self.new_chat_worker.moveToThread(self.new_chat_thread)

            self.new_chat_thread.started.connect(lambda: self.new_chat_worker.run(self.credentials, chat_name))
            self.new_chat_worker.finished.connect(self.new_chat_thread.quit)
            self.new_chat_worker.finished.connect(self.__new_chat_callback)

            self.new_chat_worker.finished.connect(self.new_chat_worker.deleteLater)
            self.new_chat_thread.finished.connect(self.new_chat_thread.deleteLater)

            self.new_chat_thread.start()
            self.new_chat_thread.finished.connect(lambda: self.new_chat_btn.setEnabled(True))

    def __new_chat_callback(self, res: requests.Response, chat_name: str):
        if res.status_code == 201:
            # make it async
            add_user_res = addUser(self.credentials, self.credentials['username'], chat_name)
            if add_user_res.status_code == 201:
                # make it async
                self._refresh_chats()

    def _refresh_chats(self):
        chats_res = getUserChats(self.credentials)
        chats = chats_res.json()
        # print(chats)
        self.chat_list.clear()
        for chat in chats:
            self.chat_list.addItem(chat)

    def __send_pressed(self):
        print("send")
        '''
            encrypt the message
            send the message
            updateChat()
        '''
        message = self.message_box.toPlainText()
        sendMessage(self.credentials['username'], self.current_chat.text(), self.chat_key, message)
        self.message_box.setText("")
        self._update_chat()

    def __add_user_pressed(self):
        print("add user")
        username, ok = QtWidgets.QInputDialog().getText(self, "Add user", "Enter user name")
        # you should check if the user exists
        addUser(self.credentials, username, self.current_chat.text())

    def __logout_pressed(self):
        # you could cleat the text boxes
        self.current_chat.setText("No Chat Selected")
        self.message_box.setText("")
        self.chat_browser.setText("")
        self.menuBar().setEnabled(False)
        self._switch(0)

    def __delete_chat_pressed(self):
        print("delete chat")
        '''
            call delete chat
            remove the chat from the list
        '''
        deleteChat(self.credentials, self.current_chat.text())
        self.chat_browser.setText("")
        self.current_chat.setText("No Chat Selected")
        self.send_btn.setEnabled(False)
        self.delete_chat_btn.setEnabled(False)
        self.add_user_btn.setEnabled(False)
        self._refresh_chats()

    def __select_chat_pressed(self, item):
        # save the current chat
        self.current_chat.setText(item.text())
        '''
            get chat key
            decrypt chat key and save it
            call updateChat()
        '''
        self.send_btn.setEnabled(True)
        self.add_user_btn.setEnabled(True)
        self.stream_btn.setEnabled(True)

        owner_name=getChatOwner(item.text()).text
        if owner_name==self.credentials['username']:
            self.delete_chat_btn.setEnabled(True)
        else:
            self.delete_chat_btn.setEnabled(False)

        self.chat_list.setEnabled(False)

        # get the chat key
        encrypted_key = getChatKey(self.credentials['username'], item.text())
        self.chat_key = decryptRSA(encrypted_key, self.credentials['PriKey'])

        self._update_chat()
        self.chat_list.setEnabled(True)

    def __stream_pressed(self):
        print("Stream Started")
        self.stream_on=not self.stream_on

        if self.stream_on:
            self.stream_btn.setText("Stop Stream")
        else:
            self.stream_btn.setText("Start Stream")


    def __preferences_pressed(self):
        print("preferences")

    def _update_chat(self):
        '''
        def updateChat():
                get the messages sent since yesterday
                decrypt the messages
                display the messages
        :return:
        '''

        chat_name = self.current_chat.text()
        print(f"updating chat {chat_name}")

        # !!! make async
        # get messages
        x = datetime.datetime.now() - datetime.timedelta(days=1)
        date_time = x.strftime("%Y-%m-%dT%H:%M:%S")
        messages = getMessages(chat_name, date_time).json()

        chat_text = ""
        # decrypt messages
        for i in range(len(messages)):
            chat_text += f"{messages[i]['author']}({messages[i]['date']}):" \
                         f" {decryptAES(messages[i]['encryptedMsg'], self.chat_key)}\n"

        self.chat_browser.setText(chat_text)

    def _switch(self, index):
        self.stacked_widget.setCurrentIndex(index)


def main():
    app = QtWidgets.QApplication(sys.argv)
    window = UI()
    app.exec_()


main()

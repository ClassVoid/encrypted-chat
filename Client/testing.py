import datetime

import requests
import hashlib
import base64
from Crypto.Cipher import PKCS1_OAEP
from Crypto.PublicKey import RSA
from Crypto.Cipher import AES
from Crypto.Random import get_random_bytes


def encryption():
    '''
    pot sa utilizez orice format pentru cheile PRIVATE atat timp cat sunt utilizate
    doar de client si serverul doar le stocheaza

    cheile publice trbuie sa fie neaparat in base64 string pentru a
    putea fi utilizate de server

    '''
    # generez cheile
    key = RSA.generate(4096)
    # keyFormat='PEM'#'PEM' 'DER'
    privateKey = key.exportKey(format='PEM')  # ASCII armored
    publicKey = key.publickey().exportKey(format='DER')  # bytes

    priv_text = str(privateKey, 'utf-8')
    print(str(privateKey, 'utf-8'))
    print(f"public key:\n{base64.b64encode(publicKey).decode('utf-8')}")

    '''
    # chei statice
    pubKey="MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEA0lKUt1WE3VgoupynT4gr90noj0HC/jA5LSMVhNs5Np6NsbA/zQC0QJdEEOLogk6mdmy73QfMJXd5S8J1/wp4sNR+NCO5uX+dDjrgGU65awMAGZ0/JRWGHC2gT7AjoJL+Ton4J/2CZBROL+ilBr72Bx/qVm91Mk/FZptVLArl0TsTp76vUhjGZpLNlrZ88nURr7meqyZap2a1wkvlPR1Zlld0jQ+sDPpkWlGfqGmdhN5bc1Mnh+9FZ7jUw0SJAy54rDYeD4dCBLAnsTDUQz7QSUN+5CEdHITY5NNvqa7g+mDR1N/Qgsymliy54SeI4RO4pBz6V9PysIhtysy33bj4FlkcTklqr+10ND241He/jqTlvA1bxSQ9zikoyiJTiAmQK9BB5fTB7qimvTGDS8Sfb2xl/nuSjngYVItW5Q/QFClHm1TGxYVQWkRe+Q6Wz3dSN95t9XYzV1E7gvTAYa/sfE4q7mIPEW6vnScvUjjSVmUikQGUKe+Jm4Q51+69JiMdoWJGm5mdzeT4IAZtLW6b2LyC2HqbqTFT6B1yqbrodfdXIccfIDkfwiYP7k65lVbLHWXQYvKrHSvApmcgrRx6FV3/IdIskMHybJCa5dHnJWPD6N0YvUpLgMWIz9cD1maBCDrc5xG/7RAv+i0X8jTWlXhi9QjR/cfHSSPzDdIcJ4ECAwEAAQ=="
    privKey="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"
    publicKey=base64.b64decode(pubKey)
    privateKey=base64.b64decode(privKey)
    '''

    # encodez mesajul in bytes
    message = "this is something"
    msgEncode = str.encode(message)

    # criptez mesajul
    rsaPubKey = RSA.importKey(publicKey)
    oaepCipher = PKCS1_OAEP.new(rsaPubKey)
    encryptedMsg = oaepCipher.encryptRSA(msgEncode)

    # encodez in baza 64
    encrStr = base64.b64encode(encryptedMsg).decode('utf-8')
    print(f"encrypted msg:\n{encrStr}")

    # decriptez mesajul
    rsaPrivKey = RSA.importKey(priv_text)
    oaepCipher = PKCS1_OAEP.new(rsaPrivKey)
    decryptedMsg = oaepCipher.decryptRSA(encryptedMsg)
    msgStr = str(decryptedMsg, 'utf-8')
    print(f"decrypted msg:\n{msgStr}")


def encryptionAES():
    message = "secret rsa key"
    key = "here is a simple key".encode()
    keySHA256 = hashlib.sha256(key).digest()

    # encrypt
    BS = AES.block_size
    pad = lambda s: s + (BS - len(s) % BS) * chr(BS - len(s) % BS)

    raw = base64.b64encode(pad(message).encode('utf8'))
    iv = get_random_bytes(AES.block_size)  # initialization vector

    cipher = AES.new(keySHA256, AES.MODE_CFB, iv)
    msgEncrypted = cipher.encrypt(raw)
    resultEncryption = base64.b64encode(iv + msgEncrypted)
    resultEncryption2 = base64.b64encode(iv + msgEncrypted).decode('utf8')
    print(f"encrypted message:\n{resultEncryption}")
    print(f"encrypted message2:\n{resultEncryption2}")

    # decryption
    unpad = lambda s: s[:-ord(s[-1:])]

    enc = base64.b64decode(resultEncryption)
    iv = enc[:AES.block_size]

    cipher = AES.new(keySHA256, AES.MODE_CFB, iv)
    msgDecrypted = cipher.decrypt(enc[AES.block_size:])
    resultDecryption =unpad(base64.b64decode(msgDecrypted).decode('utf8'))

    print(f"decrypted msg:\n{resultDecryption}")


# encryption()
#encryptionAES()


'''
size=30
result=''.join(random.choices(string.ascii_lowercase+string.ascii_uppercase+string.digits, k=size))
print(result)
'''

'''
print("2022-03-28T17:48:45")
x=datetime.datetime.now()
x_form=x.strftime("%Y-%m-%dT%H:%M:%S")
print(x_form)
'''

'''
from PyQt5.QtCore import QObject, QThread, pyqtSignal
from PyQt5.QtWidgets import *
from PyQt5 import Qt
import sys
from time import sleep
# Snip...

# Step 1: Create a worker class
class Worker(QObject):
    finished = pyqtSignal()
    progress = pyqtSignal(int)

    def __init__(self, sec):
        super(QObject, self).__init__()
        self.sec=sec

    def run(self):
        sleep(self.sec)
        self.finished.emit()

class Window(QMainWindow):
    def __init__(self, parent=None):
        super().__init__(parent)
        self.clicksCount = 0
        self.setupUi()

    def setupUi(self):
        self.setWindowTitle("Freezing GUI")
        self.resize(300, 150)
        self.centralWidget = QWidget()
        self.setCentralWidget(self.centralWidget)
        # Create and connect widgets
        self.clicksLabel = QLabel("Counting: 0 clicks", self)
        #self.clicksLabel.setAlignment(Qt.AlignHCenter | Qt.AlignVCenter)
        self.stepLabel = QLabel("Long-Running Step: 0")
        #self.stepLabel.setAlignment(Qt.AlignHCenter | Qt.AlignVCenter)
        self.countBtn = QPushButton("Click me!", self)
        self.countBtn.clicked.connect(self.countClicks)
        self.longRunningBtn = QPushButton("Long-Running Task!", self)
        self.longRunningBtn.clicked.connect(self.runLongTask)
        # Set the layout
        layout = QVBoxLayout()
        layout.addWidget(self.clicksLabel)
        layout.addWidget(self.countBtn)
        layout.addStretch()
        layout.addWidget(self.stepLabel)
        layout.addWidget(self.longRunningBtn)
        self.centralWidget.setLayout(layout)

    def countClicks(self):
        self.clicksCount += 1
        self.clicksLabel.setText(f"Counting: {self.clicksCount} clicks")

    def reportProgress(self, n):
        self.stepLabel.setText(f"Long-Running Step: {n}")

    def runLongTask(self):
        # Step 2: Create a QThread object
        self.thread = QThread()
        # Step 3: Create a worker object
        self.worker = Worker(10)
        # Step 4: Move worker to the thread
        self.worker.moveToThread(self.thread)
        # Step 5: Connect signals and slots
        self.thread.started.connect(self.worker.run)
        self.worker.finished.connect(self.thread.quit)
        self.worker.finished.connect(self.worker.deleteLater)
        self.thread.finished.connect(self.thread.deleteLater)
        self.worker.progress.connect(self.reportProgress)
        # Step 6: Start the thread
        self.thread.start()

        # Final resets
        self.longRunningBtn.setEnabled(False)
        self.thread.finished.connect(
            lambda: self.longRunningBtn.setEnabled(True)
        )
        self.thread.finished.connect(
            lambda: self.stepLabel.setText("Long-Running Step: 0")
        )

app = QApplication(sys.argv)
win = Window()
win.show()
sys.exit(app.exec())
'''

import re

serv_addr="rtmp://192.168.100.21:1935/show/cineva"
addr="rtmp://127.0.0.1/show/test"
fake_addr="rtmp://127.0.0.1/show/test & echo hello hack"

client_pattern="^rtmp://\d+\.\d+\.\d+\.\d+/\w+/\w+$"
match= re.search("^rtmp://\d+\.\d+\.\d+\.\d+:\d+/\w+/\w+$",serv_addr)
print(match)


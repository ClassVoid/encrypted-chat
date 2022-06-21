from PyQt5.QtCore import QObject, pyqtSignal
import subprocess

from client_utils import *


class LoginWorker(QObject):
    finished = pyqtSignal(requests.Response, str)

    def run(self, username: str, password: str):
        res = getUserData(username)
        self.finished.emit(res, password)


class SignUpWorker(QObject):
    finished = pyqtSignal(requests.Response)

    def run(self, username: str, password: str):
        res = createAccount(username, password)
        self.finished.emit(res)


class NewChatWorker(QObject):
    finished = pyqtSignal(requests.Response, str)

    def run(self, credentials: Dict[str, str], chat_name: str):
        res = createChat(credentials, chat_name)
        self.finished.emit(res, chat_name)


class MessagesWorker(QObject):
    finished = pyqtSignal(requests.Response)

    def run(self, chat_name: str, date_time: str):
        res = getMessages(chat_name, date_time)
        self.finished.emit(res)


class StreamSenderWorker(QObject):
    finished = pyqtSignal()

    def run(self):
        stream_address = "rtmp://localhost:1935/show/test"
        stream_cmd = f"ffmpeg -f v4l2 -i /dev/video0 -vcodec libx264 -b:v 300k -threads 2 -tune zerolatency -fflags low_delay -fflags nobuffer -g 8 -f flv {stream_address}"  # .lstrip().rstrip().split()
        # view_camera_cmd = "ffplay -i /dev/video0 -fflags nobuffer".rstrip().lstrip().split()
        self.proc = subprocess.Popen(f"exec {stream_cmd}", shell=True, stdout=subprocess.PIPE)

    def stop(self):
        self.proc.kill()
        self.finished.emit()


class StreamConsumerWorker(QObject):
    finished= pyqtSignal()

    def run(self):
        stream_address = "rtmp://127.0.0.1/show/test"
        cmd = f"ffplay -fflags nobuffer {stream_address}".rstrip().lstrip().split()
        subprocess.run(cmd)
        self.finished.emit()


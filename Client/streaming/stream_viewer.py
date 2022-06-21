import time
import subprocess

import ffmpeg_streaming
import os
import vlc



video_address="video_test.mkv"
stream_address="rtmp://127.0.0.1/show/test"
stream_obs="rtmp://127.0.0.1/show/bbb"
# video=ffmpeg_streaming.input(stream_address)
'''
vlc_instance=vlc.Instance()
player=vlc_instance.media_player_new()
media=vlc_instance.media_new("video_test.mkv")
player.set_media(media)
player.play()
time.sleep(5)
print(player.get_length())
'''


'''
vlc_media=vlc.MediaPlayer()
vlc_media.set_mrl(stream_address)
playing=vlc_media.play()

while True:
    pass
'''


# varianta bruta
cmd=f"ffplay -fflags nobuffer {stream_address}".rstrip().lstrip().split()
subprocess.run(cmd)



'''
from PyQt5.QtCore import QDir, Qt, QUrl
from PyQt5.QtMultimedia import QMediaContent, QMediaPlayer
from PyQt5.QtMultimediaWidgets import QVideoWidget
from PyQt5.QtWidgets import (QMainWindow, QWidget, QPushButton, QApplication,
                             QLabel, QFileDialog, QStyle, QVBoxLayout)
import sys


class VideoPlayer(QMainWindow):
    def __init__(self):
        super().__init__()
        self.setWindowTitle("PyQt5 Video Player")

        self.mediaPlayer = QMediaPlayer(None, QMediaPlayer.LowLatency)
        videoWidget = QVideoWidget()

        self.playButton = QPushButton()
        self.playButton.setIcon(self.style().standardIcon(QStyle.SP_MediaPlay))
        self.playButton.clicked.connect(self.play)

        #self.openButton = QPushButton("Open Video")
        #self.openButton.clicked.connect(self.openFile)

        widget = QWidget(self)
        self.setCentralWidget(widget)

        layout = QVBoxLayout()
        layout.addWidget(videoWidget)
        #layout.addWidget(self.openButton)
        layout.addWidget(self.playButton)

        widget.setLayout(layout)
        self.mediaPlayer.setVideoOutput(videoWidget)

    def openFile(self):
        fileName, _ = QFileDialog.getOpenFileName(self, "Open Movie",
                                                  QDir.homePath())

        if fileName != '':
            self.mediaPlayer.setMedia(
                QMediaContent(QUrl.fromLocalFile(fileName)))

    def play(self):

        self.mediaPlayer.setMedia(QMediaContent(QUrl(stream_obs)))

        if self.mediaPlayer.state() == QMediaPlayer.PlayingState:
            self.mediaPlayer.pause()
        else:
            self.mediaPlayer.play()


app = QApplication(sys.argv)
videoplayer = VideoPlayer()
videoplayer.resize(640, 480)
videoplayer.show()
sys.exit(app.exec_())
'''

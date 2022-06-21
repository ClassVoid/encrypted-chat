import os
import time

import pylivestream.api as pls
import subprocess
import threading
#help(pls.stream_webcam)

'''
ini_file="/usr/local/lib/python3.8/dist-packages/pylivestream/pylivestream.ini"
websites=["localhost"]
path=pls.Path(ini_file)
pls.stream_webcam(ini_file=path, websites=websites, assume_yes=True, timeout=60*60)
'''

def start_stream():
    #classic solution
    stream_address="rtmp://localhost:1935/show/test"
    stream_cmd=f"ffmpeg -f v4l2 -i /dev/video0 -vcodec libx264 -b:v 300k -threads 2 -tune zerolatency -fflags low_delay -fflags nobuffer -g 8 -f flv {stream_address}"#.lstrip().rstrip().split()
    view_camera_cmd="ffplay -i /dev/video0 -fflags nobuffer".rstrip().lstrip().split()
    proc= subprocess.Popen(f"exec {stream_cmd}", shell=True, stdout=subprocess.PIPE)
    time.sleep(5)
    proc.kill()

start_stream()

# ffmpeg -f v4l2 -i /dev/video0 -vcodec libx264 -b:v 300k -threads 2 -tune zerolatency -fflags low_delay -fflags nobuffer -g 8 -f flv rtmp://localhost:1935/show/test & ffplay -i /dev/video0 -fflags nobuffer

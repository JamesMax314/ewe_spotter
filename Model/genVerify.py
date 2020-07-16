import numpy as np
import sys
from os import listdir
import os

data = "./Data/"
verify = "./Verify/"

try:
    os.mkdir(verify)
except:
    pass

for dir in listdir(data):
    files = listdir(data + "/" + dir)
    try:
        os.mkdir(verify + "/" + dir)
    except:
        pass
    for i in range(3):
        try:
            os.rename(data + dir + "/" + files[i], verify + dir + "/" + files[i])
        except:
            pass

from os import listdir, remove
from PIL import Image
import cv2

for folder in listdir('./Data'):
    for file in listdir("./Data/" + folder + "/"):
        # print("./Data/" + folder + "/" + file)
        # cv2.imread("./Data/" + folder + "/" + file)
        try:
            img = Image.open("./Data/" + folder + "/" + file)  # open the image file
            img.verify()  # verify that it is, in fact an image
            # print(folder + "/" + file + " ok")
        except (IOError, SyntaxError) as e:
            print('Removing:', file)
            remove("./Data/" + folder + "/" + file)
        if file[-3:] != "jpg":
            print('Removing:', file)
            remove("./Data/" + folder + "/" + file)
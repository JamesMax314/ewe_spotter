import tensorflow as tf
import sys
import tensorflow.keras as keras
import numpy as np
from tensorflow.keras.preprocessing.image import load_img, img_to_array
# from tensorflow.keras.applications.vgg16 import preprocess_input
from tensorflow.keras.applications.resnet50 import preprocess_input

import pickle as pkl
import matplotlib.pyplot as plt

imgN = "./Verify/Badger Face sheep/Samson-Torwen-ram.jpg"
img = image = load_img(imgN, target_size=(224, 224))
img = img_to_array(img)
img = preprocess_input(img)
print(img)
plt.imshow(np.array(img, dtype=int))
plt.show()

img = img.reshape((1, img.shape[0], img.shape[1], img.shape[2]))



model = keras.models.load_model('./trainedModels/m3')
classes = pkl.load(open("classes", "rb"))
classes = {v: k for k, v in classes.items()}
# print(model.class_names)
print(classes)
print(classes[np.argmax(model.predict(img))])

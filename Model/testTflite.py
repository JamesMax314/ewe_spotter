import numpy as np
import tensorflow as tf
from tensorflow.keras.preprocessing.image import load_img, img_to_array
from tensorflow.keras.applications.resnet50 import preprocess_input
import matplotlib.pyplot as plt
import pickle as pkl

if __name__ == "__main__":
    path = "./liteModels/m4.tflite"
    imagePath = "./Verify/Badger Face sheep/badgerface welsh rbst - b kempton ewe_2x.jpg"
    img = load_img(imagePath, target_size=(224, 224))
    img = np.array(img, dtype=np.float32)
    img = img.reshape((1, img.shape[0], img.shape[1], img.shape[2]))
    # img = preprocess_input(img)
    interpreter = tf.lite.Interpreter(model_path=path)
    interpreter.allocate_tensors()
    input_index = interpreter.get_input_details()[0]["index"]
    output_index = interpreter.get_output_details()[0]["index"]
    interpreter.set_tensor(input_index, img)
    interpreter.invoke()
    predictions = interpreter.get_tensor(output_index)

    classes = pkl.load(open("classes", "rb"))
    classes = {v: k for k, v in classes.items()}
    print(predictions)
    print(classes[np.argmax(predictions)])
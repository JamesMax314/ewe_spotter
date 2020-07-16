import sys

import tensorflow as tf
import pickle as pkl


def convert(dir):
    print(dir)
    model = tf.keras.models.load_model(dir)
    converter = tf.lite.TFLiteConverter.from_keras_model(model)
    tfLiteModel = converter.convert()
    return tfLiteModel


def save_lite(model, dir):
    with tf.io.gfile.GFile(dir, "wb") as f:
        f.write(model)


def gen_labels(labels, dir):
    classes = pkl.load(open(labels, "rb"))
    f = open(dir, "w")
    for label in classes:
        f.write(label + "\n")


if __name__ == "__main__":
    modelDir = "./trainedModels/"
    liteDir = "./liteModels/"
    labels = "classes"
    modelFile = modelDir + str(sys.argv[1])
    liteFile = liteDir + str(sys.argv[1]) + ".tflite"
    liteLabels = liteDir + str(sys.argv[1]) + "_labels" + ".txt"
    mTfLiteModel = convert(modelFile)
    save_lite(mTfLiteModel, liteFile)
    gen_labels(labels, liteLabels)


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

def gen_labels_xml(labels, dir):
    classes = pkl.load(open(labels, "rb"))
    f = open(dir, "w")
    f.write("<string-array name=\"sheep_names\">" + "\n")
    for label in classes:
        f.write("<item>" + label + "</item>" + "\n")
    f.write("</string-array>")

if __name__ == "__main__":
    modelDir = "./trainedModels/"
    liteDir = "./liteModels/"
    labels = "classes"
    modelFile = modelDir + str(sys.argv[1])
    liteFile = liteDir + str(sys.argv[1]) + ".tflite"
    liteLabels = liteDir + str(sys.argv[1]) + "_labels" + ".txt"
    liteLabelsXml = liteDir + str(sys.argv[1]) + "_labels_xml" + ".txt"
    # mTfLiteModel = convert(modelFile)
    # save_lite(mTfLiteModel, liteFile)
    # gen_labels(labels, liteLabels)
    gen_labels_xml(labels, liteLabelsXml)


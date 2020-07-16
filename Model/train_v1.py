import numpy as np
import tensorflow as tf
from tensorflow.keras.applications.vgg16 import VGG16
from tensorflow.keras import layers, models
# from tensorflow.keras.applications.vgg16 import preprocess_input
from tensorflow.keras.applications.resnet50 import preprocess_input
import tensorflow.lite as lite
import pickle as pkl

c = tf.compat.v1.ConfigProto()
c.gpu_options.allow_growth = True
# print(device_lib.list_local_devices())

new_input = tf.keras.Input(shape=(224, 224, 3))
model = tf.keras.applications.ResNet50(
    include_top=False,
    weights="imagenet",
    input_tensor=new_input,
    pooling="avg",
)

model.summary()
model = tf.keras.Sequential(
    [
        model,
        layers.Dense(1024, activation='relu'),
        layers.Dense(21, activation='softmax')
    ]
)
model.layers[0].trainable = False

model.summary()
model.compile(optimizer="adam",
              loss="sparse_categorical_crossentropy",
              metrics=[tf.keras.metrics.SparseCategoricalAccuracy()])

datagen = tf.keras.preprocessing.image.ImageDataGenerator(preprocessing_function=preprocess_input)
train_data = datagen.flow_from_directory('Data/', class_mode='binary', target_size=(224, 224))
verify_data = datagen.flow_from_directory('Verify/', class_mode='binary', target_size=(224, 224))
print(train_data.class_indices)
pkl.dump(train_data.class_indices, open("./classes", "wb"))
model.fit(train_data, epochs=5, batch_size=32, validation_data=train_data)
model.save('./trainedModels/m4')

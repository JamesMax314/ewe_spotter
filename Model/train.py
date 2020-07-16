import numpy as np
import tensorflow as tf
import tensorflow.keras as keras
from tensorflow.keras import layers, models
from tensorflow.keras.layers.experimental.preprocessing import Rescaling, CenterCrop
import datetime

names = ["Soay", "North Ronaldsay sheep", "Hebridean sheep", "Castlemilk Moorit", "Balwen", "Badger Face sheep",
             "Eppynt Hill and Beulah Speckled Face", "Lleyn", "Llanwenog sheep", "Wensleydale sheep", "Lonk",
             "Herdwick", "Manx Loaghtan", "Border Leicester", "Suffolk sheep", "Cotswold sheep",
             "Portland sheep", "Southdown sheep", "Devon and Cornwall Longwool", "Greyface Dartmoor", "Exmoor Horn"]

directory = "./Data/"

training_data = tf.keras.preprocessing.image_dataset_from_directory(
    directory,
    labels="inferred",
    label_mode="int",
    class_names=None,
    color_mode="rgb",
    batch_size=40,
    image_size=(150, 150),
    shuffle=True,
    seed=None,
    validation_split=None,
    subset=None,
    interpolation="bilinear",
    follow_links=False,
)

def my_gen(gen):
    while True:
        try:
            data, labels = next(gen)
            yield data, labels
        except:
            pass

# training_data = my_gen(training_data)

inputs = tf.keras.Input(shape=(None, None, 3))
x = CenterCrop(height=150, width=150)(inputs)
# Rescale images to [0, 1]
x = Rescaling(scale=1.0 / 255)(x)

x = layers.Conv2D(32, (4, 4), strides=(1, 1), activation='relu')(x)
x = layers.MaxPooling2D((2, 2))(x)
x = layers.Conv2D(64, (3, 3), activation='relu')(x)
x = layers.MaxPooling2D((2, 2))(x)
x = layers.Conv2D(64, (3, 3), activation='relu')(x)
x = layers.Flatten()(x)
x = layers.Dense(64, activation='relu')(x)
outputs = layers.Dense(21, activation="softmax")(x)
model = keras.Model(inputs=inputs, outputs=outputs)
model.summary()

model.compile(optimizer="adam", loss="sparse_categorical_crossentropy", metrics=['accuracy'])
print("compiled")

log_dir = "./logs/fit/" + datetime.datetime.now().strftime("%Y%m%d-%H%M%S")
tensorboard_callback = tf.keras.callbacks.TensorBoard(log_dir=log_dir, histogram_freq=1)
# data = np.random.randint(0, 256, size=(64, 300, 300, 3)).astype("float32")
# processed_data = model(data)
history = model.fit(training_data, epochs=20, callbacks=[tensorboard_callback])
model.save('./trainedModels/m1')
# print(processed_data.shape)

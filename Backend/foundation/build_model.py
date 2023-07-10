import math
import keras
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
from sklearn.model_selection import train_test_split
from keras.models import Sequential
from keras.layers import Dense, Flatten, Conv1D, MaxPooling1D, Dropout
from keras.utils import to_categorical
from keras.metrics import categorical_accuracy

# importing the dataset
dataset = pd.read_csv('medium_squat_data.csv', delimiter=',')

X = dataset.iloc[:, 1:].values
y = dataset.iloc[:, -1].values

# one-hot encode target data
num_classes = 6
y = to_categorical(y, num_classes)

# splitting the dataset into the Training set 80% and Test set 20%
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

# reshaping the input data for CNN
X_train = np.reshape(X_train, (X_train.shape[0], X_train.shape[1], 1))
X_test = np.reshape(X_test, (X_test.shape[0], X_test.shape[1], 1))

# initialize the CNN
model = Sequential()

# adding the first convolutional layer
model.add(Conv1D(filters=32, kernel_size=3, activation='relu', input_shape=(X_train.shape[1], 1)))
model.add(MaxPooling1D(pool_size=2))
model.add(Dropout(0.2))

# adding a second convolutional layer
model.add(Conv1D(filters=64, kernel_size=3, activation='relu'))
model.add(MaxPooling1D(pool_size=2))
model.add(Dropout(0.2))

# adding a third convolutional layer
model.add(Conv1D(filters=128, kernel_size=3, activation='relu'))
model.add(MaxPooling1D(pool_size=2))
model.add(Dropout(0.2))

# flattening the output of the third convolutional layer
model.add(Flatten())

# adding the output layer
model.add(Dense(units=num_classes, activation='sigmoid'))

# compiling the CNN model using categorical crossentropy loss
model.compile(optimizer='adam', loss='categorical_crossentropy', metrics=[categorical_accuracy])

# fitting the CNN to the Training set
history = model.fit(X_train, y_train, epochs=20, batch_size=32, validation_data=(X_test, y_test))

# save the model to a file
model_file = 'medium_squat_cnn_model33.h5'
model.save(model_file)

# load the model from the file
loaded_model = keras.models.load_model(model_file)

# make predictions
predicted_y = loaded_model.predict(X_test)

# evaluate the model
loss, accuracy = model.evaluate(X_test, y_test, verbose=0)

# print accuracy
print('Accuracy: %.3f' % (accuracy*100))

# plotting the loss
plt.plot(history.history['loss'], color='red')
plt.plot(history.history['val_loss'], color='blue')
plt.title('Model Loss')
plt.ylabel('Loss')
plt.xlabel('Epoch')
plt.legend(['Training', 'Testing'], loc='upper right')
# add text for accuracy
accuracy_text = 'Accuracy: %.3f' % (accuracy * 100)
plt.annotate(accuracy_text, xy=(0.5, 0.1), xycoords='axes fraction', fontsize=13, ha='center', va='center',
             bbox=dict(boxstyle='round', facecolor='white', edgecolor='gray'))
plt.show()

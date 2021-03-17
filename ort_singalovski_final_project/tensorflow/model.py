import tensorflow as tf
import numpy as np
import cv2
import os

# Image resize width and height
resize_width = 32
resize_height = 32

# Paths to the train and test images
train_images_dir_path = '/home/adam/PycharmProjects/train_images/'
test_images_dir_path = '/home/adam/PycharmProjects/test_images/'

# Arrays to hold the RGBA values of the images as well as their classification
# 0 - non sexual image
# 1 - sexual image
train_data_features = []
train_data_labels = []
test_data_features = []
test_data_labels = []


def neural_network_model(features, labels, mode):
    """A function used to create the graph using the features and labels matrices
       features: matrix which contains the RGBA values of the images
       labels: matrix which contains the classification to each image accordingly
       mode: defines if the function is used to build the model or test on it"""

    input_layer = tf.reshape(features["x"], [-1, resize_width, resize_height, 1])

    conv1 = tf.layers.conv2d(inputs=input_layer, filters=32, kernel_size=[3, 3], padding="same", activation=tf.nn.relu)
    conv2 = tf.layers.conv2d(inputs=conv1, filters=64, kernel_size=[3, 3], padding="same", activation=tf.nn.relu)
    pool1 = tf.layers.max_pooling2d(inputs=conv2, pool_size=[2, 2], strides=2)
    drop1 = tf.layers.dropout(pool1, rate=0.25)

    conv3 = tf.layers.conv2d(inputs=drop1, filters=128, kernel_size=[3, 3], padding="same", activation=tf.nn.relu)
    pool2 = tf.layers.max_pooling2d(inputs=conv3, pool_size=[2, 2], strides=2)
    conv4 = tf.layers.conv2d(inputs=pool2, filters=128, kernel_size=[2, 2], padding="same", activation=tf.nn.relu)
    pool3 = tf.layers.max_pooling2d(inputs=conv4, pool_size=[2, 2], strides=2)
    drop2 = tf.layers.dropout(pool3, rate=0.25)

    output_layer = tf.reshape(drop2, [-1, 4 * 4 * 128])
    output = tf.layers.dense(inputs=output_layer, units=1500, activation=tf.nn.relu)
    dropout = tf.layers.dropout(inputs=output, rate=0.25, training=mode == tf.estimator.ModeKeys.TRAIN)

    logits = tf.layers.dense(inputs=dropout, units=2)

    predictions = {"classes": tf.argmax(input=logits, axis=1),
                   "probabilities": tf.nn.softmax(logits, name="softmax_tensor")}

    if mode == tf.estimator.ModeKeys.PREDICT:
        return tf.estimator.EstimatorSpec(mode=mode, predictions=predictions)

    loss = tf.losses.sparse_softmax_cross_entropy(labels=labels, logits=logits)

    if mode == tf.estimator.ModeKeys.TRAIN:
        optimizer = tf.train.GradientDescentOptimizer(learning_rate=0.001)
        train_op = optimizer.minimize(loss=loss, global_step=tf.train.get_global_step())
        return tf.estimator.EstimatorSpec(mode=mode, loss=loss, train_op=train_op)

    eval_metric_ops = {
        "accuracy": tf.metrics.accuracy(labels=labels, predictions=predictions["classes"])
    }
    return tf.estimator.EstimatorSpec(mode=mode, loss=loss, eval_metric_ops=eval_metric_ops)


def serving_input_receiver_fn():
    """A function used to define how the saved model is suppose
       to receive the values for testing"""
    inputs = {'x': tf.placeholder(dtype=tf.float32, shape=[1, resize_width * resize_height], name='x')}
    return tf.estimator.export.ServingInputReceiver(inputs, inputs)


# Counters for the amount of nude train images
count_nude_train = 0
count_not_nude_train = 0

# For loop used to build the train images features and labels matrices
for file in os.listdir(train_images_dir_path):
    try:
        image = cv2.imread(os.path.join(train_images_dir_path, file), cv2.IMREAD_GRAYSCALE)
        image = cv2.resize(image, (resize_width, resize_height))
        temp = []
        for x in image:
            for y in x:
                temp.append(y)
        train_data_features.append(temp)
        if "not_nude" in file:
            count_not_nude_train += 1
            train_data_labels.append(0)
        elif "nude" in file:
            count_nude_train += 1
            train_data_labels.append(1)
    except:
        0

# Counters for the amount of nude test images
count_nude_test = 0
count_not_nude_test = 0

# For loop used to build the test images features and labels matrices
for file in os.listdir(test_images_dir_path):
    try:
        image = cv2.imread(os.path.join(test_images_dir_path, file), cv2.IMREAD_GRAYSCALE)
        image = cv2.resize(image, (resize_width, resize_height))
        temp = []
        for x in image:
            for y in x:
                temp.append(y)
        test_data_features.append(temp)
        if "not_nude" in file:
            count_not_nude_test += 1
            test_data_labels.append(0)
        elif "nude" in file:
            count_nude_test += 1
            test_data_labels.append(1)
    except:
        0

# Set the classifier as the neural network function above
# as well as the model recovery folder
my_classifier = tf.estimator.Estimator(model_fn=neural_network_model, model_dir="/home/adam/tensorflow_model/my_model")

# Set the logging hook to see the process checkpoints
tensors_to_log = {"probabilities": "softmax_tensor"}
logging_hook = tf.train.LoggingTensorHook(tensors_to_log, every_n_iter=50)

# Change the train images matrices from 0, 1 matrix
# to 0 - 255 values
X = np.array(train_data_features, dtype=np.float32) / 255.0
Y = np.array(train_data_labels)

# Change the test images matrices from 0, 1 matrix
# to 0 - 255 values
test_X = np.array(test_data_features, dtype=np.float32) / 255.0
test_Y = np.array(test_data_labels)

# Summarize the total number of steps needed by adding the
# amount of train and test images
total_steps = count_not_nude_train + count_nude_train

# Set the parameters for the neural network function
# x - features
# y - labels
# batch_size - each iteration size
# num_epochs - when to stop (None - run until the end of data)
# shuffle - whether to shuffle the data
train_input_fn = tf.estimator.inputs.numpy_input_fn(
    x={'x': X},
    y=Y,
    batch_size=100,
    num_epochs=None,
    shuffle=True)

# Start the training
# input_fn - the classifier function
# steps - data size
# hooks - the logging hook
my_classifier.train(
    input_fn=train_input_fn,
    steps=total_steps,
    hooks=[logging_hook])

# Test the data
# save parameters as above
eval_input_fn = tf.estimator.inputs.numpy_input_fn(
    x={'x': test_X},
    y=test_Y,
    num_epochs=1,
    shuffle=True)

# Evaluate the test result
eval_results = my_classifier.evaluate(input_fn=eval_input_fn)

# Export the model to given folder for repeated usage
my_classifier.export_saved_model("/home/adam/tensorflow_model/export", serving_input_receiver_fn)
import base64
import io
import json

import mediapipe as mp
import tensorflow as tf

import flask
from flask import request
import logging
from PIL import Image

from google.protobuf.json_format import MessageToDict
from useModel import image_processing


logging.basicConfig(level=logging.INFO, format='%(asctime)s [%(levelname)s] %(message)s')

app = flask.Flask(__name__)
app.config['counter'] = 0
app.config['state'] = 'standing'
app.config['nose'] = None
app.config['r_hip'] = None
app.config['l_hip'] = None
app.config['flag'] = True

#  initialize image processing variables
model = tf.keras.models.load_model('medium_squat_cnn_model6.h5')
mpPose = mp.solutions.pose
pose = mpPose.Pose()
mpDraw = mp.solutions.drawing_utils


@app.route('/init-counter', methods=['POST'])
def init_counter():
    app.config['counter'] = 0
    return "Counter initialized successfully"


@app.route('/analyze-image', methods=['POST'])
def analyze_image():
    image_file = request.files['image']
    image_data = image_file.read()

    processed_image, label, counter, state, nose, r_hip, l_hip, flag, landmarks = image_processing(
        image_data, model, mpPose, pose, mpDraw, app.config['counter'], app.config['state'], app.config['nose'],
        app.config['r_hip'],
        app.config['l_hip'], app.config['flag']
    )

    if counter is not None:
        app.config['counter'] = counter
        app.config['state'] = state
        app.config['nose'] = nose
        app.config['r_hip'] = r_hip
        app.config['l_hip'] = l_hip
        app.config['flag'] = flag

    if landmarks is not None:
        landmarks_dict = [MessageToDict(l) for l in landmarks]
    else:
        landmarks_dict = None

    # prepare the response data
    response_data = {
        'label': label,
        'counter': counter,
        'landmarks': landmarks_dict,
        'image_data': None
    }

    # convert the processed image to PIL image
    processed_image_pil = Image.open(io.BytesIO(processed_image))

    # convert the PIL image to bytes and encode as Base64 string
    image_bytes = io.BytesIO()
    processed_image_pil.save(image_bytes, format='JPEG')
    image_bytes.seek(0)
    encoded_image = base64.b64encode(image_bytes.getvalue()).decode('utf-8')

    # include the encoded image in the response data
    response_data['image_data'] = encoded_image

    # return the response as JSON
    return json.dumps(response_data)


# this commands the script to run in the given port
if __name__ == '__main__':
    app.run(host="0.0.0.0", port=5002, debug=True)

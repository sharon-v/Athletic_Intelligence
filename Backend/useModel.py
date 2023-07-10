import cv2
import mediapipe as mp
import numpy as np
import logging

logging.basicConfig(level=logging.INFO, format='%(asctime)s [%(levelname)s] %(message)s')
server_url = "http://10.0.0.12:5002"

label = "Squat...."
# labels arr, in each label index counts how many times apeared during training - label 6 is error
training_labels = np.array([0, 0, 0, 0, 0, 0, 0])

labels = {
    0: "Normal",
    1: "Uneven back",
    2: "Feet too narrow",
    3: "Buttock too high",
    4: "Knees too wide",
    5: "Knees inward"
}


def get_key_by_value(value):
    for key, val in labels.items():
        if val == value:
            return key
    return None  # value not found in the dictionary


def make_landmark_timestep(results):
    cur_lm = []
    for id, lm in enumerate(results.pose_landmarks.landmark):
        cur_lm.append(lm.x)
        cur_lm.append(lm.y)
        cur_lm.append(lm.z)
    return cur_lm


def draw_landmark_on_image(mpDraw, results, img, mpPose):
    mpDraw.draw_landmarks(img, results.pose_landmarks, mpPose.POSE_CONNECTIONS,
                          mpDraw.DrawingSpec(color=(255, 255, 255), thickness=22, circle_radius=14),
                          mpDraw.DrawingSpec(color=(255, 255, 255), thickness=10, circle_radius=13))

    paint_error_circles(results, img, mpPose)
    return img


def paint_error_circles(results, img, mpPose):
    h, w, c = img.shape

    if get_key_by_value(label) == 1:
        p1 = (int(results.pose_landmarks.landmark[mpPose.PoseLandmark.RIGHT_SHOULDER].x * w),
              int(results.pose_landmarks.landmark[mpPose.PoseLandmark.RIGHT_SHOULDER].y * h))
        p2 = (int(results.pose_landmarks.landmark[mpPose.PoseLandmark.LEFT_SHOULDER].x * w),
              int(results.pose_landmarks.landmark[mpPose.PoseLandmark.LEFT_SHOULDER].y * h))
        p3 = (int(results.pose_landmarks.landmark[mpPose.PoseLandmark.RIGHT_HIP].x * w),
              int(results.pose_landmarks.landmark[mpPose.PoseLandmark.RIGHT_HIP].y * h))
        p4 = (int(results.pose_landmarks.landmark[mpPose.PoseLandmark.LEFT_HIP].x * w),
              int(results.pose_landmarks.landmark[mpPose.PoseLandmark.LEFT_HIP].y * h))
        cv2.circle(img, p1, 20, (0, 0, 255), cv2.FILLED)
        cv2.circle(img, p2, 20, (0, 0, 255), cv2.FILLED)
        cv2.circle(img, p3, 20, (0, 0, 255), cv2.FILLED)
        cv2.circle(img, p4, 20, (0, 0, 255), cv2.FILLED)

    elif get_key_by_value(label) == 2:
        p1 = (int(results.pose_landmarks.landmark[mpPose.PoseLandmark.RIGHT_ANKLE].x * w),
              int(results.pose_landmarks.landmark[mpPose.PoseLandmark.RIGHT_ANKLE].y * h))
        p2 = (int(results.pose_landmarks.landmark[mpPose.PoseLandmark.LEFT_ANKLE].x * w),
              int(results.pose_landmarks.landmark[mpPose.PoseLandmark.LEFT_ANKLE].y * h))
        cv2.circle(img, p1, 20, (0, 0, 255), cv2.FILLED)
        cv2.circle(img, p2, 20, (0, 0, 255), cv2.FILLED)

    elif get_key_by_value(label) == 3:
        p3 = (int(results.pose_landmarks.landmark[mpPose.PoseLandmark.RIGHT_HIP].x * w),
              int(results.pose_landmarks.landmark[mpPose.PoseLandmark.RIGHT_HIP].y * h))
        p4 = (int(results.pose_landmarks.landmark[mpPose.PoseLandmark.LEFT_HIP].x * w),
              int(results.pose_landmarks.landmark[mpPose.PoseLandmark.LEFT_HIP].y * h))
        cv2.circle(img, p3, 20, (0, 0, 255), cv2.FILLED)
        cv2.circle(img, p4, 20, (0, 0, 255), cv2.FILLED)

    elif get_key_by_value(label) == 4:
        p1 = (int(results.pose_landmarks.landmark[mpPose.PoseLandmark.RIGHT_KNEE].x * w),
              int(results.pose_landmarks.landmark[mpPose.PoseLandmark.RIGHT_KNEE].y * h))
        p2 = (int(results.pose_landmarks.landmark[mpPose.PoseLandmark.LEFT_KNEE].x * w),
              int(results.pose_landmarks.landmark[mpPose.PoseLandmark.LEFT_KNEE].y * h))
        cv2.circle(img, p1, 20, (0, 0, 255), cv2.FILLED)
        cv2.circle(img, p2, 20, (0, 0, 255), cv2.FILLED)

    elif get_key_by_value(label) == 5:
        p1 = (int(results.pose_landmarks.landmark[mpPose.PoseLandmark.RIGHT_KNEE].x * w),
              int(results.pose_landmarks.landmark[mpPose.PoseLandmark.RIGHT_KNEE].y * h))
        p2 = (int(results.pose_landmarks.landmark[mpPose.PoseLandmark.LEFT_KNEE].x * w),
              int(results.pose_landmarks.landmark[mpPose.PoseLandmark.LEFT_KNEE].y * h))
        cv2.circle(img, p1, 20, (255, 0, 0), cv2.FILLED)
        cv2.circle(img, p2, 20, (255, 0, 0), cv2.FILLED)


def draw_class_on_image(label, counter, img):
    font = cv2.FONT_HERSHEY_SIMPLEX
    bottomLeftCornerOfText = (30, 130)
    fontScale = 5
    fontColor = (255, 255, 255)
    thickness = 14
    lineType = 2

    # draw black outline
    outlineThickness = 20
    cv2.putText(img, label,
                bottomLeftCornerOfText,
                font,
                fontScale,
                (0, 0, 0),
                outlineThickness,
                lineType)

    # draw white text
    cv2.putText(img, label,
                bottomLeftCornerOfText,
                font,
                fontScale,
                fontColor,
                thickness,
                lineType)

    strCounter = "Count: " + str(counter)

    bottomLeftCornerOfText = (30, 240)
    fontScale = 3
    thickness = 10

    # draw black outline
    outlineThickness = 16
    cv2.putText(img, strCounter,
                bottomLeftCornerOfText,
                font,
                fontScale,
                (0, 0, 0),
                outlineThickness,
                lineType)

    # draw white outline
    cv2.putText(img, strCounter,
                bottomLeftCornerOfText,
                font,
                fontScale,
                fontColor,
                thickness,
                lineType)
    return img


def print_success():
    major = np.argmax(training_labels)
    total = sum(training_labels)
    print(f"major training label = {major} | sum = {total}\n"
          f"success = {round(training_labels[0] * 100 / total, 1)} %")


def detect(model, lm_list):
    lm_list = np.array(lm_list)
    lm_list = np.expand_dims(lm_list, axis=-1)
    results = model.predict(lm_list, verbose=0)

    num_labels = 6
    global label
    # labels = {
    #     0: "Normal",
    #     1: "Uneven back",
    #     2: "Feet too narrow",
    #     3: "Buttock too high",
    #     4: "Knees too wide",
    #     5: "Knees inward"
    # }
    # print(f"results= {results}")

    label_index = np.argmax(results) % num_labels
    # print(label_index)

    if label_index < len(labels):
        label = labels[int(label_index)]
    else:
        label = 'Invalid label index'
        label_index = 6
    # print(label)

    training_labels[label_index] += 1
    return label


def is_squatting(landmarks, prev_nose, prev_r_hip, prev_l_hip):
    mpPose = mp.solutions.pose

    nose = landmarks[mpPose.PoseLandmark.NOSE]
    left_hip = landmarks[mpPose.PoseLandmark.LEFT_HIP]
    right_hip = landmarks[mpPose.PoseLandmark.RIGHT_HIP]

    # check if the person is lowering their hips by comparing their current position to the previous position
    if nose.y + 0.02 < prev_nose.y and right_hip.y + 0.02 < prev_r_hip.y and left_hip.y + 0.02 < prev_l_hip.y:
        return True
    else:
        return False


def image_processing(image_to_analyze: np.ndarray, model, mpPose, pose, mpDraw, counter, state, nose, r_hip, l_hip,
                     flag):
    # model = tf.keras.models.load_model('medium_squat_cnn_model6.h5')

    lm_list = []
    label = None
    # mpPose = mp.solutions.pose
    # pose = mpPose.Pose()
    # mpDraw = mp.solutions.drawing_utils

    img = cv2.imdecode(np.frombuffer(image_to_analyze, dtype=np.uint8), cv2.IMREAD_UNCHANGED)

    if img is None:
        # print_success()
        raise Exception("Image is empty")

    imgRGB = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)

    results = pose.process(imgRGB)

    try:
        landmarks = results.pose_landmarks

        # prevents the camera from closing if there are no landmarks
        if landmarks is not None:
            landmarks = landmarks.landmark

            if flag:
                nose = landmarks[mpPose.PoseLandmark.NOSE.value]
                r_hip = landmarks[mpPose.PoseLandmark.RIGHT_HIP.value]
                l_hip = landmarks[mpPose.PoseLandmark.LEFT_HIP.value]
                flag = False

            if is_squatting(landmarks, nose, r_hip, l_hip):
                if state == "standing":
                    # user just started squatting
                    state = "squatting"
            else:
                if state == "squatting":
                    # user just finished a squat
                    state = "standing"
                    counter += 1

            # print(f"nose={nose.y}\nrhip={r_hip.y}\nlhip={l_hip.y}\n")
            # check if the user is squatting

            cv2.putText(img, "", tuple(np.multiply(
                [landmarks[mpPose.PoseLandmark.LEFT_HIP.value].x, landmarks[mpPose.PoseLandmark.LEFT_HIP.value].y],
                [640, 480]).astype(int)), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 1, cv2.LINE_AA)

            if results.pose_landmarks:
                c_lm = make_landmark_timestep(results)

                lm_list.append(c_lm)
                label = detect(model, lm_list)
                lm_list = []

                img = draw_landmark_on_image(mpDraw, results, imgRGB, mpPose)

            img = draw_class_on_image(label, counter, img)
            cv2.imshow("Image", img)

            img = cv2.cvtColor(img, cv2.COLOR_RGB2BGR)

            # convert image to bytes
            _, buffer = cv2.imencode('.jpg', img)

            image = buffer.tobytes()
            # cv2.imwrite("a.png", img)

            nose = landmarks[mpPose.PoseLandmark.NOSE.value]
            r_hip = landmarks[mpPose.PoseLandmark.RIGHT_HIP.value]
            l_hip = landmarks[mpPose.PoseLandmark.LEFT_HIP.value]
            return image, label, counter, state, nose, r_hip, l_hip, flag, landmarks
        return img, "Body not detected", None, None, None, None, None, None, None

    except ValueError:
        # label = "Body not detected"
        # print(label)
        return img, "Body not detected", None, None, None, None, None, None, None

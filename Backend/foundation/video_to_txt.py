import cv2
import mediapipe as mp
import pandas as pd
import os
import re

# landmarks_names = [
#     'nose',
#     'left_eye_inner', 'left_eye', 'left_eye_outer',
#     'right_eye_inner', 'right_eye', 'right_eye_outer',
#     'left_ear', 'right_ear',
#     'mouth_left', 'mouth_right',
#     'left_shoulder', 'right_shoulder',
#     'left_elbow', 'right_elbow',
#     'left_wrist', 'right_wrist',
#     'left_pinky_1', 'right_pinky_1',
#     'left_index_1', 'right_index_1',
#     'left_thumb_2', 'right_thumb_2',
#     'left_hip', 'right_hip',
#     'left_knee', 'right_knee',
#     'left_ankle', 'right_ankle',
#     'left_heel', 'right_heel',
#     'left_foot_index', 'right_foot_index',
# ]


def make_landmark(results, tag):
    cur_lm = []
    for id, lm in enumerate(results.pose_landmarks.landmark):
        cur_lm.append(lm.x)
        cur_lm.append(lm.y)
        cur_lm.append(lm.z)

    # add the tag to the end of the list
    cur_lm.append(tag)

    return cur_lm


# 0. initialization
mp_draw = mp.solutions.drawing_utils
mp_pose = mp.solutions.pose

videos_names = os.listdir('..\DataCollect\Squat_video\Medium_Resized')

count = 0
for video_name in videos_names:
    lm_list = list()

    if 'mp4' not in video_name:
        continue

    label = video_name[:video_name.find('.')]
    # find video number from file name
    vid_number = int(re.search("\d+", video_name).group())

    # 1. load video from dataset
    vid = cv2.VideoCapture(f'..\DataCollect\Squat_video\Medium_Resized\{label}.mp4')

    # extract video label from csv
    df = pd.read_csv("medium_video_labels.csv", header=None)

    tag = int(df.iloc[vid_number - 1])    
    print(f"num = {vid_number}, tag = {tag}")

    # 2. use mediapipe to detect
    with mp_pose.Pose(min_detection_confidence=0.5, min_tracking_confidence=0.5) as pose:
        while True:
            ret, frame = vid.read()

            if not ret:
                count += 1
                break

            image = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
            results = pose.process(frame)
            image = cv2.cvtColor(image, cv2.COLOR_RGB2BGR)

            if results.pose_landmarks:
                landmarks = results.pose_landmarks.landmark

                lm = make_landmark(results, tag=tag)  # TODO: change tag to real values

                lm_list.append(lm)
                # draw on frame
                mp_draw.draw_landmarks(image, results.pose_landmarks, mp_pose.POSE_CONNECTIONS,
                                       mp_draw.DrawingSpec(color=(255, 255, 255), thickness=2, circle_radius=2),
                                       mp_draw.DrawingSpec(color=(255, 255, 255), thickness=2, circle_radius=2)
                                       )
            cv2.imshow(label, image)

            if cv2.waitKey(1) == 27:
                break

    print(f"count = {count}")
    vid.release()
    cv2.destroyAllWindows()

    # 4. save data to file
    df = pd.DataFrame(lm_list)

    df.to_csv(f'../DataCollect/Data TXT/Squat_medium_txt/{label}.txt', header=None, index=None, sep=',',
              mode='w')

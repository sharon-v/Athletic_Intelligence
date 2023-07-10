import cv2
import os

videos_names = os.listdir('..\DataCollect\Squat_video\Medium')

count_video = 0
for video_name in videos_names:
    if 'mp4' not in video_name:
        continue
    label = video_name[:video_name.find('.')]
    count_video += 1
    new_label = 'squat_' + str(count_video)

    try:
        vid = cv2.VideoCapture(f'..\DataCollect\Squat_video\Medium\{label}.mp4')

        # check if the video file is opened successfully
        if not vid.isOpened():
            raise Exception(f"Could not open the video file: {label}.mp4")

        # your existing code here
    except Exception as e:
        print(f"Skipping video file {label}.mp4: {e}")
        continue

    frame_width = int(vid.get(3))
    frame_height = int(vid.get(4))
    fps = vid.get(5)
    size = (frame_width, frame_height)

    new_vid = cv2.VideoWriter(f'..\DataCollect\Squat_video\Medium_Resized\{new_label}.mp4', cv2.VideoWriter_fourcc(*'mp4v'),
                              fps, (576, 320))

    # open all the original videos
    while True:
        ret, frame = vid.read()

        if not ret:
            break

        new_frame = cv2.resize(frame, (576, 320))

        new_vid.write(new_frame)

        cv2.imshow(label, new_frame)

        if cv2.waitKey(30) == 27:
            break

    vid.release()
    new_vid.release()
    cv2.destroyAllWindows()

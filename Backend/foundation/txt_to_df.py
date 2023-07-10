txt_files = [f for f in os.listdir('../DataCollect/Data TXT/Squat_medium_txt') if f.endswith('.txt')]

# create an empty list to store the data from all txt files
all_data = []

# loop through the list of txt files
for txt_file in txt_files:
    if os.stat(f'../DataCollect/Data TXT/Squat_medium_txt/{txt_file}').st_size == 0:
        # skip this txt file if it is empty
        continue

    # read the txt file into a dataframe
    df = pd.read_csv(f'../DataCollect/Data TXT/Squat_medium_txt/{txt_file}', header=None)

    # append the data from the txt file to the all_data list
    all_data.append(df)

# concatenate all the data into a single dataframe
final_df = pd.concat(all_data)

# save the dataframe to a csv file
final_df.to_csv('../DataCollect/Data TXT/Squat_medium_txt/medium_squat_data.csv', index=None, header=None)

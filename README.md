# RecipeAI
Easy recipe finder using real-time object detection on Android.

## Overview
This demo app allows users to find quick recipes using their camera. The object detection model utilizes YOLOv5 ...

Note: This app was built and modified from the **Object Detection Android App Tutorial Using YOLOv5**(https://github.com/AarohiSingla/TFLite-Object-Detection-Android-App-Tutorial-Using-YOLOv5.git)

## Usage
To build the pretrained demo in Andorid Studio, select "Open an existing Android Studio project" and navigate to the recipeAI/recipeAI directory. Then, connect a devise and press 'run'. ....

## Model Training

* Create a directory in Google Drive called `food_detection`.

* Add the **[training dataset]()** and **[label_map.pbtxt]()** to `food_detection`.

* Open `RecipeAI_train.ipynb` and follow the notebook instructions.

* To use the newly trained model, download `food_detect.tflite` from `model_checkpoints/tflite_model/` and move it to the _assets_ folder in Android Studio. It should replace the existing pretrained model.

## Custom food classes
#### Preparing the data
* Create a directory in Google Drive called `food_detection`.

#### Training the model


#### Adding the model to Android Studio



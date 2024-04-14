<img src="CookingAlly.gif" width="250" height="250"/>

# CookingAlly
Easy recipes finder using real-time object detection on Android.

## Overview
This demo app allows users to find quick recipes using their camera. The object detection model utilizes YOLOv5 
and Tensorflow Lite.

## Development Environment 
 - Roboflow
 - Yolov5
 - Tensorflow 1.15.2
 - Android Studio Giraffe

## The custom dataset

Number of classes: 5 (carrot, cheese, egg, potato, zucchini)  
Number of images: 2360  
Split: Train set 75%, Valid set 16%, Test set 9%  

Our custom dataset can be found at https://universe.roboflow.com/project-0tja1/ingredients-cfeum

## The app

There are four main activities:

- __CameraActivity__: manages interactions with the device's camera.
- __Detector Activity__: uses the TensorFlow API to detect objects in an image.
- __MainActivity__: incorporates the core logic of the application, including reading recipes from an Excel file, selecting recipes based on detected ingredients and creating the log.
- __ViewRecipeActivity__: handles the organized display of recipe features, including the associated image.

## Usage
To build the pretrained demo in Android Studio, import the project and navigate to the cookingAlly/cookingAlly directory. Then, connect a device, frame the ingredients, and view the recommended recipes.





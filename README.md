# RecipeAI
Easy recipes finder using real-time object detection on Android.

## Overview
This demo app allows users to find quick recipes using their camera. The object detection model utilizes YOLOv5 
and Tensorflow Lite.

## Development Environment 
 - Roboflow
 - Yolov5
 - Tensorflow 1.15.2 (da controllare)
 - Android Studio Giraffe

## The custom dataset

Number of classes: 5 (carrot, cheese, egg, potato, zucchini)  
Number of images: 2360  
Split: Train set 75%, Valid set 16%, Test set 9%  
[MainActivity.java](app%2Fsrc%2Fmain%2Fjava%2Forg%2Ftensorflow%2Flite%2Fexamples%2Fdetection%2FMainActivity.java)
Our custom dataset can be found at https://universe.roboflow.com/project-0tja1/ingredients-cfeum

## Usage
To build the pretrained demo in Android Studio, import the project and navigate to the recipeAI/recipeAI directory. Then, connect a device and press 'run'. ....





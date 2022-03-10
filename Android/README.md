# Android Application

Heart Rate Monitoring System app for Android - A part of the IXN programme

## Installation
1. Clone this repository and import into **Android Studio**
```bash
git clone git@github.com/<reponame>.git
```
2. Download OpenCV 4.2.0 latest stable release for Windows platform.
3. Go to the official OpenCV website: https://opencv.org/ -> **Resources** -> **Releases** and click on the Windows platform.
4. Go to Downloads and extract the zip file
5. After creating a new project go to **File** -> **New** -> **Import-Module**
6. Select the OpenCV/SDK/java folder
7. Go to **File** -> **Project Structure** -> **Dependencies** in **All Dependencies** folder click on the + icon then add the module dependency.
8. Click on the app and then select the OpenCV dependency and then next then OK
9. Go to **app** -> **New** -> **folder** -> **JNI** folder then hange Folder Location then rename `src/main/JNI` to `src/main/jniLibs` then finish.

## Configuration
### Android SDK:
1. Go to File -> Project Structure -> SDK Location
2. Select hte location of the Android SDK

### OpenCV SDK:
1. Go to the `gradle.properties`
2. Update the `opencvsdk=` to `[folder containing OpenCV SDK]`
3. Go to the `settings.gradle`
4. Update the `opencvsdk=` to `[folder containing OpenCV SDK]`

### Build
1. To build lick on **Build** -> **Make Project**


### Deploy
1.Enable Debug Mode on your Android phone:
* Open the Settings app.
* Select System.
* Scroll to the bottom and select About phone.
* Scroll to the bottom and tap Build number 7 times.
* Return to the previous screen to find Developer options near the bottom.
* Scroll down and enable USB debugging.
2. Connect your phone to the PC
3. Click on **Run** -> **Run app**

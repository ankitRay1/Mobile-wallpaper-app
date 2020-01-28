# Documentation of Wallsplash android application

## Hi Please follow below steps to configure and make changes in application source code.

### Installation

### Before you start

Current documentation was created to help you with quick installation and configuration of Mobile-wallpaper-app.

Please, read it carefully to avoid most of potential problems with incorrect configuration.

Below chapter describes how to install Android SDK and Android Studio. You don’t have to install Android Studio, but it’s better. The project can be built without Android Studio, using Gradle and Android SDK. Gradle is a build system used for building final APK file.

Once you will have activated the theme you will need to change some of the pages and settings for optimal performance. That’s why, please, do not contact the Support center beforehand. Read firstly the documentation, implement all the steps following the instructions and only after that, if the issues persist, contact us.

## Download and Install Java
If you have downloaded and instal
led Java JDK then move to next point.

Download & Install Java from [here](http://www.oracle.com/technetwork/java/javase/downloads/index.html)

![jav download](https://lh5.googleusercontent.com/2GErEku78-3VOkiVKorHMvjYKgFnkAASqOIUAxjWCTzNZa9IoxJsRXNUK8BmwSIVBClBcaFWWmVQsURtWsj10y736bIvMo7ox7vSPysRPwIoHZxcTS4e97guWMC302vF0-JkTfhq)



Download Java by clicking on Java Platform (JDK)

## Install Android Studio
Download & Install Android Studio from here [Download Android Studio](https://developer.android.com/sdk/index.html)

![images2](https://lh3.googleusercontent.com/dwdx4gcYn53DjH8ehVC6LasviLtQwL1WtoKhdbtj9MfKylKcLtX5ILEwJccoq6qV6Z7Vmm7Tt84BGhPz5GiAuOzmbmGRD803CQkFfCA66bJ1qrhRUnEkO3p_Aj0y4LPE9XkdjTrb)



Run Android SDK Manager and [download necessary SDK packages](https://developer.android.com/sdk/installing/adding-packages.html), make sure that you have installed Android SDK Tools, Android SDK Platform-tools, Android SDK Build-tools, Android Support Repository, Android Support Library and Google Play services.

Now you should be able to open/edit the Android project and build APK.

## How to create Unsplash account
Go to https://unsplash.com/ link create your account you can see your home page.

Then go to Api/Developers

![images3](https://imgur.com/esfIHPW.png)

enter image description here

You can see below page
![ahahah](https://imgur.com/r7sVtNV.png)
Click on Your Apps button then you can display the below page.

![img6](https://imgur.com/BQBp8qC.png)

Click on New Application then display the below page.
![ajajj](https://imgur.com/NvqVXjO.png)
check all checkbox then you can visible Accept Terms button click on Accept Terms button you can display below dialog.

![xnnxnx](https://imgur.com/O7iEidZ.png)

Enter your Application name and also add Description then you can access your Access Key and Secret key.

![snssn](https://imgur.com/Gw5D6fj.png)
Use **Access Key** in Application.

## How to Import Project

To Import project you need to start android studio which you already downloaded, Once android studio start follows below steps to import the project in the android studio.

## Follow steps to import project
First of all go to the location where you have placed City Directory code

1.Please extract the zip file in the same location

2.Now select “Import Project (Gradle, Eclipse ADT, etc.)” from Android Studio.

3.Browse location where you have placed NearBy Code and press “OK” button.

4.Now, wait till android studio complete the Importing project successfully.
1[hshhs](https://lh6.googleusercontent.com/RavW1fPMDJlsiWYsnpxjQbfocbOpRbb9_hqAm8hyu8lT_sTQoNhAk7LKwnuNNxYGqh7ydL_18iXaWwrsKosBlWohktXvx5QvdV1Nkdf-sy4WzikkXx8nyKcCIKs5c1-9dRHqu13Y)

Import project

By clicking on import project you will get file browser dialog, select your code location and press OK button.

## Customize Project

### Project Structure
After importing project successfully you will get project structure like below image
![ss](https://imgur.com/pwK5Seh.png)

### Project Structure
Within each Android app module, files are shown in the following groups:

#### manifest
Contains the [AndroidManifest.xml](https://developer.android.com/guide/topics/manifest/manifest-intro.html) file.

#### java
Contains the Java source code files, separated by package names, including JUnit test code.

## res
Contains all non-code resources, such as XML layouts, UI strings, and
bitmap images, divided into corresponding sub-directories. For more
information about all possible resource types, see [Providing
Resources](https://developer.android.com/guide/topics/resources/providing-resources.html).

To know more about project structure please go through this link
[Android Project
Structure](https://developer.android.com/studio/projects/)

Change App icon
---------------

### Place your app icon inside mipmap folder -\> app\\src\\main\\res\\mipmap\\

Icon name should be “**ic\_launcher.png**”

Change application package name
-------------------------------

### In Android Studio, you can do this:

-   

For example, if you want to change `com.example.app` to
`my.awesome.game`, then:

1.  In your ***Project pane***, click on the little gear icon ( ![Gears
    icon](https://i.stack.imgur.com/lkezT.png) )

2.  Uncheck / De-select the `Compact Empty Middle Packages` option

![enter image description here](https://imgur.com/c8Tbwyu.png)

3.  Your package directory will now be broken up in individual
    directories

4.  Individually select each directory you want to rename, and:

    -   Right-click it
    -   Select `Refactor`
    -   Click on `Rename`
    -   In the Pop-up dialog, click on `Rename Package` instead of
        Rename Directory
    -   Enter the new name and hit **Refactor**
    -   Click **Do Refactor** in the bottom
    -   Allow a minute to let Android Studio update all changes
    -   *Note: When renaming `com` in Android Studio, it might give a
        warning. In such case, select***Rename All**

    ![Enter image description here](https://imgur.com/iI8d02N.png)

5.  Now open your ***Gradle Build File*** (`build.gradle` - Usually
    `app` or `mobile`). Update the `applicationId` in the
    `defaultConfig` to your new Package Name and Sync Gradle, if it
    hasn’t already been updated automatically:

    ![Refactor Directories](https://imgur.com/H42rrsD.png)

6.  You may need to change the `package=` attribute in your manifest.

7.  Clean and Rebuild.

8.  ***Done!*** Anyway, Android Studio needs to make this process a
    little simpler.

Change Application Name
-----------------------

Go to App -\> res -\> value -\> String.xml -\> Change in below line

![](https://imgur.com/ZDz7PvL.png)

Change Accesskey In Application
-------------------------------

Go to App -\> Java -\> wallsplash -\> itechnotion -\> com -\> retrofit
-\> config.java -\> replace your ***Accesskey*** in below line

![](https://imgur.com/Msy18yb.png)

Change Firebase Database Name
-----------------------------

Go to App -\> Java -\> wallsplash -\> itechnotion -\> com -\> retrofit
-\> config.java -\> replace your firebase database link in below line\
 ![](https://imgur.com/iHH141P.png)

Admob Configuration
===================

How to Integrate Google AdMob in your App

Introduction
------------

[AdMob](https://developers.google.com/admob/) is a multi platform mobile
ad network that allows you to monetize your android app. By integrating
AdMob you can start earning right away. It is very useful particularly
when you are publishing a free app and want to earn some money from it.

Integrating AdMob is such an easy task that it takes no more than 5mins.
In this article, we’ll build a simple app with two screens to show the
different types of ads that AdMob supports.

Creating Ad Units
-----------------

NOTE: AdMob admin interface changes quite often. The below steps to
create Ad Unit IDs might differ from time to time.

Want to know more?? Follow this link -
[https://developers.google.com/admob/android/quick-start](https://developers.google.com/admob/android/quick-start)​

1.  Sign into your [AdMob](https://apps.admob.com/) account.

2.  Create a new App by giving the package name of the app you want to
    integrate AdMob. Once the App is created, you can find the APP ID on
    the dashboard which looks like ca-app-pub-XXXXXXXXX\~XXXXXXXXX.

3.  Select the newly created App and click on ADD AD UNIT button to
    create a new ad unit.

4.  Select the ad format and give the ad unit a name.

5.  Once the ad unit is created, you can notice the Ad unit ID on the
    dashboard. An example of ad unit id look like
    ca-app-pub-066XXXXXXX/XXXXXXXXXXX

![](https://i.imgur.com/rD7JwU7.png)

> ### Create ad unit for banner ads

After creating banner ad unit and interstitial ad unit you have to place
you adUnit id in string.xml file.

### app \> res \> values \> string.xml {#app--res--values--string.xml}

![](https://i.imgur.com/KxsEsMC.png)

OneSignal integration
=====================

OneSignal is a high volume and reliable push notification service for
websites and mobile applications. To know more about onesignal follow
this link - [https://onesignal.com/](https://onesignal.com/)

* * * * *

Follow this link to configure onesignal in your app
[https://documentation.onesignal.com/docs/android-sdk-setup](https://documentation.onesignal.com/docs/android-sdk-setup)​

### Place your OneSignal App ID in build.gradle(Module:app) file in the project

![enter image description here](https://imgur.com/BD9sUl1.png)

    manifestPlaceholders = [  
        onesignal_app_id : 'PLACE YOUR ONESIGNAL APP ID HERE',  
        // Project number pulled from dashboard, local value is ignored.
        onesignal_google_project_number: 'REMOTE'  

]

Replace your generated google-service.json inside App Directory in
project

![](https://lh3.googleusercontent.com/a4cUgLQWuDK7ugHfD64RJHxFtXqy7r6hmmohjWxwWHnPMCjDYbJ8_ghy7ofTEx3bAmmtGhdLx9qvwjNj6SwLlzfcrtb3iyCwpCd5gnLwCLR7PFpEW_4V6kEE-CMTYhoIngSDqsYF)

Firebase Database Setup
=======================

Please refere below link for create new app in firebase.\

[http://mariechatfield.com/tutorials/firebase/step1.html](http://mariechatfield.com/tutorials/firebase/step1.html)

Firebase Email Auth Setup
=========================

Please refere below link for enable Email auth in firebase.

[https://firebase.google.com/docs/auth/web/email-link-auth](https://firebase.google.com/docs/auth/web/email-link-auth)

Facebook Login for Android - Quickstart
=======================================

The Facebook SDK for Android enables people to sign into your app with
Facebook Login. When people log into your app with Facebook they can
grant permissions to your app so you can retrieve information or perform
actions on Facebook on their behalf.

Please follow below link and perform action as per described\

([https://firebase.google.com/docs/auth/android/facebook-login\#next\_steps](https://firebase.google.com/docs/auth/android/facebook-login#next_steps))

Place your facebook app id inside string file in your code

    <string name="facebook_app_id" translatable="false">PLACE YOUR FACEBOOK APP ID HERE</string>
    <string name="fb_login_protocol_scheme" translatable="false">fb[PLACE YOUR FACEBOOK APP ID HERE]</string>  

Resource Credits
================

Thanks for provide great material and we are very appreciates to our
assets provider.

* * * * *

Images sources (CC0)\
 [Pexels.com\
](https://www.pexels.com/photo-license/)[[Unsplash.com](http://Unsplash.com)

PushNotification\
 [https://onesignal.com/](https://onesignal.com/)

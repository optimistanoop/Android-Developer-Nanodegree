# Nanodegree-Project-0-My-App-Portfolio
Nanodegree- Project 0: My App Portfolio
This app will show my future projects by clicking on the respective button.

# UdacityPopularMovies

##API key
- Add your key to the app/build.gradle (variable defined there).

##Stage 1
The core experiences of this app are --

- Upon launch, presents the user with an grid arrangement of movie posters.
- Allows user to change sort order via a setting.
- The sort order can be by most popular, or by top rated.
- Allows the user to tap on a movie poster and transition to a details screen with additional information such as:
   * Original title
   * Movie poster image thumbnail
   * A plot synopsis (called overview in the api)
   * User rating (called vote_average in the api)
   * Release date


## Stage 2
New features added to the  core experiences of this app are --

- Allowed users to view and play trailers ( either in the youtube app or a web browser).
- Allowed users to read reviews of a selected movie.
- Also allowed users to mark a movie as a favorite in the details view by tapping a button(star). This is for a local        movies   collection that you will maintain and does not require an API request*.
- Modified the existing sorting criteria for the main view to include an additional pivot to show their favorites            collection.
- Optimized your app experience for tablet.

# UdacityPopularMovies

##API key
- Add your key to the app/build.gradle (variable defined there).

## Stage 2
New features added to the  core experiences of this app are --

- Allowed users to view and play trailers ( either in the youtube app or a web browser).
- Allowed users to read reviews of a selected movie.
- Also allowed users to mark a movie as a favorite in the details view by tapping a button(star). This is for a local        movies   collection that you will maintain and does not require an API request*.
- Modified the existing sorting criteria for the main view to include an additional pivot to show their favorites            collection.
- Optimized your app experience for tablet.

# Udacity Stock Hawk
Stock Hawk app for Udacity Nanodegree.

Stock Hawk gives you an opportunity to diagnose problems and practice improving apps. These skills are vital to building apps users will love.

Diagnosing issues with existing apps is key to working on large apps or continuing projects in Android. Being aware of the common pitfalls in app design frees a developer to produce novel app experiences without making the same mistakes over and over.


##  Project Specification

* Diagnosed existing issues with  app.
* Accessible to sight-impaired users.
* Localized for distribution in other countries.
* Handled error cases in Android.
* A widget suppoort for best app experience.
* Line graph charting for stock data.


## Libraries

* [EazeGraph](https://github.com/blackfizz/EazeGraph)
* [FloatingActionButton](https://github.com/makovkastar/FloatingActionButton)
* [MaterialLoadingProgressBar](https://github.com/lsjwzh/MaterialLoadingProgressBar)
* [OkHttp](https://github.com/square/okhttp)
* [Schematic](https://github.com/SimonVT/schematic)


# Gradle for Android and Java Final Project

In this project, you will create an app with multiple flavors that uses
multiple libraries and Google Could Endpoints. The finished app will consist
of four modules. A Java library that provides jokes, a Google Could Endpoints
(GCE) project that serves those jokes, an Android Library containing an
activity for displaying jokes, and an Android app that fetches jokes from the
GCE module and passes them to the Android Library for display.

## Why this Project

As Android projects grow in complexity, it becomes necessary to customize the
behavior of the Gradle build tool, allowing automation of repetitive tasks.
Particularly, factoring functionality into libraries and creating product
flavors allow for much bigger projects with minimal added complexity.

##What Will I Learn?

You will learn the role of Gradle in building Android Apps and how to use Gradle to manage apps of increasing complexity. You'll learn to:

* Add free and paid flavors to an app, and set up your build to share code between them
* Factor reusable functionality into a Java library
* Factor reusable Android functionality into an Android library
* Configure a multi project build to compile your libraries and app
* Use the Gradle App Engine plugin to deploy a backend
* Configure an integration test suite that runs against the local App Engine development server

##How Do I Complete this Project?

### Step 0: Starting Point

This is the starting point for the final project, which is provided to you in the [course repository](https://github.com/udacity/ud867/tree/master/FinalProject).
It contains an activity with a banner ad and a button that purports to tell a
joke, but actually just complains. The banner ad was set up following the
instructions here:

https://developers.google.com/mobile-ads-sdk/docs/admob/android/quick-start

You may need to download the Google Repository from the Extras section of the
Android SDK Manager.

When you can build an deploy this starter code to an emulator, you're ready to
move on.

### Step 1: Create a Java library

Your first task is to create a Java library that provides jokes. Create a new
Gradle Java project either using the Android Studio wizard, or by hand. Then
introduce a project dependency between your app and the new Java Library. If
you need review, check out demo 4.01 from the course code.

Make the button display a toast showing a joke retrieved from your Java joke
telling library.

### Step 2: Create an Android Library

Create an Android Library containing an Activity that will display a joke
passed to it as an intent extra. Wire up project dependencies so that the
button can now pass the joke from the Java Library to the Android Library.

For review on how to create an Android library, check out demo 4.03. For a
refresher on intent extras, check out;

http://developer.android.com/guide/components/intents-filters.html

### Step 3: Create GCE Module

This next task will be pretty tricky. Instead of pulling jokes directly from
our Java library, we'll set up a Google Cloud Endpoints development server,
and pull our jokes from there. Follow the instructions in the following
tutorial to add a Google Could Endpoints module to your project:

https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints

Introduce a project dependency between your Java library and your GCE module,
and modify the GCE starter code to pull jokes from your Java library. Create
an Async task to retrieve jokes. Make the button kick off a task to retrieve a
joke, then launch the activity from your Android Library to display it.

### Step 4: Add Functional Tests

Add code to test that your Async task successfully retrieves a non-empty
string. For a refresher on setting up Android tests, check out demo 4.09.

### Step 5: Add a Paid Flavor

Add free and paid product flavors to your app. Remove the ad (and any
dependencies you can) from the paid flavor.


# Rubric

### Required Components

* Project contains a Java library for supplying jokes
* Project contains an Android library with an activity that displays jokes passed to it as intent extras.
* Project contains a Google Cloud Endpoints module that supplies jokes from the Java library. Project loads jokes from GCE module via an async task.
* Project contains connected tests to verify that the async task is indeed loading jokes.
* Project contains paid/free flavors. The paid flavor has no ads, and no unnecessary dependencies.

### Required Behavior

* App retrieves jokes from Google Cloud Endpoints module and displays them via an Activity from the Android Library.

# Go Ubiquitous
===================================

Synchronizes weather information from OpenWeatherMap on Android Phones and Tablets. Used in the Udacity Advanced Android course.

Pre-requisites
--------------
Android SDK 21 or Higher
Build Tools version 21.1.2
Android Support AppCompat 22.2.0
Android Support Annotations 22.2.0
Android Support GridLayout 22.2.0
Android Support CardView 22.2.0
Android Support Design 22.2.0
Android Support RecyclerView 22.2.0
Google Play Services GCM 7.0.0
BumpTech Glide 3.5.2

## Project Specification

* Works well in every wear.
* Designed similar to Design Mocks given in requirement.
* Displays the current time.
* Displays temperature according to preferences.
* Uses icons for different day type.

Getting Started
---------------
This sample uses the Gradle build system.  To build this project, use the
"gradlew build" command or use "Import Project" in Android Studio.
Put your api key in gradle file (generated from openweathermap.org) 

Support
-------

- Google+ Community: https://plus.google.com/communities/105153134372062985968
- Stack Overflow: http://stackoverflow.com/questions/tagged/android

Patches are encouraged, and may be submitted by forking this project and
submitting a pull request through GitHub. Please see CONTRIBUTING.md for more details.

License
-------
Copyright 2015 The Android Open Source Project, Inc.

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements.  See the NOTICE file distributed with this work for
additional information regarding copyright ownership.  The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License.  You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.

# udacityCapstoneStage-1 -

The design and planning of the app of own idea.

## Please check prototype_and_design_doc.pdf and updated-prototype directory for updated product design doc and prototype. 

prototype_and_design_doc.pdf is the updated version of App prototype designed for App showcase event to be held at Google India , Bangalore in September.


# SHG
SHG stands for self help group, which is mainly operated in indian rural areas.
**SHG** is an android app that allows a user to manage expenses, groups and calculate interest. The app utilizes [Splitwise REST API](http://dev.splitwise.com/).

## This app requires API key
#### To get API key, follow these steps.
* Head over to [Splitwise](http://splitwise.com/).
* Signup and loging to your account.
* Go into myAccount.
* In "Advanced features section", click on your apps.
* In "Build your own app" section , create a new Application by filling required details, after this API key will be generated.
* Put your "Consumer Key" and "Consumer Secret" in app/build.gradle at specified place.
* Now, you are ready to explore.

## Instructions for using this app
* Open app and login to splitwise account.
* Add some friends by menu options.
* your friend will get a mail for signup.
* Ask your friend to signup.
* Add some expenses in any friend detail view.
* Add some groups by same menu options.
* Add group members (friend) to group by group detail view menu.
* Add some expenses in any group detail view.
* Check for total balances for a group or a friend.
* your resistered friend can also use this app to add his expenses, and will be simplified to the balance amount between friends.
* All friend can see their real time balance and all expenses in any group and friend view. 

### Here is my [Capstone Stage-1- Design](https://github.com/optimistanoop/Capstone-Project) repo. 

###The following **required** functionality is completed:

* User can **manage expenses**.
* User can **manage friends**.
* User can **manage expense groups**.
* User can **calculate interest**.
* User validated through **oAuth**.
* App uses third party APIs to save its data on server.
* Multiple user share same real time data for better user experience and data persistency. 

## Open-source libraries used

- [Android Async HTTP](https://github.com/loopj/android-async-http) - Simple asynchronous HTTP requests with JSON parsing
- [Picasso](http://square.github.io/picasso/) - Image loading and caching library for Android

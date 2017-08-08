
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

## Credits
[Sharewise](https://github.com/sharewise/Sharewise)

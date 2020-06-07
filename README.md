# Loft

## About

This an app built as a way to show case things that I have learnt as part of **#31DaysOfKotlin** challenge by Google Developers. Though the challenge was for 31 days, I started out late and the app as effectively build in a week 😑, but the learning was continuous and will be so in future!
**About Loft android application :** It is an app that uses **Tensorflow lite** library to classify a image using a quarantined **MobilenetV1** model. The classification is done completely on device though the application needs the user to logged in.

## New Things I learnt
- It was my first experience dealing with Tensorflow lite model on android, but it was really simple and easy with all the [examples](https://github.com/tensorflow/examples/tree/master/lite/examples) that library already provides.
- Though the I had previous experience in developing android apps, the [code labs](https://eventsonair.withgoogle.com/events/kotlin/resources) were extremely helpful in figuring out bits and pieces of the app.
- To be make the challenge bit more challenging I used preview release of **Android Studio Canary Build** .
- The application supports both dark and light modes (Not going to lie but new preview release of android studio did most of the work here 😑, though I struggled in other places with preview release 🤔).
- I tried out **FireBase** authentication for the first time and implemented `Sign In` and `Sign Up` using ViewModels and  LiveData.

## Test credentials
There is not a lot of validation when you register in the app, but if you want test credentials you can use :
   > Username: test@test.com
   > Password: 1234567

  **OR**
   > Username: test2@test.com
   > Password: 1234567

## Screen captures
### Light Mode: 
![test](https://raw.githubusercontent.com/BharathBillawa/Loft/master/images/light4.png =x350) 
![test](https://raw.githubusercontent.com/BharathBillawa/Loft/master/images/light5.png =x350)
![test](https://raw.githubusercontent.com/BharathBillawa/Loft/master/images/light3.png =x350) 
![test](https://raw.githubusercontent.com/BharathBillawa/Loft/master/images/light2.png =x350) 

### Dark Mode: 

![test](https://raw.githubusercontent.com/BharathBillawa/Loft/master/images/dark1.png =x350) 
![test](https://raw.githubusercontent.com/BharathBillawa/Loft/master/images/dark2.png =x350) 
![test](https://raw.githubusercontent.com/BharathBillawa/Loft/master/images/dark3.png =x350)
![test](https://raw.githubusercontent.com/BharathBillawa/Loft/master/images/dark4.png =x350) 

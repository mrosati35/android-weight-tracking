In this project, I created an Android phone app for a user to log their weight over time and track their progress towards their weight goal. The app includes user login, database reads and writes for persistence, and SMS notification (with user permission) when the user has reached their weight goal.

There is a login screen allowing the user to enter their username and password, as well as a screen allowing the user to create their account if they are starting the app for the first time. The main screen of the app shows a table of the user's weight, letting them see their progress over time, and there is a floating action button allowing them to enter their current weight, which is logged to the database along with the date and time.

Coding the app was a matter of creating the necessary screen layouts in Android Studio's graphical designer and then wiring the controls to Java code to implement their functions. As a longtime developer of desktop software in Java, and a regular user of visual UI builders like JFormDesigner, this was largely a familiar process.

I tested the application manually, entering various inputs to make sure that the application behaved as desired for each of its features. I would have liked to apply a more test-driven approach, as I am accustomed to doing in desktop and back-end Java development, but that will be a future learning opportunity in Android development.


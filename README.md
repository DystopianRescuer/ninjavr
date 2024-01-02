# NinjaVR Software
This project was from a few years ago, it was commissioned for a family project, it was intended to be a "Point Of Rent" for Oculus Quest devices, or in general any Android-based headset.

# Some features
  - Direct connection to the glasses via Android Debug Bridge (ADB) wirelessly, which among other things allows: checking and changing the games, locking and unlocking the device, checking the status of the battery, the connection to the power supply, the status of the internet connection, and transmit in real time to monitors (more on that later), as well as virtually anything you can do through a non-root shell.

  - The connection with ADB is made automatically through a mini-script that is embedded in the program, basically it consists of waiting for an Android device to be connected via USB, after that, the program sends the command to start through the console communication via TCP, once the device accepts it, it is informed that the device can now be disconnected and the wireless connection would be initiated successfully, this entire process does not take more than 5 seconds.

  - Accept card payments through the Clip API and a reader, as easy as configuring the program with your API-Key and that's it. (You require pending transactions mode enabled, for more information see the Clip documentation)

  - Manage rentals in a simple way, for each connected viewer you have a "control center", where you can do things like check the status of the glasses, unlock them with a password (I designed a locking system in which if the glasses are in use while there is no rental in progress the program does not allow it), turn them off, restart them, or start a rental, at the beginning of each rental you select the game and the number of minutes, then the payment comes.

  - The app is also responsible for printing the ticket based on a design I also authored (it was quite tedious tbh, design was never my thing), the program is responsible for recording each transaction in a database.

As you can tell just from the context, this project was never intended to be open source, but rather to run on a single dinosaur PC intended to be on the stand counter and operated by my brother, so you will find a lot of things very hard coded (I have tried to reduce some), some logic that forces the program to run on Windows, this is because for the correct functioning of card payments I had to set up a webhook with Flask in Python, the program executes this code every time it starts and listens to its outputs (this happens with every successful card payment), but when you want to close this script, behind it it sends a taskkill, a Windows command, it is easily adaptable/correctable

The program detects each video output is connected and saves it; When a new headset is connected through ADB and there is a video output available, the program "links" between both, so when the headset is for rent, what the user is seeing will automatically be projected to the screen in real time.

# Technologies used
  - *Maven* to manage dependencies
  - *Java-FX* because doing it with swing would have been torture
  - *Android Debug Bridge* for connection to the headset
  - *MySQL* and *my-sql-connector* for database
  - *JNA* to have access to the native libraries of the OS and be able to control some things such as size and display of the mirror of each viewer.
  - *Python* with *Flask* to set up a reverse API and receive successful (or unsuccessful) transactions made with clip.
  - *Pktroit* as a service that allowed me to receive clip requests despite not having an approved IP and therefore not being able to open ports, without mentioning the issue of security, works from reverse connections.
Some other dependency that can be consulted in the Maven pom

# Why isn't this finished?
For reasons that I would perhaps tell you if we were having a coffee but I don't think it is necessary to tell here, the business did not end up prospering, so I ended up with this version which I would call a quite advanced Alpha, the most important features are already done and work In short, it could have been a good management software for Android-based devices, but it's almost done, if it works for you, go ahead :).

# Remain to be polished/finished
  - If you plan to completely polish the logging issue in a big way, the original idea is that in principle it would be something almost symbolic; do not save blank passwords (hash and salt them), and if you are not going to set up a more solid infrastructure, save the credentials locally; make registration more automatic.
  - A telegram bot to monitor the status of the stand while you are not present or give authorization to certain actions.
  - Improve the interface a lot, design is not my thing®.
  - Direct access, if you have a privileged user, to the shell of the connected devices.
  - Implementing NinjaCard, it was intended to be the business loyalty card, it was an idea to combine this project with one of my passions, RFID, in the end it was left pending.
  - Polish the logic a lot, in short it is not the most efficient program I have ever done, I was in the middle of the selection process for the most in-demand university in my country, going through a somewhat difficult stage in my life, and learning and doing things for others sides.
I may end up implementing this entire list myself, however, as I am more dedicated than ever to my studies and passions, I probably won't be doing it anytime soon.

# What do I need to run this?
  - Windows 8 or higher
  - Java
  - Python
  - ADB
  - Pktriot

# How can I use this alpha in the state it is?
Make sure you have the dependencies up here and have them win your path.
Once done you can clone the repo and compile it (compiled version in the releases section)
If you have followed the steps you should be able to see the login when you open the jar.
Use the debug credentials test:admin to access without a user base set up.
The use should be a combination of intuition and certain instructions that will appear on the screen.
The program will NOT work with any viewer other than Oculus Quest 2, to extend compatibility you will have to modify the code a little (FIXED I THINK).

# Some screenshots

Login screen
<img width="960" alt="login" src="https://github.com/DystopianRescuer/ninjavr/assets/10383549/110e3ba2-22ec-4d4c-adb1-d7e11aa03f65">

Dashboard
<img width="960" alt="dashboard" src="https://github.com/DystopianRescuer/ninjavr/assets/10383549/0a6b594c-9b56-4bc8-a26a-2f8ed4eaf2bb">

Device selector
<img width="960" alt="renta" src="https://github.com/DystopianRescuer/ninjavr/assets/10383549/b49dd88b-2b6f-4193-bff3-597328ad04c6">

NinjaCard (not implemented)
<img width="960" alt="ninjacard" src="https://github.com/DystopianRescuer/ninjavr/assets/10383549/84682375-f179-44a1-a094-4e37bec45d40">

Some settings (very few lol)
<img width="960" alt="gestion" src="https://github.com/DystopianRescuer/ninjavr/assets/10383549/68ea978b-7959-40cb-83c8-5c32d2a8e8e2">

A ticket example, it was created for a test so it does not have accurate info.<br>
<img width="129" alt="ticket" src="https://github.com/DystopianRescuer/ninjavr/assets/10383549/e14178e4-3bf9-42e7-bcc1-338719080540">


Anyone who likes to use this software is free to do it, even the logos and anything that is embedded in this code, the trademark was never registered. Have Fun.

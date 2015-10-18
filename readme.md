#Android Screencast

![Android Screencast](https://github.com/frostymarvelous/AndroidScreencast/raw/master/screenshots/screencast.png)

Android Screencast allows you to use control android device from your computer.

I needed to access my phone with a damaged screen so I wrote this as I couldn't find anything that fit
my needs.

The only tool that came close was a 6 year old unmaintained project [androidscreencast](https://code.google.com/p/androidscreencast/)
that needed root. 

I took pointers from the project and started this one in JavaFX.

It does not require root but utilizes adb commands to send input to the device so should work.

##Requirements

You need adb installed.

Your device must be developer enabled and discoverable by adb.

##Features

Currently, it mirrors your screen and takes mouse input (clicks and swipes).

##Limitations

Framerate is terrible. It works currently for my needs, but I plan on working on it.

Does not take keyboard input yet. Not difficult to implement and will be the next feature implemented.

It isn't robust as I cobbled it together in an afternoon of mork, but more work will be done to make it solid in due course.
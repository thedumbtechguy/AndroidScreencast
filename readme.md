#Android Screencast

![Android Screencast](https://github.com/frostymarvelous/AndroidScreencast/raw/master/screenshots/screencast.png)

Android Screencast allows you to control your android device from your computer.

I needed to access my phone with a damaged screen so I wrote this as I couldn't find anything that fit
my needs.

The only tool that came close was a 6 year old unmaintained project [androidscreencast](https://code.google.com/p/androidscreencast/)
that needed root. 

I took pointers from the project and started this one in JavaFX.

It does not require root but utilizes adb commands to send input to the device so should work.

##Requirements

You need adb installed.

Your device must be developer enabled and discoverable by adb.

Tested on a device running 4.4.2

##Features

- Screen mirroring (very low frame rate)
- Simulated Clicks, Swipes and Long Presses using mouse input
- Keyboard input

###Special Keys
- ESC : Back
- F1 : Menu 
- F2 : Back
- F3 : Home
- F10 : Power Button
- F11 : Camera

###Normal Keys
- All text keys
- Arrow keys
- Enter, Backspace, Delete and Tab
- Home, End, Page Up and Page Down

##Limitations

Frame rate is terrible. It works currently for my needs, but I plan on working on it.

It isn't robust as I cobbled it together in an afternoon of work, but more work will be done to make it solid in due course.

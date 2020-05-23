# Gif Vault:  A Desktop App for Organizing your Favorite Gifs!

About
===========
Gif vault is a search and offline archiving solution for gif files from multiple gif providers. (Only the Giphy platform is available at the moment...)

Prerequisites
===========
* [JDK 11](https://openjdk.java.net/). Java 11 or newer, Gif Vault uses Java language version 11, you will need a OpenJDK 11 installed on your machine.
* [VLC](https://www.videolan.org/vlc/index.html) VLC media player (version 3+) needs to be installed in order for this software to run. (VLC is a free and open source cross-platform multimedia player and framework that plays most multimedia files as well as DVDs, Audio CDs, VCDs, and various streaming protocols.)

Build
===========
* [clone the Video Converter repository](https://help.github.com/articles/cloning-a-repository/)      
* run one of the following commands from the project root:  
**Build and install the jars in the local repository executing all the unit tests:**   
`./gradlew build`
or
`./gradlew run` to run / debug

Run
===========
Once you run the build command in the `distributions` directory of the `build` output folder you will find a zip file. Unzip it somewhere and run it using the provided script in the `bin` subdirectory. 

Usage
===========
Gif Vault will store all favorited Gifs in a folder on your home directory by default. You can change the folder location by going into the file menu and then selecting the settings option.


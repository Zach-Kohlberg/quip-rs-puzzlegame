javac *.java -Xlint:unchecked -Xlint:deprecation
jar cfe Client.jar Client *.class
jar cfe Server.jar Server *.class
jar cfe Playback.jar PlaybackGUI *.class
@echo off
echo Finished!
timeout -1
@echo off

call wipe.bat

if exist "%JAVA7_HOME%\bin\javac.exe" (
    "%JAVA7_HOME%\bin\javac" -encoding UTF-8 *.java
    exit 0
)

if exist "%JAVA_HOME%\bin\javac.exe" (
    "%JAVA_HOME%\bin\javac" -encoding UTF-8 *.java
    exit 0
)

javac -encoding UTF-8 *.java

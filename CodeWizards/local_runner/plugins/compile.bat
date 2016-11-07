@echo off

call wipe.bat

if not "%JAVA8_64_HOME%"=="" (
    if exist "%JAVA8_64_HOME%\bin\javac.exe" (
        "%JAVA8_64_HOME%\bin\javac" -encoding UTF-8 *.java
        exit 0
    )
)

if not "%JAVA8_32_HOME%"=="" (
    if exist "%JAVA8_32_HOME%\bin\javac.exe" (
        "%JAVA8_32_HOME%\bin\javac" -encoding UTF-8 *.java
        exit 0
    )
)

if not "%JAVA8_HOME%"=="" (
    if exist "%JAVA8_HOME%\bin\javac.exe" (
        "%JAVA8_HOME%\bin\javac" -encoding UTF-8 *.java
        exit 0
    )
)

if not "%JAVA_HOME%"=="" (
    if exist "%JAVA_HOME%\bin\javac.exe" (
        "%JAVA_HOME%\bin\javac" -encoding UTF-8 *.java
        exit 0
    )
)

javac -encoding UTF-8 *.java

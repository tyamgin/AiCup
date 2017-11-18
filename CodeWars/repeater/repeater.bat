set JAVA_BIN="

if "%JAVA8_64_HOME%" neq "" (
    if exist "%JAVA8_64_HOME%\bin\java.exe" (
        set JAVA_BIN="%JAVA8_64_HOME%\bin\"
        goto java-start
    )
)

if "%JAVA_HOME%" neq "" (
    if exist "%JAVA_HOME%\bin\java.exe" (
        set JAVA_BIN="%JAVA_HOME%\bin\"
        goto java-start
    )
)

:java-start
"%JAVA_BIN:"=%java" -Xms1G -Xmx4G -cp ".;*;%~dp0/*" -jar repeater.jar %1

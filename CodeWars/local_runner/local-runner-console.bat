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
start "" "%JAVA_BIN:"=%java" -Xms512m -Xmx1G -jar "G:\Projects\AiCup\CodeWars\local_runner\local-runner.jar" "G:\Projects\AiCup\CodeWars\local_runner\local-runner-console.properties" "G:\Projects\AiCup\CodeWars\local_runner\local-runner-console.default.properties" %*

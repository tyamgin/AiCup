set JAVA_BIN="

if "%JAVA8_64_HOME%" neq "" (
    if exist "%JAVA8_64_HOME%\bin\javaw.exe" (
        set JAVA_BIN="%JAVA8_64_HOME%\bin\"
        goto java-start
    )
)

if "%JAVA_HOME%" neq "" (
    if exist "%JAVA_HOME%\bin\javaw.exe" (
        set JAVA_BIN="%JAVA_HOME%\bin\"
        goto java-start
    )
)

:java-start

set JAVA_PARAMETERS=

if "%NUMBER_OF_PROCESSORS%" neq "" (
    if %NUMBER_OF_PROCESSORS% gtr 4 (
        set JAVA_PARAMETERS= -XX:+UseConcMarkSweepGC
    )
)

start "" "%JAVA_BIN:"=%javaw" -Xms512m -Xmx1G%JAVA_PARAMETERS% -jar "local-runner.jar" local-runner.properties local-runner.default.properties %*

set name=MyStrategy

if not exist %name%.cs (
    echo Unable to find %name%.cs > compilation.log
    exit 1
)

del /F /Q %name%.*exe

set COMPILER_PATH=

if "%MONO_HOME%" neq "" (
    if exist "%MONO_HOME%\bin\mcs.bat" (
        set COMPILER_PATH="%MONO_HOME%\bin\"
    )
)

SetLocal EnableDelayedExpansion EnableExtensions

set FILES=

for %%i in (*.cs) do (
    set FILES=!FILES! %%i
)

for %%i in (Model\*.cs) do (
    set FILES=!FILES! %%i
)

for %%i in (Properties\*.cs) do (
    set FILES=!FILES! %%i
)

call "%COMPILER_PATH:"=%mcs" -o+ /r:System.Numerics.dll -sdk:6 -out:%name%.exe!FILES! 2>compilation.log

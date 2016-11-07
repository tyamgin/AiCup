set name=MyStrategy

if not exist %name%.cs (
    echo Unable to find %name%.cs > compilation.log
    exit 1
)

del /F /Q %name%.*exe

set COMPILER_PATH=

if "%ROSLYN_HOME%" neq "" (
    if exist "%ROSLYN_HOME%\tools\csc.exe" (
        set COMPILER_PATH="%ROSLYN_HOME%\tools\"
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

call "%COMPILER_PATH:"=%csc.exe" /optimize+ /r:System.Numerics.dll /out:%name%.exe!FILES! 1>compilation.log 2>&1

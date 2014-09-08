set name=MyStrategy

if not exist %name%.cs (
    echo Unable to find %name%.cs > compilation.log
    exit 1
)

del /F /Q %name%.mono-exe

SET FILES=

for %%i in (*.cs) do (
    call concatenate %%i
)

for %%i in (Model\*.cs) do (
    call concatenate %%i
)

for %%i in (Properties\*.cs) do (
    call concatenate %%i
)

for /D %%i in (%windir%\Microsoft.NET\Framework\v*) do SET NET_HOME=%%i\

call %NET_HOME%csc.exe /o+ /d:ONLINE_JUDGE /r:System.Numerics.dll /out:%name%.mono-exe %FILES% 1>compilation.log 2>&1
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

call dmcs -define:ONLINE_JUDGE -o+ -sdk:4 -out:%name%.mono-exe %FILES% 2>compilation.log
start java -cp ".;*;%~dp0/*" -jar "D:\Projects\AiCup\CodeHockey\local_runner\local-runner.jar" "D:\Projects\AiCup\CodeHockey\local_runner\local-runner-console.properties"
timeout 1
start old 127.0.0.1 31001 0000000000000000
new 127.0.0.1 31002 0000000000000000
type result.txt
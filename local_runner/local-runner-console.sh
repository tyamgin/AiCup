pushd `dirname $0` > /dev/null
SCRIPTDIR=`pwd`
popd > /dev/null

java -cp ".:*:$SCRIPTDIR/*" -jar "local-runner.jar" false false 3 result.txt true false

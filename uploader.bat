@ECHO OFF
PUSHD %~dp0
java -classpath .;target\uploader.jar be.crydust.uploader.App
POPD
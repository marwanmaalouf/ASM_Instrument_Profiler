cls
echo "Building files ..."
@echo off
javac -d out -cp "./src/libs/*";"./" src/com/asmproj/*.java
javac -d out -cp "./src/libs/*";"./" src/com/asmproj/test/*.java



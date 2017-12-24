cls
@echo off
echo Building files ...
javac -d out -g -parameters -cp "./src/libs/*";"./" src/com/asmproj/*.java
javac -d out -g -parameters -cp "./src/libs/*";"./" src/com/asmproj/test/*.java
echo Build completed
@echo on



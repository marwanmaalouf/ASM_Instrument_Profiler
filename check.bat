cls
echo "Printing class bytecode"
javac -cp "./src/libs/*";"./" Visualizer\Visualize.java 
java  -cp "./src/libs/*";"./Visualizer/" Visualize .\out\com\asmproj\test\Testing_2.class 
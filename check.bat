cls
echo "Printing class bytecode"
javac -cp "./src/libs/*";"./" Visualizer\Visualize.java 
cd Visualizer
java  -cp "../src/libs/*";"./" Visualize \..\out\com\asmproj\Testing.class 
cd ..
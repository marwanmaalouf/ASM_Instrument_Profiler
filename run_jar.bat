java -cp "./src/libs/*";"./out/" com.asmproj.Main instrumented\Testing.jar
java -cp "./out";"./instrumented/" -jar Testing_instrumented

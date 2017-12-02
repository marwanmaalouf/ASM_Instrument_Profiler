mkdir .\instrumented
mkdir .\instrumented\jar
java -cp .\src\libs\*;.\out\ com.asmproj.Main jar\Testing.jar
java -cp .\instrumented\jar\Testing_instrumented.jar;.\out com.asmproj.test.Testing_2

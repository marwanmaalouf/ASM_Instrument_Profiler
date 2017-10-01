# ASMProject

## How to set up using Intellij

Check out from Version control -> Github -> log in -> find ASMProject.git

You might need to add the ASM Jar files to the project:

file -> Project Structure -> Libraries -> add -> java -> select all ASM Jar files
Make sure you download ASM 6 jar files if using JDK 9, but use Opcodes 5 while working.


## How to run using command line
Use the bat files provided:
	_ build.bat: builds the java files and stores them in a root directory 'out'. (Note you need to manually create the out dir at the same level as the build.bat file)
	_ run.bat: runs the instrumenter on the first program (Testing: if statements, function calls)
	- run2.bat: runs the instrumenter on the second program (Testing_2: multiple classes, function call, instances)

## Progress so far:
This part will be used and updated to reflect what has been done/what is supported by the current version:
	. Local variable Def-Use (primitive types and object instances)
	. GETFIELD
	. Array Element Def (int)

## TODOs:
This part reflects the next steps to be addressed (@Elie please put the list you compiled here)
	. m_Signature is giving null
	. PUTFIELD (@Johnny)
	. Array Element Def-Use (primitive types and object instances)
	. <Put the list here>


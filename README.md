# ASMProject

## How to set up using Intellij

Check out from Version control -> Github -> log in -> find ASMProject.git

In some cases, You might need to add the ASM Jar files to the project:  
file -> Project Structure -> Libraries -> add -> java -> select all ASM Jar files found in src/lib

## Running using IntelliJ

Build Testing.java (Right click -> Compile)  
Build Main and provide args, being the filepath to Testing.class or any other class found in out/.. (Do so via config editor)
Run Main  
(Optional) Checkout changes in the decompiled Testing.class under out/..  
Run Testing

## How to run using command line
Use the bat files provided:

	_build.bat: builds the java files and stores them in a root directory 'out'. (Note you need to manually create the out dir at the same level as the build.bat file)
	_run.bat: runs the instrumenter on the first program (Testing: if statements, function calls)
	_run2.bat: runs the instrumenter on the second program (Testing_2: multiple classes, function call, instances)

## Progress so far:
This part will be used and updated to reflect what has been done/what is supported by the current version:

	. Local variable Def-Use (primitive types and object instances)
	. GETFIELD, GETSTATIC
	. Array Element Def
	. INVOKEVIRTUAL, INVOKEINTERFACE

## TODOs:
This part reflects the next steps to be addressed (@Elie please put the list you compiled here)
	
	. PUTFIELD -- @Johnny
	. Array Element Use (primitive types and object instances) -- @Samir
	. <Put the list here>
	
## Useful Links
[Java Bytecode Instructions List](https://en.wikipedia.org/wiki/Java_bytecode_instruction_listings)


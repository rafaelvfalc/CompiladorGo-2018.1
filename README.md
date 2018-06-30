// Generating GoLexicalAnalyzer.java, GoParser.java and sym.java (Lexical and Syntatic analysis)
	
Step 1: Run GeneratorGo.java
Step 2: Go to the terminal, the directory of the 'go.cup' file and run de following command:
	java -jar /home/rafaelf/eclipse-workspace/CompiladorGo-2018.1/src/libs/java-cup-11a.jar -expect 96 -interface -parser GoParser go_2.cup
	
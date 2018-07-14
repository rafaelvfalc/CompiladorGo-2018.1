package lexicalanalyzer.go;

/** Test class to try out the command lexer. */
import java.nio.file.Paths;
import java.io.*;

public class Tester {
	static public void main(String argv[]) {    
    /* Start the parser */
	  
		String rootPath = Paths.get("").toAbsolutePath(). toString();
		String FilePath = rootPath + "/src/lexicalanalyzer/go/program.go";
	  
		try {
			GoParser p = new GoParser(new GoLexicalAnalyzer(new FileReader(FilePath)));
			Object result = p.parse();
		} catch (Exception e) {
			/* do cleanup here -- possibly rethrow e */
			e.printStackTrace();
			System.exit(1);
			
		}
		System.out.println("Programa Sintaticamente Compilado!");
	}
}
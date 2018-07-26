package semanticanalyzer.go;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Paths;

import java_cup.runtime.ComplexSymbolFactory;
import lexicalanalyzer.go.GoLexicalAnalyzer;
import semanticanalyzer.go.exceptions.SemanticException;
import semanticanalyzer.go.Semantic;
import lexicalanalyzer.go.GoSyntaticAnalyzer;

public class SpecificTestSemantic {

	public static void parse(String sourceCode, ComplexSymbolFactory csf) throws Exception {
		
		Semantic.getInstance().clear();
		
		boolean exception = false;
		
		try {
			FileInputStream stream = new FileInputStream(sourceCode);
			Reader reader = new InputStreamReader(stream);
	
			GoLexicalAnalyzer lexer = new GoLexicalAnalyzer(reader, csf);
			// start parsing
			GoSyntaticAnalyzer p = new GoSyntaticAnalyzer(lexer, csf);
			System.out.println("Parsing: " + sourceCode);
			p.parse();
			System.out.println("Parsing finished!");
		} catch (SemanticException e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
			
			if(!sourceCode.contains("FAIL")) {
				throw new Exception(sourceCode + " was not parsed correctly but it should!");
			}
			exception = true;
		}
		
		if(sourceCode.contains("FAIL") && !exception) {
			throw new Exception(sourceCode + " should fail but it was parsed correctly :/");
		}
	}
	
	public static String getAbsolutePath(String path) {
		return Paths.get("").toAbsolutePath().toString() + path; 
	}
	
	public static void main(String[] args) throws Exception {
		ComplexSymbolFactory csf = new ComplexSymbolFactory();
		
		String filePath = getAbsolutePath("/core/src/code_generation/tests/finaltest.go");
		parse(filePath, csf);

		System.out.println("----------------------------------");
		System.out.println("All tests passed!");
		System.out.println("----------------------------------");
	}
}

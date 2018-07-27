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

public class TestSemanticAuxiliar {

	public static void parse(String sourceCode, ComplexSymbolFactory csf) throws Exception {
		
		Semantic.getInstance().clear();
		
		boolean exception = false;
		
		try {
			FileInputStream stream = new FileInputStream(sourceCode);
			Reader reader = new InputStreamReader(stream);
	
			GoLexicalAnalyzer lexer = new GoLexicalAnalyzer(reader, csf);

			GoSyntaticAnalyzer p = new GoSyntaticAnalyzer(lexer, csf);
			System.out.println("Analisando semanticamente: " + sourceCode);
			p.parse();
			System.out.println("Analise semantica finalizada!");
		} catch (SemanticException e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
			
			if(!sourceCode.contains("FAIL")) {
				throw new Exception(sourceCode + " nao foi analizado corretamente");
			}
			exception = true;
		}
		
		if(sourceCode.contains("FAIL") && !exception) {
			throw new Exception(sourceCode + " nao foi analizado corretamente");
		}
	}
	
	public static String getAbsolutePath(String path) {
		return Paths.get("").toAbsolutePath().toString() + path; 
	}
	
}

package lexicalanalyzer.go;

import lexicalanalyzer.go.GoSyntaticAnalyzer;
import lexicalanalyzer.go.GoLexicalAnalyzer;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Paths;

import java_cup.runtime.ComplexSymbolFactory;

public class SyntaticTester {

	//private static String sourcecode = "/src/examples/lexic_syntatic_correct.go";
	private static String sourcecode = "/src/examples/syntatic_error_4.go";

	public static void main(String[] args) {

		try {
			ComplexSymbolFactory csf = new ComplexSymbolFactory();

			String rootPath = Paths.get("").toAbsolutePath().toString();
			String sourceCode = rootPath + sourcecode;
			FileInputStream stream = new FileInputStream(sourceCode);
			Reader reader = new InputStreamReader(stream);

			GoLexicalAnalyzer lexer = new GoLexicalAnalyzer(reader, csf);
			GoSyntaticAnalyzer p = new GoSyntaticAnalyzer(lexer, csf);
		    System.out.println("Analise sintatica rodando: ");
		    p.parse();

		    System.out.println("Analise sintatica finalizada!");
		} catch (Exception e) {
		    e.printStackTrace();
		    System.err.println(e.getMessage());
		}
    }
}


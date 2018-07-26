package lexicalanalyzer.go;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Paths;

import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.Symbol;
import lexicalanalyzer.go.Sym;

public class Tester {
	// Run Generator before executing this script

	//private String path = "/core/src/lexical/tests/comment_and_if.go";
	//private static String path = "/core/src/lexical/tests/keywords_and_operations.go";
	//private static String path = "/core/src/lexical/tests/number_literals.go";
	//private static String path = "/core/src/lexical/tests/rune_and_string_literals.go";
    //private static String path = "/core/src/lexical/tests/variables.go";
	private static String path = "/src/lexicalanalyzer/go/examples/program.go";
	//private static String path = "/core/src/lexical/tests/identifiers.go";
	
	public static void main(String[] args) {
		try {
			ComplexSymbolFactory csf = new ComplexSymbolFactory();

			String rootPath = Paths.get("").toAbsolutePath().toString();
			String sourceCode = rootPath + path;
			FileInputStream stream = new FileInputStream(sourceCode);
			Reader reader = new InputStreamReader(stream);

			GoLexicalAnalyzer lexer = new GoLexicalAnalyzer(reader, csf);

			Symbol symb = lexer.next_token();
			while (symb.sym != Sym.EOF) {
				symb = lexer.next_token();
			}
			System.out.println(symb);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
	}
}
package lexicalanalyzer.go;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Paths;

import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.Symbol;
import lexicalanalyzer.go.Sym;

public class LexicTester {

	private static String path = "/src/examples/lexic_syntatic_correct.go";
	
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
package lexicalanalyzer.go;

import java.io.IOException;
import java.nio.file.Paths;

import java_cup.internal_error;

public class GeneratorGoCup {
	/* Generates a Parser implemented in Java using the Parser.cup file */

	private static String filename = "go.cup";
	private static String subPath = "/src/lexicalanalyzer/go/" + filename;

	public static void main(String[] args) throws internal_error, IOException, Exception {

		// Get root path
		String rootPath = Paths.get("").toAbsolutePath().toString();

		// Parser.cup file
		String filePath = rootPath + subPath;
		String folderPath = rootPath + "/src/lexicalanalyzer/go/";

		String[] options = { "-package", "lexicalanalyzer.go", "-parser", "GoSyntaticAnalyzer", "-destdir", folderPath, "-symbols", "Sym",
				"-interface", filePath };
		java_cup.Main.main(options);
	}
}

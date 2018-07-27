package codegenerator.go;

import java.io.BufferedReader;
import java.io.FileReader;

import java_cup.runtime.ComplexSymbolFactory;
import semanticanalyzer.go.Semantic;
import semanticanalyzer.go.TestSemanticAuxiliar;


public class TestCodeGenerator {
	public static void main(String[] args) throws Exception {
		ComplexSymbolFactory csf = new ComplexSymbolFactory();
		
		String rootPath = TestSemanticAuxiliar.getAbsolutePath("/src/examples/");
		String outputPath = TestSemanticAuxiliar.getAbsolutePath("/src/examples/");

		TestSemanticAuxiliar.parse(rootPath + "codegenerator.go", csf);
		String outputFilename = outputPath + TestCodeGeneratorAuxiliar.removeExtension("result-codegenerator") + ".asm";
		Semantic.getInstance().getCodeGenerator().generateFinalAssemblyCode(outputFilename);
		
		for (int i = 0; i < 25; i++) {
			System.out.println();
		}
		
		try (BufferedReader br = new BufferedReader(new FileReader(rootPath + "result-codegenerator.asm"))) {
			   String line = null;
			   while ((line = br.readLine()) != null) {
			       System.out.println(line);
			   }
		}
		
	}
}

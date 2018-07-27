package codegenerator.go;

import java.io.BufferedReader;
import java.io.FileReader;

import java_cup.runtime.ComplexSymbolFactory;
import semanticanalyzer.go.Semantic;
import semanticanalyzer.go.TestSemanticAuxiliar;


public class TestCodeGenerator {
	public static void main(String[] args) throws Exception {
		ComplexSymbolFactory c = new ComplexSymbolFactory();
		
		String root = TestSemanticAuxiliar.getAbsolutePath("/src/examples/");
		String output = TestSemanticAuxiliar.getAbsolutePath("/src/examples/");

		TestSemanticAuxiliar.parse(root + "codegenerator_4.go", c);
		String outputFilename = output + TestCodeGeneratorAuxiliar.removeExtension("result-codegenerator") + ".asm";
		Semantic.getInstance().getCodeGenerator().generateFinalAssemblyCode(outputFilename);
		
		for (int i = 0; i < 10; i++) {
			System.out.println();
		}
		
		System.out.println("***************");
		System.out.println();
		
		try (BufferedReader br = new BufferedReader(new FileReader(root + "result-codegenerator.asm"))) {
			   String line = null;
			   while ((line = br.readLine()) != null) {
			       System.out.println(line);
			   }
		}
		
		System.out.println();
		System.out.println("***************");
		
	}
}

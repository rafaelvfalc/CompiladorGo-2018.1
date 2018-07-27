package codegenerator.go;

import java_cup.runtime.ComplexSymbolFactory;
import semanticanalyzer.go.Semantic;
import semanticanalyzer.go.TestSemanticAuxiliar;


public class TestCodeGeneratorAuxiliar {
	
	public static String removeExtension(String filename) {
		if (filename.indexOf(".") > 0) {
		   return filename.substring(0, filename.lastIndexOf("."));
		} else {
		   return filename;
		}
	}

	public static String getExtension(String filename) {
		if (filename.indexOf(".") > 0) {
		   return filename.substring(filename.lastIndexOf(".") + 1);
		} else {
		   return "";
		}
	}
	
	public static void main(String[] args) throws Exception {
		ComplexSymbolFactory csf = new ComplexSymbolFactory();
		
		String filePath = TestSemanticAuxiliar.getAbsolutePath("/core/src/code_generation/tests/finaltest.go");
		String outputFilename = TestSemanticAuxiliar.getAbsolutePath("/core/src/code_generation/tests/finaltest.asm");
        
		TestSemanticAuxiliar.parse(filePath, csf);
		Semantic.getInstance().getCodeGenerator().generateFinalAssemblyCode(outputFilename);
		
		System.out.println("----------------------------------");
		System.out.println("All tests passed!");
		System.out.println("----------------------------------");
	}
}


package semanticanalyzer.go;

import java_cup.runtime.ComplexSymbolFactory;

public class TestSemantic {
	public static void main(String[] args) throws Exception {
		
		ComplexSymbolFactory csf = new ComplexSymbolFactory();
		
		String rootPath = TestSemanticAuxiliar.getAbsolutePath("/src/examples/");
		
		TestSemanticAuxiliar.parse(rootPath + "semantic_error_6.go", csf);
		
	}
}

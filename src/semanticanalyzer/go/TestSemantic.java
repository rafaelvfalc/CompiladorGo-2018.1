package semanticanalyzer.go;

import java_cup.runtime.ComplexSymbolFactory;

public class TestSemantic {
	public static void main(String[] args) throws Exception {
		ComplexSymbolFactory csf = new ComplexSymbolFactory();
		
		String rootPath = TestSemanticAuxiliar.getAbsolutePath("/src/examples/");
		
		TestSemanticAuxiliar.parse(rootPath + "semantic1.go", csf);
		
		System.out.println("----------------------------------");
		System.out.println("All tests passed!");
		System.out.println("----------------------------------");
	}
}

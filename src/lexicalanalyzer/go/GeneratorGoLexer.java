
package lexicalanalyzer.go;

import java.io.File;
import java.nio.file.Paths;

public class GeneratorGoLexer {

    public static void main(String[] args) {

        String rootPath = Paths.get("").toAbsolutePath(). toString();
        String subPath = "/src/lexicalanalyzer/go/";

        String file = rootPath + subPath + "go.lex";

        File sourceCode = new File(file);

        JFlex.Main.generate(sourceCode);

    }
}
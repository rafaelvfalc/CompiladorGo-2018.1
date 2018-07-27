package codegenerator.go;

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
	
}


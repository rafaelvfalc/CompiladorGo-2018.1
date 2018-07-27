package codegenerator.go.objects;

public enum OperationsAssembly {
	
	ADD("+", "binOp"),
	SUB("-", "binOp"),
	MUL("*", "binOp"),
	DIV("/", "binOp"),
	MOD("%", "binOp"),
	AND("&&", "binOp"),
	OR("||", "binOp"),
	XOR("^", "binOp"),
	NOT("!", "binOp");

	private final String name;
	private final String type;
	private final String relOperator;

	private OperationsAssembly(String name, String type) {
		this.name = name;
		this.type = type;
		this.relOperator = null;
	}
	
	private OperationsAssembly(String name, String type, String relOperator) {
		this.name = name;
		this.type = type;
		this.relOperator = relOperator;
	}

	public static String mapOp(String op) {
		return getOperator(op).name();
	}
	
	public static boolean isRelop(String op) {
		return getOperator(op).getType().equals("relOp");
	}
	
	public static OperationsAssembly getOperator(String op) {
		for (OperationsAssembly optass : OperationsAssembly.values()) {
			if (optass.name.equals(op)) {
				return optass;
			}
		}
		return null;
	}
	
	public String toString() {
		return this.name;
	}

	public String getType() {
		return type;
	}

	public String getRelOperator() {
		return relOperator;
	}
}

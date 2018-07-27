package semanticanalyzer.go.objects;

import semanticanalyzer.go.objects.Expr;
import semanticanalyzer.go.objects.Type;
import semanticanalyzer.go.objects.EntityWithType;

public class Expr extends EntityWithType {

	private String value;
	private String reg;

	public Expr() {
		super(Type.VOID, null);
		this.value = null;
		this.reg = null;
	}
	
	public Expr(Type type, String value) {
		super(type, null);
		this.value = value;
		this.reg = null;
	}

	public Expr(Type t, String name, String value) {
		super(t, name);
		this.value = value;
		this.reg = null;
	}

	public String getValue() {
		return value;
	}

	public void setType(Type type) {
		if (!getType().equals(Type.UNKNOWN) && !type.equals(getType()))
			System.out.println("Erro semantico, tipo atribuido nao permitido " + type + " e " + getType());
		super.setType(type);
	}

	public void setValue(Expr exp) {
		if (!exp.getType().equals(Type.UNKNOWN))
			setType(exp.getType());
		this.value = exp.getValue();
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String getReg() {
		return reg;
	}
	
	public void setReg(String reg) {
		this.reg = reg;
	}

	public String toString() {
		return "{ Expressao: " + getType() + " " + getValue() + "  }";
	}

}

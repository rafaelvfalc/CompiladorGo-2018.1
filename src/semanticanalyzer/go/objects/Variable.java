package semanticanalyzer.go.objects;

import semanticanalyzer.go.objects.Expr;
import semanticanalyzer.go.objects.Type;
import semanticanalyzer.go.objects.EntityWithType;

public class Variable extends EntityWithType {

	private Expr value;
	
	public Variable(String name) {
		super(Type.UNKNOWN, name);
	}
	
	public Variable(Type type, String name) {
		super(type, name);
		initValue(type);
	}
	
	public Variable(Type type, String name, Expr value) {
		super(type, name);
		this.value = value;
	}
	
	public Expr getValue() {
		return value;
	}
	
	public void setValue(Expr value) {
		this.value = value;
	}
	
	public void setType(Type type) {
		initValue(type);
		super.setType(type);
	}
	
	private void initValue(Type type) {
		if(type == type.FLOAT32 || type == type.INT) {
			this.value = new Expr(type, "0");
		} else if (type == type.STRING) {
			this.value = new Expr(type, "\"\"");
		}
	}
	
	public String toString() {
		return "Variavel: " + getName() + ", tipo: " + getType() + ", valor: " + value; 
	}
}
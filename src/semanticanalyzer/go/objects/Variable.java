package semanticanalyzer.go.objects;

import semanticanalyzer.go.objects.Expression;
import semanticanalyzer.go.objects.Type;
import semanticanalyzer.go.objects.TypedEntity;

public class Variable extends TypedEntity {

	private Expression value;
	
	public Variable(String name) {
		super(Type.UNKNOWN, name);
	}
	
	public Variable(Type type, String name) {
		super(type, name);
		initValue(type);
	}
	
	public Variable(Type type, String name, Expression value) {
		super(type, name);
		this.value = value;
	}
	
	public Expression getValue() {
		return value;
	}
	
	public void setValue(Expression value) {
		this.value = value;
	}
	
	public void setType(Type type) {
		initValue(type);
		super.setType(type);
	}
	
	private void initValue(Type type) {
		if(type == type.FLOAT32 || type == type.INT) {
			this.value = new Expression(type, "0");
		} else if (type == type.STRING) {
			this.value = new Expression(type, "\"\"");
		}
	}
	
	public String toString() {
		return "Variavel: " + getName() + ", tipo: " + getType() + ", valor: " + value; 
	}
}
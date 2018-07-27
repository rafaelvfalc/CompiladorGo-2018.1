package semanticanalyzer.go.objects;

import semanticanalyzer.go.objects.NamedEntity;
import semanticanalyzer.go.objects.Type;

public class TypedEntity extends NamedEntity {
	
	private Type type;
	
	public TypedEntity(Type type, String name) {
		super(name);
		this.type = type;
	}
	
	public Type getType() {
		return this.type;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
}
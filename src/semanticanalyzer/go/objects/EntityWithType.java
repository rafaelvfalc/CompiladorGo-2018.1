package semanticanalyzer.go.objects;

import semanticanalyzer.go.objects.EntityWithName;
import semanticanalyzer.go.objects.Type;

public class EntityWithType extends EntityWithName {
	
	private Type type;
	
	public EntityWithType(Type type, String name) {
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
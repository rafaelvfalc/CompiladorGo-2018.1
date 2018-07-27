package semanticanalyzer.go.objects;

import java.util.ArrayList;
import java.util.List;

import codegenerator.go.CodeGenerator;
import semanticanalyzer.go.exceptions.ExceptionSemanticError;

public class Func extends EntityWithScope {
	
	private Type returnType = Type.VOID;
	private ArrayList<Variable> parameters;
	
	private Expr returnedExpression = new Expr();
	private boolean seenReturn = false;

	private Integer labels = null;

	public Func(String name, ArrayList<Variable> parameters) throws ExceptionSemanticError {
		super(name);
		
		if (parameters != null) {
			this.parameters = parameters;
		} else {
			this.parameters = new ArrayList<Variable>();	
		}
		
		for (Variable parameter : this.parameters) {
			addParameter(parameter);
		}
	}
	
	public Func(String name) throws ExceptionSemanticError {
		this(name, null);
	}
	
	public void addParameter(Variable v) throws ExceptionSemanticError {
		if(getVariables().containsKey(v.getName())) {
			throw new ExceptionSemanticError("Variavel ja declarada " + v.toString());
		}
		
		parameters.add(v);
		addVariable(v);
	}
	
	public Type getReturnType() {
		return returnType;
	}
	
	public List<Variable> getParameters() {
		return parameters;
	}
	
	public List<Type> getParameterTypes() {
		List<Type> pTypes = new ArrayList<>();
		for (int i = 0 ; i < parameters.size() ; i++)
			pTypes.add(parameters.get(i).getType());
		return pTypes;
	}

	public void setReturnType(Type type) {
		this.returnType = type;
	}
	
	public Integer getLabels() {
		return labels;
	}
	
	public void setLabels(Integer labels) {
		this.labels = labels;
	}

	/* Checks if the function returned what it was supposed to */
	public void validateReturnedType() throws ExceptionSemanticError {
		System.out.println(returnedExpression);
		if (!returnedExpression.getType().equals(returnType))
			throw new ExceptionSemanticError("Funcao " + getName() + " era pra ter retornado " + returnType + " mas esta retornando " + returnedExpression.getType());
	}

	public void setReturnedExpression(Expr e) {
		if(!seenReturn) {
			returnedExpression = e;
			seenReturn = true;
		}
	}
	
	public Expr getReturnedExpression() {
		return returnedExpression;
	}

	public void initializeParameters(Type type, CodeGenerator cg) throws ExceptionSemanticError {
		for(Variable v: parameters) {
			if(v.getType() == Type.UNKNOWN) {
				v.setType(type);
				cg.parameterDeclaration(v);
			}
		}	
	}
	
	@Override
	public String toString() {
		return "{ Funcao: " + getName() + " " + getReturnType() + " " + parameters + " }";
	}
}
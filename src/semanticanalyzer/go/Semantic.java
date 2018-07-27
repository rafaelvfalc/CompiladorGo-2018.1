package semanticanalyzer.go;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import semanticanalyzer.go.objects.Type;
import semanticanalyzer.go.exceptions.SemanticException;
import semanticanalyzer.go.objects.Expression;
import semanticanalyzer.go.objects.Function;
import semanticanalyzer.go.objects.IfElse;
import semanticanalyzer.go.objects.ScopedEntity;
import semanticanalyzer.go.objects.Variable;

import codegenerator.go.CodeGenerator;

public class Semantic {

	private static Semantic semantic = new Semantic();	
	private static CodeGenerator codeGenerator = new CodeGenerator();	
	private Map<String, Variable> variables = new HashMap<>();
	private Map<String, Function> functions = new HashMap<>();
	private List<String> varNamesBuffer = new ArrayList<>();
	private List<Expression> expBuffer = new ArrayList<>();
	private List<Expression> expBufferBeforeAssign = new ArrayList<>();
	private Stack<ScopedEntity> scopeStack = new Stack<>();
		

	private Semantic() {}
	
	public CodeGenerator getCodeGenerator() {
		return codeGenerator;
	}
	
	public static Semantic getInstance() {
		return semantic;
	}

	public Map<String, Variable> getVariables() {
		return variables;
	}
	
	public void addExpression(Expression v) {
		expBuffer.add(v);
	}

	public void transferExpBuffer() {
		expBufferBeforeAssign.clear();
		expBufferBeforeAssign.addAll(expBuffer);
		expBuffer.clear();
	}

	public void clear() {
		varNamesBuffer.clear();
		
		expBuffer.clear();
		expBufferBeforeAssign.clear();
		
		variables.clear();
		functions.clear();
		scopeStack.clear();
	}

	private void clearBuffers() {
		varNamesBuffer.clear();
		expBuffer.clear();
		expBufferBeforeAssign.clear();
	}
	
	private void throwSemanticException(String message) throws SemanticException {
		clear();
		throw new SemanticException(message);
	}
	
	public void printVars() {
		for (String varName : this.variables.keySet()) {
			System.out.println("Name: " + varName + ", Type: " + this.variables.get(varName).getType());
		}
	}
	
	public boolean checkVariableAllScopes(String name) {
		Set<String> allVariables = new HashSet<String>();
		allVariables.addAll(variables.keySet());
		for (int i = 0 ; i < scopeStack.size(); i++) {
			allVariables.addAll(scopeStack.get(i).getVariables().keySet());
		}
		return allVariables.contains(name);
	}
	
	public void addVarName(String varName) {
		varNamesBuffer.add(varName);
	}
	
	public void addVariable(Variable var) throws SemanticException {
		if (checkVariableInCurrentScope(var.getName()))
			throwSemanticException("Variable " + var.getName() + " ja declarada dentro desse escopo.");
		
		if (!scopeStack.isEmpty()) {
			System.out.println("Adicionando variavel a escopo especifico: " + var);
			scopeStack.peek().addVariable(var);
		} else {
			if(functions.containsKey(var.getName())) {
				throwSemanticException(var.getName() + " redeclarada nesse bloco.");
			}
			System.out.println("Adicionando variavel no escopo main: " + var);
			variables.put(var.getName(), var);
		}

		codeGenerator.variableDeclaration(var);
				
		System.out.println(variables);
	}
	
	public boolean checkVariableInCurrentScope(String name) {
		if (scopeStack.isEmpty())
			return variables.containsKey(name);
		else {
			return scopeStack.peek().getVariables().containsKey(name);
		}	
	}
	
	public Variable getVariable(String varName) throws SemanticException {
		try {
			if (scopeStack.isEmpty()) {
				return variables.get(varName);
			} else {
				for (int i = scopeStack.size()-1; i >= 0; i--) {
					if(scopeStack.get(i).getVariables().containsKey(varName)) {
						return scopeStack.get(i).getVariables().get(varName);
					}
				}
				
				if(variables.containsKey(varName)) {
					return variables.get(varName);
				}
				
			}
		} catch(NullPointerException e) {
			throwSemanticException("Variavel " + varName + " nao foi declarada.");
		}
		
		throwSemanticException("Variavel " + varName + " nao foi declarada.");
		return null;
	}

	public Expression calculateUnaryExpr(String op, Expression expr) throws SemanticException {
		expr = assignTypeIfNeeded(expr);

		validateUnaryOperation(op, expr.getType());
		String exprValue = op + expr.getValue();
		String exprName = expr.getName() == null ? null : "Var: " + expr.getName();

		Expression resultingExpr = new Expression(expr.getType(), exprName, exprValue);
		resultingExpr.setReg(expr.getReg());

		Object obj = expressionToObject(expr);
		resultingExpr = codeGenerator.generateUnaryCode(obj, resultingExpr, op);

		return resultingExpr;
	}

	private void validateUnaryOperation(String op, Type exprType) throws SemanticException {
		switch (exprType) {
		case BOOL:
			if (op == "+" || op == "-") {
				throwSemanticException("Operando invalido " + op + " para o tipo " + exprType.toString());
			}
			break;
		case INT:
			if (op == "!") {
				throwSemanticException("Operando invalido " + op + " para o tipo " + exprType.toString());
			}
			break;
		case FLOAT32:
			if (op == "!") {
				throwSemanticException("Operando invalido " + op + " para o tipo " + exprType.toString());
			}
			break;
		default:
			throwSemanticException("Operando invalido " + op + " para o tipo " + exprType.toString());
		}
	}

	public Expression calculateExpr(Expression e1, String op, Expression e2) throws SemanticException {
		Type resultingType = validateBinOperation(e1, op, e2);
		String exprValue = e1.getValue() + op + e2.getValue();
		String exprName = formatExpressionName(e1, e2);
		

		/* Code generation */
		Expression resultingExpr = new Expression(resultingType, exprName, exprValue);
		Object ob1 = expressionToObject(e1);
		Object ob2 = expressionToObject(e2);
		
		resultingExpr = codeGenerator.generateOpCode(ob1, ob2, resultingExpr, op);

		return resultingExpr;
	}
	
	private Object expressionToObject(Expression e) throws SemanticException {
		Object ob = e;
		if(checkVariableAllScopes(e.getName())) {
			ob = getVariable(e.getName());
		}
		else if(functions.containsKey(e.getName())) {
			ob = functions.get(e.getName());
		}
		
		return ob;
	}

	private String formatExpressionName(Expression e1, Expression e2) {
		String e1Name = e1.getName();
		String e2Name = e2.getName();

		if (e1Name != null && e2Name != null) {
			return "Variavel: " + e1Name + " Variavel: " + e2Name;
		} else if (e1Name != null) {
			return "Variavel: " + e1Name;
		} else if (e2Name != null) {
			return "Variavel: " + e2Name;
		}
		return null;
	}

	private Type validateBinOperation(Expression e1, String op, Expression e2) throws SemanticException {
		Expression e1typed = assignTypeIfNeeded(e1);
		Expression e2typed = assignTypeIfNeeded(e2);

		System.out.println(e1 + " " + op + " " + e2);
		if (e1.getName() != null && e2.getName() != null) {
			if (e1typed.getType() != e2typed.getType()) {
				throwSemanticException("Tipos invalidos " + e1typed.getType().toString() + " e "
						+ e2typed.getType().toString() + " para operacao" + op);
			}

			validateSpecificOp(e1typed.getType(), op);
			return isRelOp(op) ? Type.BOOL : e1typed.getType();
		} else if (e1.getName() != null) {
			validateBinOpTypes(e1typed.getType(), e2typed.getType(), op);
			return binOpTypeCoersion(e1typed.getType(), e2typed.getType(), op);
		} else if (e2.getName() != null) {
			validateBinOpTypes(e2typed.getType(), e1typed.getType(), op);
			return binOpTypeCoersion(e2typed.getType(), e1.getType(), op);
		} else {
			validateBinOpTypes(e1typed.getType(), e2typed.getType(), op);
			return isRelOp(op) ? Type.BOOL : e2typed.getType();
		}
	}

	private boolean isRelOp(String op) {
		return op == "==" || op == "!=" || op == "<" || op == "<=" || op == ">" || op == ">=" || op == "||" || op == "&&";
	}

	private void validateBinOpTypes(Type t1, Type t2, String op) throws SemanticException {
		if ((t1 == Type.STRING && t2 != Type.STRING) || (t1 == Type.BOOL && t2 != Type.BOOL)) {
			throwSemanticException(
					"Invalid types " + t1.toString() + " and " + t2.toString() + " for the " + op + " operation");
		} else if ((t2 == Type.STRING && t1 != Type.STRING) || (t2 == Type.BOOL && t1 != Type.BOOL)) {
			throwSemanticException(
					"Invalid types " + t1.toString() + " and " + t2.toString() + " for the " + op + " operation");
		}
		validateSpecificOp(t1, op);
		validateSpecificOp(t2, op);
	}

	private void validateSpecificOp(Type exprType, String op) throws SemanticException {
		switch (exprType) {
		case BOOL:
			if (op != "&&" && op != "||" && op != "==" && op != "!=") {
				throwSemanticException("Operando invalido " + op + " para tipo " + exprType.toString());
			}
			break;
		case INT:
			if (op == "&&" || op == "||") {
				throwSemanticException("Operando invalido " + op + " para tipo " + exprType.toString());
			}
			break;
		case FLOAT32:
			if (op == "&&" || op == "||" || op == "%" || op == "&" || op == "|" || op == "^" || op == "&^" || op == "<<"
					|| op == ">>") {
				throwSemanticException("Operando invalido " + op + " para tipo " + exprType.toString());
			}
			break;
		case STRING:
			if (op == "&&" || op == "||" || op == "-" || op == "*" || op == "/" || op == "%" || op == "&" || op == "|"
					|| op == "^" || op == "&^" || op == "<<" || op == ">>") {
				throwSemanticException("Operando invalido " + op + " para tipo " + exprType.toString());
			}
			break;
		default:
			throwSemanticException("Operando invalido " + op + " para tipo " + exprType.toString());
		}
	}

	public void createNewFunction(String functionName) throws SemanticException {
		if(variables.containsKey(functionName)) {
			throwSemanticException(functionName + " redeclarada nesse bloco.");
		}
		
		if(functions.containsKey(functionName)) {
			throwSemanticException(functionName + " ja existe.");
		}
		
		Function f = new Function(functionName);
		functions.put(functionName, f);
		
		System.out.println("Criando funcao: " + functionName);
		System.out.println(functions);
		createNewScope(f);
		
		codeGenerator.createFunction(f);
	}
	
	public void FunctionAddReturnType(Type type) {
		Function f = (Function) scopeStack.peek();
		f.setReturnType(type);
	}
	
	public void FunctionAddReturnedExpression(Expression e) throws SemanticException {
		Function f = null;
		for(int i = scopeStack.size()-1; i >= 0; i--) {
			try {
				f = (Function) scopeStack.get(i);
				break;
			} catch(ClassCastException cce) {
			}
		}
		
		if(f == null) {
			throwSemanticException("Palavra chave Return precisa estar dentro da funcao.");
		}
		
		e = assignTypeIfNeeded(e);
		f.setReturnedExpression(e);
		clearBuffers();

		codeGenerator.addReturnCode(e);
		
	}
	
	public void FunctionAddParameter(String varName) throws SemanticException {
		System.out.println("Adicionando novo parametro: " + varName);
		Function f = (Function) scopeStack.peek();
		Variable var = new Variable(Type.UNKNOWN, varName);
		f.addParameter(var);
	}
	
	public void FunctionInitializeParameters(Type type) throws SemanticException {
		System.out.println("Inicializando parametros com tipo: " + type);
		Function f = (Function) scopeStack.peek();
		f.initializeParameters(type, codeGenerator);
	}
	
	public void FunctionCheckParameters(Expression expr) throws SemanticException {
		System.out.println("Chacando parametros:" + expr);
		System.out.println(expBuffer);
		try {
			if(checkVariableAllScopes(expr.getName())) {
				throwSemanticException("nao pode chamar uma nao-funcao " + expr.getName());
			}

			Function fexpr = functions.get(expr.getName());
			List<Variable> parameters = fexpr.getParameters();
			if(parameters.size() != expBuffer.size()) {
				throwSemanticException("Funcao " + fexpr.getName() + " recebe " + parameters.size() + " parametros. " + expBuffer.size() + " parametros recebidos ao inves disso.");
			}
			
			for(int i = 0; i < expBuffer.size(); i++) {
				Expression e = expBuffer.get(i);
				typeCoersion(parameters.get(i).getType(), e);
				codeGenerator.addCodeLoading(parameters.get(i), assignTypeIfNeeded(e)); 
			}
			
		} catch (NullPointerException e) {
			throwSemanticException("Funcao " + expr.getName() + " nao existe.");
		}
		
		functionCallCode(expr.getName()); 
		
		expBuffer.clear();
	}

	private void createNewScope(ScopedEntity scope) {
		scopeStack.push(scope);
	}
	
	public void exitCurrentScope() throws SemanticException {
		ScopedEntity scoped = scopeStack.pop();
		if (scoped instanceof Function) {
			((Function) scoped).validateReturnedType();
			codeGenerator.endFunction();
		}
	}
	
	public void exitCurrentScopeEndIf() {
		scopeStack.pop();
		codeGenerator.endIf();
	}

	public void createIf(Expression e) throws SemanticException {
		e = assignTypeIfNeeded(e);
		createNewScope(new IfElse(e));
	}
	
	public void createElse() {
		createNewScope(new IfElse());
	}
	
	public void initializeVars(Type type, String assigment) throws SemanticException {
		if (assigment.isEmpty()) {
			for (String varName : this.varNamesBuffer) {
				addVariable(new Variable(type, varName));
			}
		} else if (expBuffer.size() != varNamesBuffer.size()) {
			throwSemanticException("contador de atribuicao diferente: " + varNamesBuffer.size() + " != " + expBuffer.size());
		} else {
			for (int i = 0, j = varNamesBuffer.size() - 1; i < varNamesBuffer.size(); i++, j--) {
				Expression exp = this.expBuffer.get(j);
				String varName = this.varNamesBuffer.get(i);
				
				typeCoersion(type, exp);

				addVariable(new Variable(type, varName, exp));
			}
		}

		clearBuffers();
	}
	
	public Variable updateVar(Expression expbefr, Expression exp) throws SemanticException {
		exp = assignTypeIfNeeded(exp);
		Variable var = getVariable(expbefr.getName());
		Type t = typeCoersion(var.getType(), exp);

		var.setType(t);
		var.setValue(exp);

		Object ob = expressionToObject(exp);
		if (ob instanceof Function) {
			var.getValue().setReg("R0");
		}
		
		return var;
	}

	public void updateVars(String assignment) throws SemanticException {
		System.out.println("Atribuicao: " + assignment);
		System.out.println(expBuffer.toString());
		System.out.println(expBufferBeforeAssign.toString());

		if (assignment == "=") {
			if (expBuffer.size() != expBufferBeforeAssign.size()) {
				throwSemanticException(
						"contador de atribuicao diferente: " + expBufferBeforeAssign.size() + " != " + expBuffer.size());
			} else {
				for (int i = 0; i < expBuffer.size(); i++) {
					Expression expbefr = expBufferBeforeAssign.get(i);
					Expression exp = expBuffer.get(i);
					Variable var = updateVar(expbefr, exp);
					
					codeGenerator.variableDeclaration(var);
				}
			}
		} else {
			if (expBuffer.size() != 1 || expBufferBeforeAssign.size() != 1) {
				throwSemanticException(
						"atribuicao " + assignment + " nao permite multiplas variaveis.");
			} else {
				Expression expbefr = expBufferBeforeAssign.get(0);
				Expression exp = expBuffer.get(0);
				
				Expression resultExpr = calculateOpAssign(assignment, expbefr, exp);
				Variable var = updateVar(expbefr, resultExpr);

				codeGenerator.variableDeclaration(var);
			}
		}

		clearBuffers();
	}

	private Expression calculateOpAssign(String assignment, Expression expbefr, Expression exp)
			throws SemanticException {
		Expression resultExpr = new Expression();
		if (assignment == "+=") {
			resultExpr = calculateExpr(expbefr, "+", exp);
		} else if (assignment == "*=") {
			resultExpr = calculateExpr(expbefr, "*", exp);
		} else if (assignment == "-=") {
			resultExpr = calculateExpr(expbefr, "-", exp);
		} else if (assignment == "/=") {
			resultExpr = calculateExpr(expbefr, "/", exp);
		}
		return resultExpr;
	}
	
	public Type checkVariableDeclaration(Type variableType, Type expressionType) throws SemanticException {
		if (expressionType == null) {
			return null;
		} else if (!variableType.equals(expressionType)) {
			throwSemanticException(
					"Tipo de variavel eh " + variableType.name() + " porem tipo da expressao eh " + expressionType.name());
		}
		return variableType;
	}
	

	public Type typeCoersion(Type mainType, Expression e) throws SemanticException {
		e = assignTypeIfNeeded(e);
		
		Type otherType = e.getType();
		
		if (mainType == Type.FLOAT32 && otherType == Type.INT) {
			return Type.FLOAT32;
		} else if (mainType == Type.UNKNOWN) {
			return otherType;
		} else if (mainType != otherType) {
			throwSemanticException("Tipos invalidos " + mainType.toString() + " e " + otherType.toString());
		}
		return otherType;
	}

	public Type binOpTypeCoersion(Type mainType, Type otherType, String op) throws SemanticException {
		if (isRelOp(op)) {
			return Type.BOOL;
		}

		if ((mainType == Type.FLOAT32 || mainType == Type.INT)
			&& (otherType == Type.FLOAT32 || otherType == Type.INT)) {
			return mainType;
		} else if (mainType != otherType) {
			throwSemanticException("Tipos invalidos " + mainType.toString() + " e " + 
		                           otherType.toString() + " para a operacao " + op);
		}
		return mainType;
	}

	private Expression assignTypeIfNeeded(Expression e) throws SemanticException {
		Expression newExpression = new Expression(e.getType(), e.getName(), e.getValue());
		newExpression.setReg(e.getReg());
		
		if(newExpression.getType() == Type.UNKNOWN) {
			try {
				Function f = functions.get(e.getName());
				newExpression.setType(f.getReturnType());
				newExpression.setValue(f.getReturnedExpression().getValue());
				newExpression.setReg(f.getReturnedExpression().getReg());
			} catch(NullPointerException npe) {
				Variable var = getVariable(e.getName());
				newExpression.setType(var.getType());
				newExpression.setValue(var.getValue());
				newExpression.setReg(var.getValue().getReg());
			}
		}
		
		return newExpression;
	}
	
	public void createIfCode() {
		codeGenerator.createIf();
	}
	
	public void createIfElseCode() {
		codeGenerator.createIfElse();
	}
	
	public void createElseCode() {
		codeGenerator.createElse();
	}
	
	public void functionCallCode(String exprName) {
		System.out.println("Call " + exprName);
		if (functions.containsKey(exprName))
			codeGenerator.addFunctionCall(functions.get(exprName));
	}

}
package semanticanalyzer.go;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import semanticanalyzer.go.objects.Type;
import semanticanalyzer.go.exceptions.ExceptionSemanticError;
import semanticanalyzer.go.objects.Expr;
import semanticanalyzer.go.objects.Func;
import semanticanalyzer.go.objects.EntityWithScope;
import semanticanalyzer.go.objects.Variable;

import codegenerator.go.CodeGenerator;

public class Semantic {
	
	/* LEMBRAR DE VERIFICAR TESTES SEMANTICOS ESPECIFICOS DO TODO */
	
	// Buffers
	private List<String> vnBuffer = new ArrayList<>();
	private List<Expr> expBuffer = new ArrayList<>();
	private List<Expr> expBufferBeforeAssign = new ArrayList<>();
	private Stack<EntityWithScope> scopeStack = new Stack<>();
	

	private static Semantic semantic = new Semantic();
	private static CodeGenerator cg = new CodeGenerator();
	private Map<String, Variable> vars = new HashMap<>();
	private Map<String, Func> funcs = new HashMap<>();

	private Semantic() {}

	public static Semantic getInstance() {
		return semantic;
	}
	
	public void addExpression(Expr v) {
		expBuffer.add(v);
	}

	public void transferExpBuffer() {
		expBufferBeforeAssign.clear();
		expBufferBeforeAssign.addAll(expBuffer);
		expBuffer.clear();
	}

	public void clear() {
		vnBuffer.clear();
		
		expBuffer.clear();
		expBufferBeforeAssign.clear();
		
		vars.clear();
		funcs.clear();
		scopeStack.clear();
	}

	private void clearBuffers() {
		vnBuffer.clear();
		expBuffer.clear();
		expBufferBeforeAssign.clear();
	}
	
	private void throwSemanticException(String message) throws ExceptionSemanticError {
		clear();
		throw new ExceptionSemanticError(message);
	}

	public void printVars() {
		for (String varName : this.vars.keySet()) {
			System.out.println("Nome: " + varName + ", Tipo: " + this.vars.get(varName).getType());
		}
	}
	
	public boolean checkVariableAllScopes(String name) {
		Set<String> allVars = new HashSet<String>();
		allVars.addAll(vars.keySet());
		for (int i = 0 ; i < scopeStack.size(); i++) {
			allVars.addAll(scopeStack.get(i).getVariables().keySet());
		}
		return allVars.contains(name);
	}
	
	public void addVarName(String varName) {
		vnBuffer.add(varName);
	}
	
	public void addVariable(Variable var) throws ExceptionSemanticError {
		if (checkVariableInCurrentScope(var.getName()))
			throwSemanticException("Variavel " + var.getName() + " ja foi declarada nesse escopo.");
		
		if (!scopeStack.isEmpty()) {
			System.out.println("Adicionando variavel a escopo especifico: " + var);
			scopeStack.peek().addVariable(var);
		} else {
			if(funcs.containsKey(var.getName())) {
				throwSemanticException(var.getName() + " redeclarando esse bloco.");
			}
			System.out.println("Adicionando variavel ao escopo principal: " + var);
			vars.put(var.getName(), var);
		}

		cg.variableDeclaration(var);
				
		System.out.println(vars);
	}
	
	public boolean checkVariableInCurrentScope(String name) {
		if (scopeStack.isEmpty())
			return vars.containsKey(name);
		else {
			return scopeStack.peek().getVariables().containsKey(name);
		}	
	}
	
	public Variable getVariable(String varName) throws ExceptionSemanticError {
		try {
			if (scopeStack.isEmpty()) {
				return vars.get(varName);
			} else {
				for (int i = scopeStack.size()-1; i >= 0; i--) {
					if(scopeStack.get(i).getVariables().containsKey(varName)) {
						return scopeStack.get(i).getVariables().get(varName);
					}
				}

				if(vars.containsKey(varName)) {
					return vars.get(varName);
				}
				
			}
		} catch(NullPointerException e) {
			throwSemanticException("Variavel " + varName + " nao foi declarada.");
		}
		
		throwSemanticException("Variavel " + varName + " nao foi declarada.");
		return null;
	}

	public Expr calculateUnaryExpr(String op, Expr expr) throws ExceptionSemanticError {
		expr = assignTypeIfNeeded(expr);

		validateUnaryOperation(op, expr.getType());
		String exprVal = op + expr.getValue();
		String exprName = expr.getName() == null ? null : "Var: " + expr.getName();

		Expr resultingExpr = new Expr(expr.getType(), exprName, exprVal);
		resultingExpr.setReg(expr.getReg());

		Object obj = expressionToObject(expr);
		resultingExpr = cg.generateUnaryCode(obj, resultingExpr, op);

		return resultingExpr;
	}

	private void validateUnaryOperation(String op, Type exprType) throws ExceptionSemanticError {
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

	public Expr calculateExpr(Expr e1, String op, Expr e2) throws ExceptionSemanticError {
		Type resultingType = validateBinOperation(e1, op, e2);
		String exprVal = e1.getValue() + op + e2.getValue();
		String exprName = formatExpressionName(e1, e2);
		
		Expr result = new Expr(resultingType, exprName, exprVal);
		Object o1 = expressionToObject(e1);
		Object o2 = expressionToObject(e2);
		
		result = cg.generateOpCode(o1, o2, result, op);

		return result;
	}
	
	private Object expressionToObject(Expr e) throws ExceptionSemanticError {
		Object ob = e;
		if(checkVariableAllScopes(e.getName())) {
			ob = getVariable(e.getName());
		}
		else if(funcs.containsKey(e.getName())) {
			ob = funcs.get(e.getName());
		}
		
		return ob;
	}
	
	// FUNCTIONS
	
	public void FunctionAddParameter(String varName) throws ExceptionSemanticError {
		System.out.println("Adicionando parametro: " + varName);
		Func f = (Func) scopeStack.peek();
		Variable var = new Variable(Type.UNKNOWN, varName);
		f.addParameter(var);
	}
	
	public void FunctionInitializeParameters(Type type) throws ExceptionSemanticError {
		System.out.println("Inicializando parametros com tipo: " + type);
		Func f = (Func) scopeStack.peek();
		f.initializeParameters(type, cg);
	}
	
	// IMPORTANT CHECK!!!!
	private void validateSpecificOp(Type exprType, String op) throws ExceptionSemanticError {
		switch (exprType) {
		case BOOL:
			if (op != "&&" && op != "||" && op != "==" && op != "!=") {
				throwSemanticException("Operando Invalido " + op + " para o tipo " + exprType.toString());
			}
			break;
		case INT:
			if (op == "&&" || op == "||") {
				throwSemanticException("Operando Invalido " + op + " para o tipo " + exprType.toString());
			}
			break;
		case FLOAT32:
			if (op == "&&" || op == "||" || op == "%" || op == "&" || op == "|" || op == "^" || op == "&^" || op == "<<"
					|| op == ">>") {
				throwSemanticException("Operando Invalido " + op + " para o tipo " + exprType.toString());
			}
			break;
		case STRING:
			if (op == "&&" || op == "||" || op == "-" || op == "*" || op == "/" || op == "%" || op == "&" || op == "|"
					|| op == "^" || op == "&^" || op == "<<" || op == ">>") {
				throwSemanticException("Operando Invalido " + op + " para o tipo " + exprType.toString());
			}
			break;
		default:
			throwSemanticException("Operando invalido " + op + " para o tipo " + exprType.toString());
		}
	}

	private String formatExpressionName(Expr e1, Expr e2) {
		String e1N = e1.getName();
		String e2N = e2.getName();

		if (e1N != null && e2N != null) {
			return "Var: " + e1N + " Var: " + e2N;
		} else if (e1N != null) {
			return "Var: " + e1N;
		} else if (e2N != null) {
			return "Var: " + e2N;
		}
		return null;
	}

	private Type validateBinOperation(Expr e1, String op, Expr e2) throws ExceptionSemanticError {
		Expr e1T = assignTypeIfNeeded(e1);
		Expr e2T = assignTypeIfNeeded(e2);

		System.out.println(e1 + " " + op + " " + e2);
		if (e1.getName() != null && e2.getName() != null) {
			if (e1T.getType() != e2T.getType()) {
				throwSemanticException("Tipos invalidos " + e1T.getType().toString() + " e "
						+ e2T.getType().toString() + " para a operacao " + op);
			}

			validateSpecificOp(e1T.getType(), op);
			return isRelandBoolOp(op) ? Type.BOOL : e1T.getType();
		} else if (e1.getName() != null) {
			validateBinOpTypes(e1T.getType(), e2T.getType(), op);
			return binOpTypeCoersion(e1T.getType(), e2T.getType(), op);
		} else if (e2.getName() != null) {
			validateBinOpTypes(e2T.getType(), e1T.getType(), op);
			return binOpTypeCoersion(e2T.getType(), e1.getType(), op);
		} else {
			validateBinOpTypes(e1T.getType(), e2T.getType(), op);
			return isRelandBoolOp(op) ? Type.BOOL : e2T.getType();
		}
	}

	private boolean isRelandBoolOp(String op) {
		return op == "==" || op == "!=" || op == "<" || op == "<=" || op == ">" || op == ">=" || op == "||" || op == "&&";
	}

	private void validateBinOpTypes(Type t1, Type t2, String op) throws ExceptionSemanticError {
		if ((t1 == Type.STRING && t2 != Type.STRING) || (t1 == Type.BOOL && t2 != Type.BOOL)) {
			throwSemanticException(
					"Tipos invalidos " + t1.toString() + " e " + t2.toString() + " para a operacao " + op);
		} else if ((t2 == Type.STRING && t1 != Type.STRING) || (t2 == Type.BOOL && t1 != Type.BOOL)) {
			throwSemanticException(
					"Tipos invalidos " + t1.toString() + " e " + t2.toString() + " para a operacao " + op);
		}
		validateSpecificOp(t1, op);
		validateSpecificOp(t2, op);
	}

	public void createNewFunction(String functionName) throws ExceptionSemanticError {
		if(vars.containsKey(functionName)) {
			throwSemanticException(functionName + " redeclarado nesse bloco.");
		}
		
		if(funcs.containsKey(functionName)) {
			throwSemanticException(functionName + " ja existe.");
		}
		
		Func f = new Func(functionName);
		funcs.put(functionName, f);
		
		System.out.println("Criando funcao: " + functionName);
		System.out.println(funcs);
		createNewScope(f);

		cg.createFunction(f);
	}
	
	public void FunctionAddReturnType(Type type) {
		Func f = (Func) scopeStack.peek();
		f.setReturnType(type);
	}
	
	public void FunctionAddReturnedExpression(Expr e) throws ExceptionSemanticError {
		Func f = null;
		for(int i = scopeStack.size()-1; i >= 0; i--) {
			try {
				f = (Func) scopeStack.get(i);
				break;
			} catch(ClassCastException cce) {
			}
		}
		
		if(f == null) {
			throwSemanticException("Palavra chave Return deve estar contido dentro de uma funcao.");
		}
		
		e = assignTypeIfNeeded(e);
		f.setReturnedExpression(e);
		clearBuffers();
		cg.addReturnCode(e);
		
	}
	
	public void FunctionCheckParameters(Expr expr) throws ExceptionSemanticError {
		System.out.println("Checando parametros:" + expr);
		System.out.println(expBuffer);
		try {
			
			if(checkVariableAllScopes(expr.getName())) {
				throwSemanticException("Nao pode chamar uma nao-funcao " + expr.getName());
			}

			Func fexpr = funcs.get(expr.getName());
			List<Variable> parameters = fexpr.getParameters();
			if(parameters.size() != expBuffer.size()) {
				throwSemanticException("Funcao " + fexpr.getName() + " recebe " + parameters.size() + " parametros. " + expBuffer.size() + " parametros achados ao inves disso.");
			}
			
			for(int i = 0; i < expBuffer.size(); i++) {
				Expr e = expBuffer.get(i);
				typeCoersion(parameters.get(i).getType(), e);
				cg.addCodeLoading(parameters.get(i), assignTypeIfNeeded(e)); 
			}
			
		} catch (NullPointerException e) {
			throwSemanticException("Funcao " + expr.getName() + " nao existe.");
		}
		functionCallCode(expr.getName()); 
		
		expBuffer.clear();
	}
	
	public void initializeVars(Type type, String assigment) throws ExceptionSemanticError {
		if (assigment.isEmpty()) {
			for (String varName : this.vnBuffer) {
				addVariable(new Variable(type, varName));
			}
		} else if (expBuffer.size() != vnBuffer.size()) {
			throwSemanticException("assignment count mismatch: " + vnBuffer.size() + " != " + expBuffer.size());
		} else {
			for (int i = 0, j = vnBuffer.size() - 1; i < vnBuffer.size(); i++, j--) {
				Expr exp = this.expBuffer.get(j);
				String varName = this.vnBuffer.get(i);

				typeCoersion(type, exp);

				addVariable(new Variable(type, varName, exp));
			}
		}

		clearBuffers();
	}
	
	public Variable updateVar(Expr expbefr, Expr exp) throws ExceptionSemanticError {
		exp = assignTypeIfNeeded(exp);
		Variable var = getVariable(expbefr.getName());
		Type t = typeCoersion(var.getType(), exp);

		var.setType(t);
		var.setValue(exp);

		Object ob = expressionToObject(exp);
		if (ob instanceof Func) {
			var.getValue().setReg("R0");
		}
		
		return var;
	}
	
	// SCOPE AUXILIARS
	
	private void createNewScope(EntityWithScope scope) {
		scopeStack.push(scope);
	}
	
	public void exitCurrentScope() throws ExceptionSemanticError {
		EntityWithScope scoped = scopeStack.pop();
		if (scoped instanceof Func) {
			((Func) scoped).validateReturnedType();
			cg.endFunction();
		}
	}

	public void updateVars(String assignment) throws ExceptionSemanticError {
		System.out.println("Atribuicao: " + assignment);
		System.out.println(expBuffer.toString());
		System.out.println(expBufferBeforeAssign.toString());

		if (assignment == "=") {
			if (expBuffer.size() != expBufferBeforeAssign.size()) {
				throwSemanticException(
						"Contador de atribuicao errada: " + expBufferBeforeAssign.size() + " != " + expBuffer.size());
			} else {
				for (int i = 0; i < expBuffer.size(); i++) {
					Expr expbefr = expBufferBeforeAssign.get(i);
					Expr exp = expBuffer.get(i);
					Variable var = updateVar(expbefr, exp);

					cg.variableDeclaration(var);
				}
			}
		} else {
			if (expBuffer.size() != 1 || expBufferBeforeAssign.size() != 1) {
				throwSemanticException(
						"atribuicao " + assignment + " nao permite multiplas variaveis.");
			} else {
				Expr expbefr = expBufferBeforeAssign.get(0);
				Expr exp = expBuffer.get(0);
				
				Expr resultExpr = calculateOpAssign(assignment, expbefr, exp);
				Variable var = updateVar(expbefr, resultExpr);

				cg.variableDeclaration(var);
			}
		}

		clearBuffers();
	}

	private Expr calculateOpAssign(String assignment, Expr expbefr, Expr exp)
			throws ExceptionSemanticError {
		Expr resultExpr = new Expr();
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
	
	public Type checkVariableDeclaration(Type variableType, Type expressionType) throws ExceptionSemanticError {
		if (expressionType == null) {
			return null;
		} else if (!variableType.equals(expressionType)) {
			throwSemanticException(
					"Tipo da variavel eh " + variableType.name() + " porem tipo da expressao eh " + expressionType.name());
		}
		return variableType;
	}

	public Type typeCoersion(Type mainType, Expr e) throws ExceptionSemanticError {
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

	public Type binOpTypeCoersion(Type mainType, Type otherType, String op) throws ExceptionSemanticError {
		if (isRelandBoolOp(op)) {
			return Type.BOOL;
		}

		if ((mainType == Type.FLOAT32 || mainType == Type.INT)
			&& (otherType == Type.FLOAT32 || otherType == Type.INT)) {
			return mainType;
		} else if (mainType != otherType) {
			throwSemanticException("Tipos invalidos " + mainType.toString() + " e " + 
		                           otherType.toString() + " para operacao " + op);
		}
		return mainType;
	}

	private Expr assignTypeIfNeeded(Expr e) throws ExceptionSemanticError {
		Expr newExpression = new Expr(e.getType(), e.getName(), e.getValue());
		newExpression.setReg(e.getReg());
		
		if(newExpression.getType() == Type.UNKNOWN) {
			try {
				Func f = funcs.get(e.getName());
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
	
	public void functionCallCode(String exprName) {
		System.out.println("Chamando " + exprName);
		if (funcs.containsKey(exprName))
			cg.addFunctionCall(funcs.get(exprName));
	}
	
	public CodeGenerator getCodeGenerator() {
		return cg;
	}

	public Map<String, Variable> getVariables() {
		return vars;
	}

}
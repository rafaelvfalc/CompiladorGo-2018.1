package codegenerator.go;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import codegenerator.go.objects.IfElseScope;
import codegenerator.go.objects.OpToAssembly;

import semanticanalyzer.go.Semantic;
import semanticanalyzer.go.exceptions.SemanticException;
import semanticanalyzer.go.objects.Expression;
import semanticanalyzer.go.objects.Function;
import semanticanalyzer.go.objects.Variable;

public class CodeGenerator {

    private Integer lbls;
    private Integer labelsFunction;
    private List<String> codeFunctions;
    private String currFunctionName;
    private boolean inFunctionScope;
    private String assemblyCode;
    private static String R = "R";
    private static int rnumber;
    private Stack<IfElseScope> ifElseStack = new Stack<>();
    private static int ifnumber;

    public CodeGenerator() {
    	init();
    }

    public void init() {
    	lbls = 100;
    	assemblyCode = "100: LD SP, #4000\n";
        
    	rnumber = 0;
    	ifnumber = -1;
        
        labelsFunction = 992;
    	inFunctionScope = false;
    	currFunctionName = "";
    	codeFunctions = new ArrayList<>();
    }
    

    public void generateFinalAssemblyCode(String sourceCode) throws IOException {
    	addFunctionsToCode();
    	
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(sourceCode)));
        writer.write(assemblyCode);
        writer.close();
        init();
    }
    
    public String allocateRegister(){
        rnumber++;
        return R + rnumber;
    }
    
    public String getRegisterFromObject(Object obj) throws SemanticException {
        String reg1;
        System.out.println("get register from: " + obj.toString());
        if (obj instanceof Variable) {
            Variable var = (Variable) obj;
            if(var.getValue().getReg() == null)
            	addCodeLoading(var);
            var = Semantic.getInstance().getVariable(var.getName());
            reg1 = var.getValue().getReg();
        } else if (obj instanceof Function) {
        	reg1 = "R0";
        } else {
            Expression temp = (Expression) obj;
            if (temp.getReg() == null) {
                reg1 = "#" + temp.getValue().toString();
            } else {
                reg1 = temp.getReg();
            }
        }

        return reg1;
    }
    
    public void variableDeclaration(Variable var) throws SemanticException {
        System.out.println("declaring variable: " + var);
        if (var.getValue().getValue() != null) {
            if (var.getValue().getReg() == null) {
            	addCodeLoadingExpression(var.getValue());
            }
            
            String reg = var.getValue().getReg().toString();
            addCode("ST " + var.getName() + ", " + reg);
        }
    }
    
    public void parameterDeclaration(Variable var) throws SemanticException {
        System.out.println("declaring parameter: " + var);
        var.getValue().setReg(allocateRegister());
        String reg = var.getValue().getReg().toString();
        addCode("ST " + var.getName() + ", " + reg);
    }

    public Expression generateOpCode(Object obj1, Object obj2, Expression exp, String op) throws SemanticException {
        if(OpToAssembly.isRelop(op)) {
            exp = generateRelopCode(obj1, obj2, exp, op);
        } else {
        	// Allocate register to resulting expression
        	String reg = allocateRegister();
            exp.setReg(reg);
        	
        	String reg1 = getRegisterFromObject(obj1);
            String reg2 = getRegisterFromObject(obj2);
            addCode(OpToAssembly.mapOp(op) + " " + exp.getReg() + ", " + reg1 + ", " + reg2);
        }
        
        return exp;
    }

    public void generateOpCode(Object obj, Expression exp, String op) throws SemanticException {
        String reg = getRegisterFromObject(obj);
        addCode(OpToAssembly.mapOp(op) + " " + exp.getReg() + ", " + reg);
    }
    
    public Expression generateRelopCode(Object obj1, Object obj2, Expression exp, String op) throws SemanticException {
    	String reg1 = getRegisterFromObject(obj1);
        String reg2 = getRegisterFromObject(obj2);
        
        OpToAssembly operator = OpToAssembly.getOperator(op);
        String subReg = allocateRegister();
        
        if(!ifElseStack.empty() && !ifElseStack.peek().initialized()) {
        	ifElseStack.peek().initialize(getCurrentLabel(), subReg, reg1, reg2, operator);
        	
        	if(inFunctionScope) {
    			labelsFunction += 16;
    		} else {
    			lbls += 16;
    		}
        	
        } else {
	        addCode("SUB " + subReg + ", " + reg1 + ", " + reg2);
	        addCode(operator.getRelOperator() + " " + subReg + ", ", 24);
	        addCode("LD " + subReg + ", #true");
	        addCode("BR ", 16);
	        addCode("LD " + subReg + ", #false");
        }
    	
        exp.setReg(subReg);
        return exp;
    }
    
	public Expression generateUnaryCode(Object obj, Expression exp, String op) throws SemanticException {
		if (op.equals("-")) {
			Expression minusOne = new Expression(exp.getType(), "-1");
			return generateOpCode(minusOne, obj, exp, "*");
		} else if (op.equals("!")) {
			String reg = allocateRegister();
			exp.setReg(reg);

			String objReg = getRegisterFromObject(obj);
			addCode(OpToAssembly.mapOp(op) + " " + exp.getReg() + ", " + objReg);

			return exp;
		}
		return exp;
	}

    public void addCode(String assemblyString) {
    	if (!assemblyString.substring(assemblyString.length() - 1).equals("\n")) {
           assemblyString += "\n";
        }

    	if (!ifElseStack.empty() && ifElseStack.peek().initialized()) {
    		IfElseScope ifElse = ifElseStack.peek();
    		Integer label = updateCurrentLabel(8);
    		String code = ifElse.getCode();
    		code += label + ": " + assemblyString;
    		ifElse.setCode(code);    		
    	} else if (inFunctionScope) {
    		String functionCode = codeFunctions.get(codeFunctions.size()-1);
    		labelsFunction += 8;
    		functionCode += labelsFunction + ": " + assemblyString;
    		codeFunctions.set(codeFunctions.size()-1, functionCode);
    	} else {
    		lbls += 8;
    		assemblyCode += lbls + ": " + assemblyString;
    	}
    }
    
    private void addCode(String assemblyString, int branchToAddLabels) {
    	if (!ifElseStack.empty() && ifElseStack.peek().initialized()) {
    		IfElseScope ifElse = ifElseStack.peek();
    		Integer label = updateCurrentLabel(8);
    		String code = ifElse.getCode();
    		code += label + ": " + assemblyString + "#" + (labelsFunction + branchToAddLabels) + "\n";
    		ifElse.setCode(code);    		
    	} else if (inFunctionScope) {
     		String functionCode = codeFunctions.get(codeFunctions.size()-1);
     		labelsFunction += 8;
     		functionCode += labelsFunction + ": " + assemblyString + "#" + (labelsFunction + branchToAddLabels) + "\n";
     		codeFunctions.set(codeFunctions.size()-1, functionCode);
     	} else {
     		lbls += 8;
     		assemblyCode += lbls + ": " + assemblyString + "#" + (lbls + branchToAddLabels) + "\n";
     	}
	}
    
    public void addCodeLoadingExpression(Expression e) throws SemanticException {
        e.setReg(allocateRegister());
        
        String value = e.getValue();
        if(e.getName() == null) {
        	value = "#" + value;
        }
        
        addCode("LD " + e.getReg() + ", " + value);
    }
    
    public void addCodeLoading(Variable v) throws SemanticException {
    	v.getValue().setReg(allocateRegister());
        addCode("LD " + v.getValue().getReg() + ", " + v.getName());
    }
    
    public void addCodeLoading(Variable v, Expression e) throws SemanticException {
    	String value;
        if(e.getName() == null) {
        	value = "#" + e.getValue();
        } else {
        	value = e.getReg();
        }
    	addCode("LD " + v.getValue().getReg() + ", " + value);
    }
    
    public void createFunction(Function f) {
    	codeFunctions.add("function " + f.getName() + "\n");
    	inFunctionScope = true;
    	currFunctionName = f.getName();
    	f.setLabels(labelsFunction + 8);
    }

    public void addReturnCode(Expression e) {
    	if(e.getReg() != null) {
    		addCode("LD R0, " + e.getReg());
    	}
    	else if (e.getValue() != null) {
    		addCode("LD R0, #" + e.getValue());
    	}
    	addCode("BR *0(SP)");
    }
    
    public void endFunction() {
    	inFunctionScope = false;
    	labelsFunction += 3000;
    }
    
    public void addFunctionCall(Function f) {
    	addCode("ADD SP, SP, #" + currFunctionName + "size");
    	addCode("ST *SP, ", 16);
    	addCode("BR #" + f.getLabels());
    	addCode("SUB SP, SP, #" + currFunctionName + "size");
    }

	private void addFunctionsToCode() {
    	for(String functionCode: codeFunctions) {
    		assemblyCode += "\n";
    		assemblyCode += functionCode;
    	}
    }
	
    public void createIf() {
    	System.out.println("-------------------Create if");
    	ifnumber++;
    	IfElseScope ifelse = new IfElseScope("if", ifnumber);
    	ifElseStack.push(ifelse);
    }
    
    public void createIfElse() {
    	System.out.println("-------------------Create if else");
    	updateLastBranchLabel();
    	IfElseScope ifelse = new IfElseScope("ifelse", ifnumber);
    	ifElseStack.push(ifelse);
    }
    
    public void createElse() {
    	System.out.println("-------------------Create else");
    	updateLastBranchLabel();
    	IfElseScope ifelse = new IfElseScope("else", ifnumber, getCurrentLabel());
    	ifElseStack.push(ifelse);
    }
    
    private void updateLastBranchLabel() {
    	if(inFunctionScope) {
			labelsFunction += 8;
	    	ifElseStack.peek().setLastBranchLabel(labelsFunction);
		} else {
			lbls += 8;
	    	ifElseStack.peek().setLastBranchLabel(lbls);
		}
    }
    
    public void endIf() {
    	unstackIf();
    }
    
    private void unstackIf() {
    	String auxCode = "";
    	for(int i = 0; i < ifElseStack.size(); i++) {
    		if(ifElseStack.get(i).getCodeGenerationLevel() == ifnumber) {
    			IfElseScope currIf = ifElseStack.get(i);
    			
    			boolean lastIfElse = i == (ifElseStack.size()-1);
    			currIf.setEndLine(getCurrentLabel() + 8);
    			
    			if(!lastIfElse) {
    				currIf.setFailLine(ifElseStack.get(i+1).getLabel() + 8);
    			}
    			
    			auxCode += ifElseStack.get(i).generateCode(lastIfElse);
    		}
    	}
    	
    	while(!ifElseStack.empty() && ifElseStack.peek().getCodeGenerationLevel() == ifnumber) {
    		ifElseStack.pop();
    	}
    	
    	ifnumber--;
    	
    	if(!ifElseStack.empty()) {
    		ifElseStack.peek().addToCode(auxCode);
    	} else if(inFunctionScope) {
    		String functionCode = codeFunctions.get(codeFunctions.size()-1);
    		functionCode += auxCode;
    		codeFunctions.set(codeFunctions.size()-1, functionCode);
    	} else {
        	assemblyCode += auxCode;	
    	}
    }
    
    private int getCurrentLabel() {
    	return inFunctionScope ? labelsFunction : lbls;
    }
    
    private int updateCurrentLabel(int upd) {
    	int label;
    	
    	if (inFunctionScope) {
			labelsFunction += upd;
			label = labelsFunction;
		} else {
			lbls += upd;
			label = lbls;
		}
    	
    	return label;
    }
}

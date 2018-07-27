package codegenerator.go;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import codegenerator.go.objects.OperationsAssembly;

import semanticanalyzer.go.Semantic;
import semanticanalyzer.go.exceptions.ExceptionSemanticError;
import semanticanalyzer.go.objects.Expr;
import semanticanalyzer.go.objects.Func;
import semanticanalyzer.go.objects.Variable;

public class CodeGenerator {
	
	/* TODO VERIFICAR CONSISTENCIA NA ESCOLHA DE REGISTRADORES */

	private String assembly;
	private Integer lbls;
    private Integer lblsFunc;
    private boolean inFunctionScope;
    private List<String> codeOfFunctions;
    private String currFunctionName;
    
    
    /* STATIC AUXILIAR VARIABLES */
    private static String R = "R";
    private static int rnumber;

    public CodeGenerator() {
    	init();
    }

    public void init() {
    	rnumber = 0;
        
        lblsFunc = 992;
    	inFunctionScope = false;
    	currFunctionName = "";
    	codeOfFunctions = new ArrayList<>();
    	lbls = 100;
    	assembly = "100: LD SP, #4000\n";
    }
    

    public void generateFinalAssemblyCode(String sourceCode) throws IOException {
    	addFuncCode();
    	
        BufferedWriter w = new BufferedWriter(new FileWriter(new File(sourceCode)));
        w.write(assembly);
        w.close();
        init();
    }
    
    // FUNCTION SECTION
    
    public void createFunction(Func f) {
    	codeOfFunctions.add("function " + f.getName() + "\n");
    	inFunctionScope = true;
    	currFunctionName = f.getName();
    	f.setLabels(lblsFunc + 8);
    }

    public void addReturnCode(Expr e) {
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
    	lblsFunc += 3000;
    }
    
    public void addFunctionCall(Func f) {
    	addCode("ADD SP, SP, #" + currFunctionName + "size");
    	addCode("ST *SP, ", 16);
    	addCode("BR #" + f.getLabels());
    	addCode("SUB SP, SP, #" + currFunctionName + "size");
    }

	private void addFuncCode() {
    	for(String functionCode: codeOfFunctions) {
    		assembly += "\n";
    		assembly += functionCode;
    	}
    }
	
    public void addCode(String assemblyString) {
    	if (!assemblyString.substring(assemblyString.length() - 1).equals("\n")) {
           assemblyString += "\n";
        }

    	if (inFunctionScope) {
    		String funcCode = codeOfFunctions.get(codeOfFunctions.size()-1);
    		lblsFunc += 8;
    		funcCode += lblsFunc + ": " + assemblyString;
    		codeOfFunctions.set(codeOfFunctions.size()-1, funcCode);
    	} else {
    		lbls += 8;
    		assembly += lbls + ": " + assemblyString;
    	}
    }
    
    // ADDITION OF CODE SECTION
    
    private void addCode(String assemblyString, int branchToAddLabels) {
    	if (inFunctionScope) {
     		String funcCode = codeOfFunctions.get(codeOfFunctions.size()-1);
     		lblsFunc += 8;
     		funcCode += lblsFunc + ": " + assemblyString + "#" + (lblsFunc + branchToAddLabels) + "\n";
     		codeOfFunctions.set(codeOfFunctions.size()-1, funcCode);
     	} else {
     		lbls += 8;
     		assembly += lbls + ": " + assemblyString + "#" + (lbls + branchToAddLabels) + "\n";
     	}
	}
    
    public void addCodeLoadingExpression(Expr e) throws ExceptionSemanticError {
        e.setReg(allocateRegister());
        
        String v = e.getValue();
        if(e.getName() == null) {
        	v = "#" + v;
        }
        
        addCode("LD " + e.getReg() + ", " + v);
    }
    
    public void addCodeLoading(Variable v) throws ExceptionSemanticError {
    	v.getValue().setReg(allocateRegister());
        addCode("LD " + v.getValue().getReg() + ", " + v.getName());
    }
    
    public void addCodeLoading(Variable v, Expr e) throws ExceptionSemanticError {
    	String value;
        if(e.getName() == null) {
        	value = "#" + e.getValue();
        } else {
        	value = e.getReg();
        }
    	addCode("LD " + v.getValue().getReg() + ", " + value);
    }
    
    // REGISTRADOR
    
    public String allocateRegister(){
        rnumber++;
        return R + rnumber;
    }
    
    public String getRegisterFromObject(Object obj) throws ExceptionSemanticError {
        String register;
        System.out.println("Pegando registrador de: " + obj.toString());
        if (obj instanceof Variable) {
            Variable v = (Variable) obj;
            if(v.getValue().getReg() == null)
            	addCodeLoading(v);
            v = Semantic.getInstance().getVariable(v.getName());
            register = v.getValue().getReg();
        } else if (obj instanceof Func) {
        	register = "R0";
        } else {
            Expr temp = (Expr) obj;
            if (temp.getReg() == null) {
                register = "#" + temp.getValue().toString();
            } else {
                register = temp.getReg();
            }
        }

        return register;
    }
    
    public void variableDeclaration(Variable v) throws ExceptionSemanticError {
        System.out.println("Declarando variavel: " + v);
        if (v.getValue().getValue() != null) {
            if (v.getValue().getReg() == null) {
            	addCodeLoadingExpression(v.getValue());
            }
            
            String reg = v.getValue().getReg().toString();
            addCode("ST " + v.getName() + ", " + reg);
        }
    }
    
    public void parameterDeclaration(Variable v) throws ExceptionSemanticError {
        System.out.println("Declarando parametro: " + v);
        v.getValue().setReg(allocateRegister());
        String reg = v.getValue().getReg().toString();
        addCode("ST " + v.getName() + ", " + reg);
    }

    public Expr generateOpCode(Object o1, Object o2, Expr exp, String op) throws ExceptionSemanticError {
        if(OperationsAssembly.isRelop(op)) {
            exp = generateRelopCode(o1, o2, exp, op);
        } else {
        	String registrador = allocateRegister();
            exp.setReg(registrador);
        	
        	String registrador1 = getRegisterFromObject(o1);
            String registrador2 = getRegisterFromObject(o2);
            addCode(OperationsAssembly.mapOp(op) + " " + exp.getReg() + ", " + registrador1 + ", " + registrador2);
        }
        
        return exp;
    }

    public void generateOpCode(Object obj, Expr exp, String op) throws ExceptionSemanticError {
        String registrador = getRegisterFromObject(obj);
        addCode(OperationsAssembly.mapOp(op) + " " + exp.getReg() + ", " + registrador);
    }
    
    public Expr generateRelopCode(Object obj1, Object obj2, Expr exp, String op) throws ExceptionSemanticError {
    	String r1 = getRegisterFromObject(obj1);
        String r2 = getRegisterFromObject(obj2);
        
        OperationsAssembly operator = OperationsAssembly.getOperator(op);
        String subtracaoRegistrador = allocateRegister();
        
	    addCode("SUB " + subtracaoRegistrador + ", " + r1 + ", " + r2);
	    addCode(operator.getRelOperator() + " " + subtracaoRegistrador + ", ", 24);
	    addCode("LD " + subtracaoRegistrador + ", #true");
	    addCode("BR ", 16);
	    addCode("LD " + subtracaoRegistrador + ", #false");
    	
        exp.setReg(subtracaoRegistrador);
        return exp;
    }
    
	public Expr generateUnaryCode(Object obj, Expr exp, String op) throws ExceptionSemanticError {
		if (op.equals("-")) {
			Expr mOne = new Expr(exp.getType(), "-1");
			return generateOpCode(mOne, obj, exp, "*");
		} else if (op.equals("!")) {
			String r = allocateRegister();
			exp.setReg(r);

			String objReg = getRegisterFromObject(obj);
			addCode(OperationsAssembly.mapOp(op) + " " + exp.getReg() + ", " + objReg);

			return exp;
		}
		return exp;
	}
}
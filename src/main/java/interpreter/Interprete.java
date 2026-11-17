package interpreter;
import ast.AST.*;
import java.util.HashMap;
import java.util.Map;
public class Interprete {
    private final Map<String, Object> memoria = new HashMap<>();
    public Object ejecutar(ProgramNode programa) {
        memoria.clear();
        for (VarDeclNode decl : programa.declarations) {
            if (decl.type.equals("int")) {
                memoria.put(decl.name, 0);
            } else {
                memoria.put(decl.name, false);
            }
        }


        for (ASTNode sent : programa.statements) {
            if (sent instanceof AssignNode) {
                AssignNode asignacion = (AssignNode) sent;
                Object valor = evaluar(asignacion.expression);
                memoria.put(asignacion.varName, valor);

            } else if (sent instanceof ReturnNode) {
                ReturnNode ret = (ReturnNode) sent;
                if (ret.expression == null) {
                    return null;
                }
                return evaluar(ret.expression);
            }
        }
        return null;
    }

    private Object evaluar(ASTNode expr) {
        if (expr instanceof IntLiteralNode) {
            return ((IntLiteralNode) expr).value;
        }
        if (expr instanceof BoolLiteralNode) {
            return ((BoolLiteralNode) expr).value;
        }
        if (expr instanceof VarUseNode) {
            VarUseNode var = (VarUseNode) expr;
            return memoria.get(var.name);
        }
        if (expr instanceof BinOpNode) {
            BinOpNode bin = (BinOpNode) expr;
            int izq = (Integer) evaluar(bin.left);
            int der = (Integer) evaluar(bin.right);
            if (bin.op.equals("+")) {
                return izq + der;
            } else {
                return izq * der;
            }
        }
        throw new RuntimeException("Error de ejecucion: nodo invalido en el AST.");
    }


    public void mostrarMemoria() {
        System.out.println("Interprete AST");
        for (Map.Entry<String, Object> entrada : memoria.entrySet()) {
            System.out.println(entrada.getKey() + " = " + entrada.getValue());
        }
    }
}
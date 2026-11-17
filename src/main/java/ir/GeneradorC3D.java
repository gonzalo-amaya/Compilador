package ir;
import ast.AST.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class GeneradorC3D {
    public static class Cuadrupla {
        public final String op;
        public final String arg1;
        public final String arg2;
        public final String res;

        public Cuadrupla(String op, String arg1, String arg2, String res) {
            this.op = op;
            this.arg1 = arg1;
            this.arg2 = arg2;
            this.res = res;
        }

        @Override
        public String toString() {
            String a1 = (arg1 != null) ? arg1 : "-";
            String a2 = (arg2 != null) ? arg2 : "-";
            String r  = (res != null)  ? res  : "-";
            return "(" + op + ", " + a1 + ", " + a2 + ", " + r + ")";
        }
    }

    private final List<Cuadrupla> listaCodigo = new ArrayList<>();
    private int contadorTemp = 0;
    private String nuevoTemporal() {
        contadorTemp++;
        return "T" + contadorTemp;
    }
    public List<Cuadrupla> generar(ProgramNode programa) {
        listaCodigo.clear();
        contadorTemp = 0;
        for (ASTNode sent : programa.statements) {
            generarSentencia(sent);
        }
        return listaCodigo;
    }

    private void generarSentencia(ASTNode sent) {
        if (sent instanceof AssignNode) {
            AssignNode asignacion = (AssignNode) sent;
            String lugarExpr = generarExpresion(asignacion.expression);
            listaCodigo.add(new Cuadrupla("=", lugarExpr, null, asignacion.varName));

        } else if (sent instanceof ReturnNode) {
            ReturnNode ret = (ReturnNode) sent;
            if (ret.expression == null) {
                listaCodigo.add(new Cuadrupla("RET_VOID", null, null, null));
            } else {
                String lugarExpr = generarExpresion(ret.expression);
                listaCodigo.add(new Cuadrupla("RET", lugarExpr, null, null));
            }
        }
    }


    private String generarExpresion(ASTNode expr) {
        if (expr instanceof IntLiteralNode) {
            return String.valueOf(((IntLiteralNode) expr).value);
        }
        if (expr instanceof BoolLiteralNode) {
            boolean val = ((BoolLiteralNode) expr).value;
            return val ? "1" : "0";
        }
        if (expr instanceof VarUseNode) {
            return ((VarUseNode) expr).name;
        }
        if (expr instanceof BinOpNode) {
            BinOpNode bin = (BinOpNode) expr;
            String izq = generarExpresion(bin.left);
            String der = generarExpresion(bin.right);
            String temp = nuevoTemporal();
            listaCodigo.add(new Cuadrupla(bin.op, izq, der, temp));
            return temp;
        }
        throw new RuntimeException("Error al generar C3D.");
    }

    public int cantidadTemporales() {
        return contadorTemp;
    }

    public void mostrarCodigo() {
        System.out.println("Codigo de Tres Direcciones");
        for (int i = 0; i < listaCodigo.size(); i++) {
            System.out.println(i + ": " + listaCodigo.get(i));
        }
    }

    public static class InterpreteC3D {
        private final Map<String, Integer> mem = new HashMap<>();
        public Integer ejecutar(List<Cuadrupla> cuadruplas, ProgramNode programa) {
            mem.clear();
            for (VarDeclNode decl : programa.declarations) {
                mem.put(decl.name, 0);
            }

            for (Cuadrupla c : cuadruplas) {
                if (c.op.equals("+")) {
                    mem.put(c.res, obtenerValor(c.arg1) + obtenerValor(c.arg2));
                } else if (c.op.equals("*")) {
                    mem.put(c.res, obtenerValor(c.arg1) * obtenerValor(c.arg2));
                } else if (c.op.equals("=")) {
                    mem.put(c.res, obtenerValor(c.arg1));
                } else if (c.op.equals("RET")) {
                    return obtenerValor(c.arg1);
                } else if (c.op.equals("RET_VOID")) {
                    return null;
                }
            }
            return null;
        }

        private int obtenerValor(String operando) {
            if (mem.containsKey(operando)) {
                return mem.get(operando);
            }
            return Integer.parseInt(operando);
        }

        public void mostrarMemoria() {
            System.out.println("Estado final de Interprete C3D");
            for (Map.Entry<String, Integer> entrada : mem.entrySet()) {
                System.out.println(entrada.getKey() + " = " + entrada.getValue());
            }
        }
    }
}
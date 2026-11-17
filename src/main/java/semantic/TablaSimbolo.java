package semantic;
import ast.AST.*;
import java.util.LinkedHashMap;
import java.util.Map;
public class TablaSimbolo {

    public static class InfoSimbolo {
        public final String nombre;
        public final String tipo;
        public final int offset;

        public InfoSimbolo(String nombre, String tipo, int offset) {
            this.nombre = nombre;
            this.tipo = tipo;
            this.offset = offset;
        }
    }

    private final Map<String, InfoSimbolo> tabla = new LinkedHashMap<>();
    private int offsetActual = 0;

    public void analizar(ProgramNode programa) {
        for (VarDeclNode decl : programa.declarations) {
            if (tabla.containsKey(decl.name)) {
                throw new RuntimeException("Error semantico en linea " + decl.line + ": la variable '" + decl.name + "' ya fue declarada.");
            }
            offsetActual = offsetActual - 8;
            tabla.put(decl.name, new InfoSimbolo(decl.name, decl.type, offsetActual));
        }

        for (ASTNode sent : programa.statements) {
            verificarSentencia(sent, programa.returnType);}
    }

    private void verificarSentencia(ASTNode sent, String tipoRetornoEsperado) {
        if (sent instanceof AssignNode) {
            AssignNode asignacion = (AssignNode) sent;
            InfoSimbolo info = buscar(asignacion.varName, asignacion.line);
            String tipoExpr = verificarExpresion(asignacion.expression);

            if (!info.tipo.equals(tipoExpr)) {
                throw new RuntimeException("Error semantico en linea " + asignacion.line + ": no se puede asignar '" + tipoExpr + "' a '" + asignacion.varName + "' que es '" + info.tipo );
            }

        } else if (sent instanceof ReturnNode) {
            ReturnNode ret = (ReturnNode) sent;
            if (ret.expression == null) {
                if (!tipoRetornoEsperado.equals("void")) {
                    throw new RuntimeException("Error semantico en linea " + ret.line + ": falta el valor de retorno para '" + tipoRetornoEsperado + "'.");
                }
            } else {
                if (tipoRetornoEsperado.equals("void")) {
                    throw new RuntimeException("Error semantico en linea " + ret.line + ": una funcion void no puede devolver un valor.");
                }
                String tipoExpr = verificarExpresion(ret.expression);
                if (!tipoRetornoEsperado.equals(tipoExpr)) {
                    throw new RuntimeException("Error semantico en linea " + ret.line + ": tipo de retorno incorrecto (se esperaba '" + tipoRetornoEsperado + "' y se devolvio '" + tipoExpr + "').");
                }
            }
        }
    }

    private String verificarExpresion(ASTNode expr) {
        if (expr instanceof IntLiteralNode) {
            return "int";
        }
        if (expr instanceof BoolLiteralNode) {
            return "bool";
        }
        if (expr instanceof VarUseNode) {
            VarUseNode var = (VarUseNode) expr;
            return buscar(var.name, var.line).tipo;
        }
        if (expr instanceof BinOpNode) {
            BinOpNode bin = (BinOpNode) expr;
            String tipoIzq = verificarExpresion(bin.left);
            String tipoDer = verificarExpresion(bin.right);

            if (!tipoIzq.equals("int") || !tipoDer.equals("int")) {
                throw new RuntimeException("Error semantico en linea " + bin.line + ": el operador '" + bin.op + "' solo acepta enteros.");
            }
            return "int";
        }
        throw new RuntimeException("Error semantico: expresion no reconocida.");
    }

    public InfoSimbolo buscar(String nombre, int linea) {
        InfoSimbolo info = tabla.get(nombre);
        if (info == null) {
            throw new RuntimeException("Error semantico en linea " + linea + ": la variable '" + nombre + "' no fue declarada.");}
        return info;}

    public InfoSimbolo obtener(String nombre) {
        return tabla.get(nombre);}

    public int cantidadVariables() {
        return tabla.size();}

    public void mostrarTabla() {
        System.out.println("Tabla de Simbolos");
        for (InfoSimbolo info : tabla.values()) {
            System.out.println("Variable: " + info.nombre + " | Tipo: " + info.tipo + " | Offset: " + info.offset + "(%rbp)");
        }
    }
}
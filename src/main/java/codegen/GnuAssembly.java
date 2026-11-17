package codegen;
import ir.GeneradorC3D.Cuadrupla;
import semantic.TablaSimbolo;
import semantic.TablaSimbolo.InfoSimbolo;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GnuAssembly {
    private final Map<String, Integer> offsetsTemporales = new HashMap<>();

    public String generar(List<Cuadrupla> cuadruplas, TablaSimbolo tablaSimbolos, int cantTemporales) {
        offsetsTemporales.clear();
        int cantVars = tablaSimbolos.cantidadVariables();
        for (int i = 1; i <= cantTemporales; i++) {
            int offset = -8 * (cantVars + i);
            offsetsTemporales.put("T" + i, offset);
        }
        int totalSlots = cantVars + cantTemporales;
        int slotsAlineados = (totalSlots % 2 == 0) ? totalSlots : totalSlots + 1;

        StringBuilder asm = new StringBuilder();
        asm.append("    .text\n");
        asm.append("    .globl main\n");
        asm.append("    .type main, @function\n");
        asm.append("main:\n");
        asm.append("    enter $(8 * ").append(slotsAlineados).append("), $0\n\n");

        for (int i = 1; i <= cantVars; i++) {
            asm.append("    movq $0, ").append(-8 * i).append("(%rbp)\n");
        }
        if (cantVars > 0) {
            asm.append("\n");
        }

        for (Cuadrupla c : cuadruplas) {
            asm.append("    # ").append(c.toString()).append("\n");
            if (c.op.equals("+")) {
                String op1 = formatearOperando(c.arg1, tablaSimbolos);
                String op2 = formatearOperando(c.arg2, tablaSimbolos);
                String dst = formatearOperando(c.res, tablaSimbolos);
                asm.append("    movq ").append(op1).append(", %r10\n");
                asm.append("    movq ").append(op2).append(", %r11\n");
                asm.append("    addq %r11, %r10\n");
                asm.append("    movq %r10, ").append(dst).append("\n\n");

            } else if (c.op.equals("*")) {
                String op1 = formatearOperando(c.arg1, tablaSimbolos);
                String op2 = formatearOperando(c.arg2, tablaSimbolos);
                String dst = formatearOperando(c.res, tablaSimbolos);

                asm.append("    movq ").append(op1).append(", %r10\n");
                asm.append("    movq ").append(op2).append(", %r11\n");
                asm.append("    imulq %r11, %r10\n");
                asm.append("    movq %r10, ").append(dst).append("\n\n");

            } else if (c.op.equals("=")) {
                String origen = formatearOperando(c.arg1, tablaSimbolos);
                String destino = formatearOperando(c.res, tablaSimbolos);
                asm.append("    movq ").append(origen).append(", %r10\n");
                asm.append("    movq %r10, ").append(destino).append("\n\n");
            } else if (c.op.equals("RET")) {
                String origen = formatearOperando(c.arg1, tablaSimbolos);
                asm.append("    movq ").append(origen).append(", %rax\n");
                asm.append("    leave\n");
                asm.append("    ret\n\n");
            } else if (c.op.equals("RET_VOID")) {
                asm.append("    movq $0, %rax\n");
                asm.append("    leave\n");
                asm.append("    ret\n\n");
            }
        }

        asm.append("    movq $0, %rax\n");
        asm.append("    leave\n");
        asm.append("    ret\n");
        return asm.toString();
    }

    private String formatearOperando(String operando, TablaSimbolo tablaSimbolos) {
        InfoSimbolo info = tablaSimbolos.obtener(operando);
        if (info != null) {
            return info.offset + "(%rbp)";
        }
        if (offsetsTemporales.containsKey(operando)) {
            return offsetsTemporales.get(operando) + "(%rbp)";
        }
        return "$" + operando;
    }

    public void guardarArchivo(String codigoAsm, String rutaArchivo) throws IOException {
        try (FileWriter writer = new FileWriter(rutaArchivo)) {
            writer.write(codigoAsm);
        }
    }
}
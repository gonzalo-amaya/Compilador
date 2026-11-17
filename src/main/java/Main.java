import ast.AST.ProgramNode;
import codegen.GnuAssembly;
import interpreter.Interprete;
import ir.GeneradorC3D;
import ir.GeneradorC3D.Cuadrupla;
import ir.GeneradorC3D.InterpreteC3D;
import java_cup.runtime.Symbol;
import lexer.Lexer;
import parser.Parser;
import semantic.TablaSimbolo;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String rutaEntrada = (args.length > 0) ? args[0] : "programa.txt";

        try (Reader lector = new FileReader(rutaEntrada)) {
            System.out.println("Compilando archivo: " + rutaEntrada + "\n");

            //Analisis lexico y sintactico
            Lexer lexer = new Lexer(lector);
            Parser parser = new Parser(lexer);
            Symbol resultadoParse = parser.parse();
            ProgramNode ast = (ProgramNode) resultadoParse.value;
            System.out.println("AST construido correctamente.\n");

            // Analisis semantico
            TablaSimbolo tabla = new TablaSimbolo();
            tabla.analizar(ast);
            tabla.mostrarTabla();
            System.out.println();

            // interprete del AST
            Interprete interprete = new Interprete();
            Object resAst = interprete.ejecutar(ast);
            interprete.mostrarMemoria();
            System.out.println("Retorno del programa (AST): " + resAst + "\n");

            //Codigo de Tres Direcciones (C3D)
            GeneradorC3D genC3D = new GeneradorC3D();
            List<Cuadrupla> cuadruplas = genC3D.generar(ast);
            genC3D.mostrarCodigo();
            System.out.println();

            InterpreteC3D interpreteC3D = new InterpreteC3D();
            Integer resC3D = interpreteC3D.ejecutar(cuadruplas, ast);
            interpreteC3D.mostrarMemoria();
            System.out.println("Retorno del programa (C3D): " + resC3D + "\n");

            //codigo ensamblador
            GnuAssembly generadorAsm = new GnuAssembly();
            String codigoAsm = generadorAsm.generar(cuadruplas, tabla, genC3D.cantidadTemporales());
            generadorAsm.guardarArchivo(codigoAsm, "salida.s");

            System.out.println("Codigo Assembly generado");
            System.out.println(codigoAsm);
            System.out.println("Archivo salida.s guardado correctamente.");
        } catch (Exception e) {
            System.err.println("Error durante la compilacion: " + e.getMessage());
        }
    }
}
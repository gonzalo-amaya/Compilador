package ast;
import java.util.List;
public class AST {

    public interface ASTNode {}

    public static class ProgramNode implements ASTNode {
        public final String returnType;
        public final List<VarDeclNode> declarations;
        public final List<ASTNode> statements;

        public ProgramNode(String returnType, List<VarDeclNode> declarations, List<ASTNode> statements) {
            this.returnType = returnType;
            this.declarations = declarations;
            this.statements = statements;
        }
    }

    public static class VarDeclNode implements ASTNode {
        public final String type;
        public final String name;
        public final int line;

        public VarDeclNode(String type, String name, int line) {
            this.type = type;
            this.name = name;
            this.line = line;
        }
    }

    public static class VarUseNode implements ASTNode {
        public final String name;
        public final int line;

        public VarUseNode(String name, int line) {
            this.name = name;
            this.line = line;
        }
    }

    public static class IntLiteralNode implements ASTNode {
        public final int value;
        public IntLiteralNode(int value) { this.value = value; }
    }

    public static class BoolLiteralNode implements ASTNode {
        public final boolean value;
        public BoolLiteralNode(boolean value) { this.value = value; }
    }

    public static class BinOpNode implements ASTNode {
        public final String op;
        public final ASTNode left, right;
        public final int line;

        public BinOpNode(String op, ASTNode left, ASTNode right, int line) {
            this.op = op;
            this.left = left;
            this.right = right;
            this.line = line;
        }
    }

    public static class AssignNode implements ASTNode {
        public final String varName;
        public final ASTNode expression;
        public final int line;

        public AssignNode(String varName, ASTNode expression, int line) {
            this.varName = varName;
            this.expression = expression;
            this.line = line;
        }
    }

    public static class ReturnNode implements ASTNode {
        public final ASTNode expression;
        public final int line;

        public ReturnNode(ASTNode expression, int line) {
            this.expression = expression;
            this.line = line;
        }
    }
}
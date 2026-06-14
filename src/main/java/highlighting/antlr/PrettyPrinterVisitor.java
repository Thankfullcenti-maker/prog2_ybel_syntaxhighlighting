package highlighting.antlr;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

public final class PrettyPrinterVisitor extends MiniJavaBaseVisitor<Void> {

    private final StringBuilder out = new StringBuilder();
    private final int indentWidth;
    private int currentIndent = 0;
    private boolean atLineStart = true;
    private Token lastToken = null;

    public PrettyPrinterVisitor(int indentWidth) {
        this.indentWidth = Math.max(0, indentWidth);
    }

    public String result() {
        return out.toString();
    }

    // ----------------------------------------------------
    // Structural methods
    // ----------------------------------------------------

    @Override
    public Void visitCompilationUnit(MiniJavaParser.CompilationUnitContext ctx) {
        if (ctx.children == null) return null;

        for (var child : ctx.children) {
            if (child.getPayload() instanceof Token && ((Token) child.getPayload()).getType() == Token.EOF) {
                continue;
            }
            visit(child);
            // Strukturierte Leerzeilen nach Top-Level Elementen erzeugen
            nl();
        }
        return null;
    }

    @Override
    public Void visitClassBody(MiniJavaParser.ClassBodyContext ctx) {
        write("{");
        nl();
        currentIndent++;

        if (ctx.children != null) {
            for (var child : ctx.children) {
                String text = child.getText();
                if (text.equals("{") || text.equals("}")) {
                    continue;
                }
                // Leere Deklarationen (einzelne Semikolons) überspringen
                if (text.equals(";")) {
                    continue;
                }
                visit(child);
                nl();
            }
        }

        currentIndent--;
        write("}");
        return null;
    }

    @Override
    public Void visitBlock(MiniJavaParser.BlockContext ctx) {
        write("{");
        nl();
        currentIndent++;

        if (ctx.children != null) {
            for (var child : ctx.children) {
                String text = child.getText();
                if (text.equals("{") || text.equals("}")) {
                    continue;
                }
                if (text.equals(";")) {
                    continue;
                }
                visit(child);
            }
        }

        currentIndent--;
        write("}");
        nl();
        return null;
    }

    @Override
    public Void visitStatement(MiniJavaParser.StatementContext ctx) {
        // Falls das Statement ein geklammerter Block ist, übernimmt visitBlock die Einrückung
        if (ctx.block() != null) {
            visit(ctx.block());
        } else if (ctx.IF() != null) {
            // Strukturierte Standard-Formatierung für Verzweigungen
            write("if ");
            visit(ctx.expression());
            nl();
            currentIndent++;
            visit(ctx.statement(0));
            currentIndent--;

            if (ctx.ELSE() != null) {
                write("else");
                nl();
                currentIndent++;
                visit(ctx.statement(1));
                currentIndent--;
            }
        } else if (ctx.WHILE() != null) {
            write("while ");
            visit(ctx.expression());
            nl();
            currentIndent++;
            visit(ctx.statement(0));
            currentIndent--;
        } else {
            // Lineare Statements (Zuweisungen, Returns, Methodenaufrufe)
            visitChildren(ctx);
            nl();
        }
        return null;
    }

    // ---------------- helper methods ----------------

    private void indent() {
        if (atLineStart) {
            out.append(" ".repeat(Math.max(0, indentWidth * currentIndent)));
            atLineStart = false;
        }
    }

    private void write(String s) {
        if (s == null || s.isEmpty()) return;
        indent();
        out.append(s);
    }

    private void nl() {
        if (out.length() > 0 && out.charAt(out.length() - 1) == '\n' && atLineStart) {
            return;
        }
        out.append('\n');
        atLineStart = true;
        lastToken = null;
    }

    private void writeln(String s) {
        write(s);
        nl();
    }

    // --------------- token output + basic spacing ---------------

    @Override
    public Void visitTerminal(TerminalNode node) {
        Token t = node.getSymbol();
        String text = t.getText();

        if (lastToken != null) {
            int prevType = lastToken.getType();
            int curType = t.getType();

            if (needsSpaceBetween(prevType, curType)) write(" ");
        }

        write(text);
        lastToken = t;
        return null;
    }

    private boolean needsSpaceBetween(int prevType, int curType) {
        return isWordLike(prevType) && isWordLike(curType);
    }

    private boolean isWordLike(int type) {
        return type == MiniJavaLexer.IDENTIFIER
            || type == MiniJavaLexer.STRING_LITERAL
            || type == MiniJavaLexer.CHAR_LITERAL
            || type == MiniJavaLexer.NULL
            || type == MiniJavaLexer.PACKAGE
            || type == MiniJavaLexer.IMPORT
            || type == MiniJavaLexer.CLASS
            || type == MiniJavaLexer.PUBLIC
            || type == MiniJavaLexer.PRIVATE
            || type == MiniJavaLexer.FINAL
            || type == MiniJavaLexer.RETURN
            || type == MiniJavaLexer.NEW
            || type == MiniJavaLexer.IF
            || type == MiniJavaLexer.ELSE
            || type == MiniJavaLexer.WHILE
            || type == MiniJavaLexer.EXTENDS
            || type == MiniJavaLexer.IMPLEMENTS;
    }
}

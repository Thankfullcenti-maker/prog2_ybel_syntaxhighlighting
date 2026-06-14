package highlighting.antlr;

import java.util.Scanner;

import org.antlr.v4.runtime.*;

public class DemoRunner {

    public static void main(String[] args) {
        // Ein unformatiertes Stück MiniJava-Code zum Testen
        String sampleCode = "package demo; import java.util.*; "
            + "@Deprecated class MyClass { "
            + "private int x = 10; "
            + "public void test() { "
            + "if (x > 5) { x = 0; } else { while (x < 5) { x = x + 1; } } "
            + "} }";

        // 1. Abfrage der Einrückstufe
        Scanner scanner = new Scanner(System.in);
        System.out.print("Anzahl der Leerzeichen pro Einrückstufe (z.B. 2, 4, 8): ");
        int indentWidth = scanner.nextInt();

        // 2. ANTLR Parse-Tree erzeugen
        MiniJavaLexer lexer = new MiniJavaLexer(CharStreams.fromString(sampleCode));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MiniJavaParser parser = new MiniJavaParser(tokens);
        MiniJavaParser.CompilationUnitContext tree = parser.compilationUnit();

        // 3. Visitor anwenden
        PrettyPrinterVisitor printer = new PrettyPrinterVisitor(indentWidth);
        printer.visit(tree);

        // 4. Ausgabe auf der Konsole
        System.out.println("\n=== Formatiertes MiniJava-Programm ===");
        System.out.println(printer.result());
    }
}

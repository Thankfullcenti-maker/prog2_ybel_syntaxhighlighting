package highlighting.antlr;

import highlighting.core.HighlightRegion;
import highlighting.core.SyntaxHighlighter;
import highlighting.presets.MiniJavaColours; // Import aus dem presets Package

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.*;

public class AntlrTokenCollector extends SyntaxHighlighter {

    @Override
    public List<HighlightRegion> collectMatches(String text) {
        List<HighlightRegion> regions = new ArrayList<>();

        // 1. Lexer mit dem Eingabetext initialisieren
        MiniJavaLexer lexer = new MiniJavaLexer(CharStreams.fromString(text));

        // 2. Token-Stream erzeugen und befüllen
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        tokenStream.fill();
        List<Token> tokens = tokenStream.getTokens();

        // 3. Tokens sequentiell ablaufen
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);

            // EOF-Token ignorieren
            if (token.getType() == Token.EOF) {
                continue;
            }

            int start = token.getStartIndex();
            int end = token.getStopIndex() + 1; // Konvertierung in exklusiven End-Index [start, end)

            // Spezialfall: Annotationen laut Aufgabenstellung (@ und folgender IDENTIFIER)
            if (token.getType() == MiniJavaLexer.AT) {
                Color annotationColor = MiniJavaColours.ANNOTATION_COLOUR;
                regions.add(new HighlightRegion(start, end, annotationColor));

                // Prüfen, ob das nächste Token ein Bezeichner ist
                if (i + 1 < tokens.size()) {
                    Token nextToken = tokens.get(i + 1);
                    if (nextToken.getType() == MiniJavaLexer.IDENTIFIER) {
                        int nextStart = nextToken.getStartIndex();
                        int nextEnd = nextToken.getStopIndex() + 1;
                        regions.add(new HighlightRegion(nextStart, nextEnd, annotationColor));
                        i++; // Das Identifier-Token überspringen, da es mitgefärbt wurde
                    }
                }
                continue;
            }

            // Standard-Mapping für Token-Typen auf Farb-Objekt
            Color color = mapTokenTypeToColor(token.getType());
            if (color != null) {
                regions.add(new HighlightRegion(start, end, color));
            }
        }

        return regions;
    }

    private Color mapTokenTypeToColor(int tokenType) {
        switch (tokenType) {
            case MiniJavaLexer.PACKAGE:
            case MiniJavaLexer.IMPORT:
            case MiniJavaLexer.CLASS:
            case MiniJavaLexer.PUBLIC:
            case MiniJavaLexer.PRIVATE:
            case MiniJavaLexer.FINAL:
            case MiniJavaLexer.RETURN:
            case MiniJavaLexer.NEW:
            case MiniJavaLexer.IF:
            case MiniJavaLexer.ELSE:
            case MiniJavaLexer.WHILE:
            case MiniJavaLexer.EXTENDS:
            case MiniJavaLexer.IMPLEMENTS:
                return MiniJavaColours.KEYWORD_COLOUR;

            case MiniJavaLexer.STRING_LITERAL:
            case MiniJavaLexer.NULL: // null wird hier dem String-Stil zugeordnet
                return MiniJavaColours.STRING_LITERAL_COLOUR;

            case MiniJavaLexer.CHAR_LITERAL:
                return MiniJavaColours.CHAR_LITERAL_COLOUR;

            case MiniJavaLexer.LINE_COMMENT:
                return MiniJavaColours.LINE_COMMENT_COLOUR;

            case MiniJavaLexer.BLOCK_COMMENT:
                return MiniJavaColours.BLOCK_COMMENT_COLOUR;

            case MiniJavaLexer.JAVADOC_COMMENT:
                return MiniJavaColours.JAVADOC_COMMENT_COLOUR;

            default:
                return null; // Keine Einfärbung für reguläre Trennzeichen oder Standard-Identifier
        }
    }
}

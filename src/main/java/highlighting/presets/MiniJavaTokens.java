package highlighting.presets;

import highlighting.regex.Token;
import java.util.List;
import java.util.regex.Pattern;

public final class MiniJavaTokens {

  // TODO (Phase I+II: RegexHighlighter/ScanningHighlighter)
  // TODO: Define the MiniJava tokens used by the highlighters. Each token is a mapping from a
  // regular expression to a colour (and, if applicable, a specific matching group). The order of
  // tokens in this list determines their relative priority during highlighting. One example token
  // definition is provided below; define the remaining tokens in an analogous way.

  // Basic token set for MiniJava. Extend this list with further tokens as needed (e.g. identifiers,
  // numeric literals, operators, brackets, whitespace), following the same pattern. Each token is
  // defined by a regular expression and a colour. Optionally, a specific capturing group within the
  // pattern can be selected as the "highlighted" region.
  public static List<Token> defaultTokens() {
    return List.of(
        // 1. Kommentare (Spezifischste zuerst: Javadoc -> Block -> Einzeilig)
        // (?s) sorgt dafür, dass der Punkt (.) auch Zeilenumbrüche matcht
        Token.of(Pattern.compile("/\\*\\*(?s:.*?)\\*/"), MiniJavaColours.JAVADOC_COMMENT_COLOUR),
        Token.of(Pattern.compile("/\\*(?s:.*?)\\*/"), MiniJavaColours.BLOCK_COMMENT_COLOUR),
        Token.of(Pattern.compile("//.*"), MiniJavaColours.LINE_COMMENT_COLOUR),

        // 2. Literale (Strings und Characters)
        Token.of(Pattern.compile("\"([^\"\\\\]|\\\\.)*\""), MiniJavaColours.STRING_LITERAL_COLOUR),
        Token.of(Pattern.compile("'[^']'"), MiniJavaColours.CHAR_LITERAL_COLOUR),

        // 3. Annotationen (Beginnen mit @, gefolgt von Buchstaben oder Bindestrich)
        Token.of(Pattern.compile("@[a-zA-Z-]+"), MiniJavaColours.ANNOTATION_COLOUR),

        // 4. Keywords (Eingeschlossen in Wortgrenzen \\b, damit sie exakte Treffer sind)
        Token.of(
            Pattern.compile("\\b(package|import|class|public|private|final|return|null|new)\\b"),
            MiniJavaColours.KEYWORD_COLOUR)

        // Hinweis: Falls in MiniJavaColours die genauen Farb-Konstanten (wie CHAR_LITERAL_COLOUR)
        // leicht anders heißen, passe den Namen nach dem Punkt einfach kurz an deine Vorgabe an.
        );
  }
}

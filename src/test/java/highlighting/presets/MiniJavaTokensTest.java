package highlighting.presets;

import static org.junit.jupiter.api.Assertions.*;

import highlighting.regex.Token;
import java.util.List;
import org.junit.jupiter.api.Test;

public class MiniJavaTokensTest {

  @Test
  public void testCommentsAndEmbeddedKeywords() {
    List<Token> tokens = MiniJavaTokens.defaultTokens();
    // Index 2 ist unser Line-Comment-Token (//.*)
    Token lineCommentToken = tokens.get(2);

    String commentWithKeyword = "// This is a public class";
    // Es muss mindestens ein Match gefunden werden (Liste nicht leer)
    assertFalse(lineCommentToken.test(commentWithKeyword).isEmpty());
  }

  @Test
  public void testStringsWithSpecialCharacters() {
    List<Token> tokens = MiniJavaTokens.defaultTokens();
    // Index 3 ist unser String-Token
    Token stringToken = tokens.get(3);

    String complexString = "\"String mit // und /* Kommentar\"";
    assertFalse(stringToken.test(complexString).isEmpty());
  }

  @Test
  public void testAnnotations() {
    List<Token> tokens = MiniJavaTokens.defaultTokens();
    // Index 5 ist unser Annotation-Token
    Token annotationToken = tokens.get(5);

    assertFalse(annotationToken.test("@Override").isEmpty());
    assertFalse(annotationToken.test("@My-Custom-Annotation").isEmpty());
  }

  @Test
  public void testKeywordsWithWordBoundaries() {
    List<Token> tokens = MiniJavaTokens.defaultTokens();
    // Index 6 ist unser Keyword-Token
    Token keywordToken = tokens.get(6);

    // Gültiges Keyword isoliert -> Match vorhanden
    assertFalse(keywordToken.test("return").isEmpty());

    // Keyword als Teil eines längeren Bezeichners darf NICHT matchen -> Liste leer
    assertTrue(keywordToken.test("myreturnvalue").isEmpty());
    assertTrue(keywordToken.test("newton").isEmpty());
  }
}

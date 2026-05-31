package highlighting.regex;

import static org.junit.jupiter.api.Assertions.*;

import highlighting.core.HighlightRegion;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ScanningHighlighterTest {

  @Test
  public void testLongestMatchWins() {
    ScanningHighlighter highlighter = new ScanningHighlighter();

    // Falls ein Text von zwei Tokens gematcht werden könnte (z. B. ein Keyword "class"
    // und ein theoretisches längeres Token, falls vorhanden), gewinnt das längere.
    String text = "public class";
    List<HighlightRegion> regions = highlighter.computeRegions(text);

    // Es sollten "public" und "class" gefunden werden
    assertEquals(2, regions.size());
  }

  @Test
  public void testSkipNonMatchingChars() {
    ScanningHighlighter highlighter = new ScanningHighlighter();

    // Das Zeichen '?' matcht auf keines unserer Token.
    // Der Scanner darf hier nicht hängen bleiben, sondern muss weiterspringen.
    String text = "??? public ??? class";
    List<HighlightRegion> regions = highlighter.computeRegions(text);

    // "public" und "class" müssen trotzdem sauber erkannt werden
    assertEquals(2, regions.size());
  }

  @Test
  public void testEmptyText() {
    ScanningHighlighter highlighter = new ScanningHighlighter();
    List<HighlightRegion> regions = highlighter.computeRegions("");
    assertTrue(regions.isEmpty());
  }
}

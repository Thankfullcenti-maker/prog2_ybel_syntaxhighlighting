package highlighting.regex;

import static org.junit.jupiter.api.Assertions.*;

import highlighting.core.HighlightRegion;
import java.util.List;
import org.junit.jupiter.api.Test;

public class RegexHighlighterTest {

  @Test
  public void testNoOverlapsAllowed() {
    RegexHighlighter highlighter = new RegexHighlighter();

    // Simuliere einen Text mit einem Keyword in einem einzeiligen Kommentar
    String text = "// public class Test";

    // Berechne die Regionen (Nutzt collectMatches -> normalize -> resolveConflicts)
    List<HighlightRegion> regions = highlighter.computeRegions(text);

    // Der Kommentar startet bei 0. Das Keyword "public" fängt bei Index 3 an.
    // Da der Kommentar zuerst kommt und länger ist, darf das Keyword NICHT enthalten sein!
    // Es sollte also nur genau 1 Region (der Kommentar selbst) übrig bleiben.
    assertEquals(1, regions.size());
  }

  @Test
  public void testConsecutiveRegions() {
    RegexHighlighter highlighter = new RegexHighlighter();

    // Zwei getrennte Keywords hintereinander (z. B. "public class")
    String text = "public class";
    List<HighlightRegion> regions = highlighter.computeRegions(text);

    // "public" [0, 6) und "class" [7, 12) überlappen nicht. Beide müssen bleiben.
    assertEquals(2, regions.size());
  }

  @Test
  public void testEmptyText() {
    RegexHighlighter highlighter = new RegexHighlighter();

    List<HighlightRegion> regions = highlighter.computeRegions("");
    assertTrue(regions.isEmpty());
  }
}

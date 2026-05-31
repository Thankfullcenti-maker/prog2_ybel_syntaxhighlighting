package highlighting.regex;

import highlighting.core.HighlightRegion;
import highlighting.core.SyntaxHighlighter;
import highlighting.presets.MiniJavaTokens;
import java.util.ArrayList;
import java.util.List;

public class RegexHighlighter extends SyntaxHighlighter {

  @Override
  public List<HighlightRegion> collectMatches(String text) {
    List<HighlightRegion> allCandidates = new ArrayList<>();

    // Alle definierten Token holen
    List<Token> tokens = MiniJavaTokens.defaultTokens();

    // Jedes Token unabhängig auf den gesamten Text anwenden
    for (Token token : tokens) {
      List<HighlightRegion> matches = token.test(text);
      if (matches != null) {
        allCandidates.addAll(matches);
      }
    }

    return allCandidates;
  }

  @Override
  public List<HighlightRegion> resolveConflicts(List<HighlightRegion> regions) {
    List<HighlightRegion> acceptedRegions = new ArrayList<>();

    // Gierig von vorne nach hinten durchgehen (Liste ist bereits vorsortiert)
    for (HighlightRegion r : regions) {
      boolean hasOverlap = false;

      for (HighlightRegion s : acceptedRegions) {
        // Intervall-Überlappung prüfen bei halb-offenen Intervallen [start, end)
        if (r.start() < s.end() && s.start() < r.end()) {
          hasOverlap = true;
          break;
        }
      }

      // Wenn kein Konflikt mit einer bereits genommenen Region vorliegt: behalten
      if (!hasOverlap) {
        acceptedRegions.add(r);
      }
    }

    return acceptedRegions;
  }
}

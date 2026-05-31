package highlighting.regex;

import highlighting.core.HighlightRegion;
import highlighting.core.SyntaxHighlighter;
import highlighting.presets.MiniJavaTokens;
import java.util.ArrayList;
import java.util.List;

public class ScanningHighlighter extends SyntaxHighlighter {

  @Override
  public List<HighlightRegion> collectMatches(String text) {
    List<HighlightRegion> result = new ArrayList<>();
    List<Token> tokens = MiniJavaTokens.defaultTokens();
    int i = 0;
    int length = text.length();

    // Wir wandern zeichenweise von links nach rechts durch den Text
    while (i < length) {
      String remainingText = text.substring(i);

      HighlightRegion bestMatch = null;
      int bestTokenIndex = -1;

      // Prüfe alle verfügbaren Token an der aktuellen Position i
      for (int t = 0; t < tokens.size(); t++) {
        Token token = tokens.get(t);
        List<HighlightRegion> matches = token.test(remainingText);

        if (matches != null && !matches.isEmpty()) {
          // Wir suchen nach Treffern, die GENAU am Anfang des Resttextes (Index 0) beginnen
          for (HighlightRegion match : matches) {
            if (match.start() == 0 && match.end() > 0) {

              // Kriterien: Längstes Match gewinnt.
              // Bei Gleichstand gewinnt das Token, das weiter vorne in der Liste steht (t <
              // bestTokenIndex).
              if (bestMatch == null
                  || match.end() > bestMatch.end()
                  || (match.end() == bestMatch.end() && t < bestTokenIndex)) {

                // Absolute Position im Gesamttext berechnen
                bestMatch = new HighlightRegion(i + match.start(), i + match.end(), match.colour());

                bestTokenIndex = t;
              }
            }
          }
        }
      }

      // Auswertung des besten Treffers für Position i
      if (bestMatch != null) {
        result.add(bestMatch);
        // Wenn ein Token passt: Index direkt hinter das gefundene Match setzen
        i = bestMatch.end();
      } else {
        // Wenn kein Token passt: Index um ein Zeichen erhöhen (Vermeidet Endlosschleifen)
        i++;
      }
    }

    return result;
  }

  // Überlagert die Normalisierung als Identitätsfunktion, da collectMatches
  // bereits eine perfekt sortierte und überschneidungsfreie Liste liefert.
  @Override
  public List<HighlightRegion> normalize(List<HighlightRegion> candidates) {
    return candidates;
  }
}

package compleat.datatypes;

import java.util.HashMap;

import io.magicthegathering.javasdk.resource.Card;

/**
 * Represents a segment of the Guide which we may have found potential matches for
 * @author nossr50
 *
 */
public class GuideChunk {
    public String chunk;
    public HashMap<Card, Integer> potentialMatches = new HashMap<Card, Integer>();
    private boolean hasMatches = false;
    
    public GuideChunk(String string)
    {
        chunk = string;
    }
    
    public void addMatches(Card card, int editDistance)
    {
        potentialMatches.put(card, editDistance);
    }
}

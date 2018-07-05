package compleat.datatypes;

import java.util.ArrayList;

/**
 * This class is an array of GuideChunks which represent a line in the Guide
 * @author nossr50
 * @see compleat.datatypes.GuideChunk
 */
public class GuideLine {
    private ArrayList<GuideChunk> stringChunks = new ArrayList<GuideChunk>();

    /**
     * Constructs chunks based on raw unaltered input
     * @param rawLine the raw unedited line from the guide
     */
    public GuideLine(String rawLine)
    {
        constructChunks(rawLine);
    }

    private void constructChunks(String line)
    {

    }

    
}

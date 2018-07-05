package compleat.tools;

import java.util.Arrays;

/**
 * This class contains helper functions to compare the likeness of strings (Levenshtein distance)
 * @author nossr50
 *
 */
public class EditDistance {
    
    /**
     * This method compares the distance between two strings (Levenshtein distance)
     * <p> Levenshtein Distance is simple the number of edits needed to convert one string into the other
     * @param a The first string
     * @param b The second string
     * @return the numeric distance between the strings
     * @see <a href="https://en.wikipedia.org/wiki/Levenshtein_distance">Levenshtein Distance Wikipedia Page</a>
     */
    public static int getDistance(String x, String y)
    {
        if(x.isEmpty())
            return y.length();
        
        if(y.isEmpty())
            return x.length();
        
        int substitution = getDistance(x.substring(1), y.substring(1)) 
                + costOfSubstitution(x.charAt(0), y.charAt(0));
        int insertion = getDistance(x, y.substring(1)) + 1;
        int deletion = getDistance(x.substring(1), y) + 1;
        
        return min(substitution, insertion, deletion);
    }

    public static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    public static int min(int... numbers) {
        return Arrays.stream(numbers)
          .min().orElse(Integer.MAX_VALUE);
    }
}

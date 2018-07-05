package compleat.scripts;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import compleat.Main;
import compleat.Manager;
import compleat.datatypes.Guide;
import compleat.tools.EditDistance;
import io.magicthegathering.javasdk.resource.Card;

/**
 * This class contains the script that parses through deck guides trying to match words to existing MTG cards
 * @author nossr50
 *
 */
public class GuideScript {

    private static HashMap<String, Card> simpleCardMap; //List containing all cards
    private static HashMap<Character, HashMap<String, Card>> allCardMapIndex = new HashMap<Character, HashMap<String,Card>>(); //Hashmap index to split up our hashmap into multiple maps
    private static int cardCount = 0;
    
    /**
     * Downloads all cards for standard and then adds them to our simpleCardMap local var
     */
    public static void downloadDatabaseStandard()
    {
        addGuides();
        
        //Initialize our map
        simpleCardMap = new HashMap<String, Card>(2000); //Standard has around 1500 cards so this might be a bit overboard
        
        //Download cards for each set in standard currently
        List<Card> XLN      = Manager.getCards("set=XLN");
        List<Card> RIX      = Manager.getCards("set=RIX");
        List<Card> AKH      = Manager.getCards("set=AKH");
        List<Card> HOU      = Manager.getCards("set=HOU");
        List<Card> KLD      = Manager.getCards("set=KLD");
        List<Card> AER      = Manager.getCards("set=AER");
        List<Card> W17      = Manager.getCards("set=W17");
        List<Card> M19      = Manager.getCards("set=M19");
        
        //Add cards from each set into the map
        addCardsToSimpleMap(XLN);
        addCardsToSimpleMap(RIX);
        addCardsToSimpleMap(AKH);
        addCardsToSimpleMap(HOU);
        addCardsToSimpleMap(KLD);
        addCardsToSimpleMap(AER);
        addCardsToSimpleMap(W17);
        if(M19 != null && M19.size() > 0) //Currently M19 is not in the DB so we'll check it for being null / empty to avoid NPE
            addCardsToSimpleMap(M19);
        
        System.out.println("Finished adding cards to map: "+cardCount);
        
        ArrayList<Card> cardsMatching = getCardsPartiallyMatchingString("scarab");
        
        System.out.println("Matching cards: ");
        
        if(cardsMatching.size() > 0)
            for(Card c : cardsMatching)
            {
                System.out.println(c.getName());
            }
        
        
    }
    
    /**
     * Adds cards into an indexed map of other maps (map inception) using the first character of the Card's name as the target map to store it into.
     * <p> This is for very large data-sets (for example, sorting through all 20,000~ MTG cards)
     * @param queryResults The cards to add to the map
     */
    private static void addCardsToIndexedMap(List<Card> queryResults)
    {
        for(Card card : queryResults)
        {
            char firstCharacter = card.getName().toCharArray()[0]; //The first char in our cards name will be used to grab the relevant char-specific map to reduce lookup times
            
            //First check if the map for the first character of this String is initialized yet
            if(allCardMapIndex.get(firstCharacter) == null)
            {
                allCardMapIndex.put(firstCharacter, new HashMap<String, Card>(100)); //Initialize our new map at 100 size since MTG has a silly number of cards
            }
            
            String cardName = card.getName();
            
            //If our card doesn't exist in its character-specific map lets add it
            if(allCardMapIndex.get(firstCharacter).get(cardName) == null)
            {
                allCardMapIndex.get(firstCharacter).put(cardName, card);
                cardCount+=1;
            }
        }
     
        System.out.println("Card Count: "+cardCount);
    }
    
    /**
     * Adds cards to a map that isn't split up into multiple smaller maps, this is for smaller data-sets
     * @param queryResults The cards to add to the map
     */
    private static void addCardsToSimpleMap(List<Card> queryResults)
    {
        for(Card card : queryResults)
        {
            String cardName = card.getName();
            
            //Add it to the map only if there isn't already an instance of that card in the map (MTG contains multiple cards with the same name in different sets)
            if(simpleCardMap.get(cardName) == null)
            {
                simpleCardMap.put(cardName, card);
                cardCount+=1;
            }
        }
     
        System.out.println("Card Count: "+cardCount);
    }
    
    /**
     * Attempts to match the String with MTG cards in the map, it looks for partial matches rather than complete ones
     * @param string The string to attempt partial matching
     * @return all partial matches found
     */
    private static ArrayList<Card> getCardsPartiallyMatchingString(String string)
    {
        ArrayList<Card> matchCandidates = new ArrayList<Card>();
        
        if(simpleCardMap != null && simpleCardMap.size() > 0)
        {
            for(Card card : simpleCardMap.values())
            {
                int distance = EditDistance.getDistance(string, card.getName());
                if(distance < 3)
                {
                    //Card is likely to be a match
                    matchCandidates.add(card);
                }
                
                System.out.println("[DISTANCE] : ("+string+") ("+card.getName()+") ["+distance+"]");
            }
        }
        
        return matchCandidates;
    }
    
    private static List<String> compareStrings()
    {
        return null;
    }
    
    private static void addGuides()
    {
        File dir = new File(Main.impDir + File.separator + "guides");
        
        File[] files = dir.listFiles();
        
        //Initialize each one
        for (File curFile : files )
        {
            if(!curFile.isDirectory())
                Manager.addGuide(curFile);
        }
        
        System.out.println("[DEBUG] Found "+files.length+" guides!");
    }
    
    private static void convertGuideIntoChunks()
    {
        for(Guide g : Manager.getGuides())
        {
            for(String curLine : g.getRawContents())
            {
                
            }
        }
    }
    
    private String getSimpleName(String string)
    {
        String newName = "";

        for(char x : string.toLowerCase().toCharArray())
        {
            if(isSimpleChar(x))
            {
                newName+=x;
            }
        }
        
        return newName;
    }

    private boolean isSimpleChar(char x)
    {
        switch(x)
        {
        case 'a':
        case 'b':
        case 'c':
        case 'd':
        case 'e':
        case 'f':
        case 'g':
        case 'h':
        case 'i':
        case 'j':
        case 'k':
        case 'l':
        case 'm':
        case 'n':
        case 'o':
        case 'p':
        case 'q':
        case 'r':
        case 's':
        case 't':
        case 'u':
        case 'v':
        case 'w':
        case 'x':
        case 'y':
        case 'z':
            return true;
        default:
            return false;
        }
    }
    
    private boolean isFiltered(String s)
    {
        List<String> filters = Arrays.asList("the", "and", "or", "but", "that", "this"); //filters subject to change
        
        for(String f : filters)
        {
            if(f.toLowerCase().equals(s.toLowerCase()))
                    return true;
        }
        
        return false;
    }
}

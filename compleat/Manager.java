package compleat;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import compleat.datatypes.Deck;
import io.magicthegathering.javasdk.api.CardAPI;
import io.magicthegathering.javasdk.resource.Card;

/**
 * This class facilitates the queries done through the MTG-API and provides several helper functions for Cards stored in our local cache
 * 
 * <p> It makes sure we never execute any needless queries by storing results of previous queries into a HashMap, 
 *  as the HTTPS requests are an expensive operation sometimes taking 10-20 seconds depending on the server load of mtgjson.com
 * <p> Not to mention the rate limit of 5000 requests an hour for http://mtgjson.com/ queries
 * 
 * @see <a href=https://magicthegathering.io/>MTG-API</a>
 * @see <a href=https://mtgjson.com/>MTG JSON DB</a>
 * 
 * @author nossr50
 *
 */
public class Manager {

    //Cache for cards we've already requested to reduce needless https requests on http://mtgjson.com/
    private static HashMap<String, Card> cardCache = new HashMap<String, Card>();
    //Array of Decks which is constructed by our DeckScript class when it scans for files in the import folder
    private static ArrayList<Deck>       deckArray = new ArrayList<Deck>();

    /**
     * Grabs an instance of card if it exists in our local cache, otherwise makes a new one after performing HTTPS queries
     * <p> Detects whether or not the card is an MTGA splitcard and converts it as they are treated as individual cards in the mtgjson.com DB
     * @param cardName the MTG card by its printed name
     * @return a Card object from our local cache
     */
    public static Card getCard(String cardName) {
        //Special code to make looking up split cards work
        CharSequence splitSequence = "///";

        if (cardName.contains(splitSequence)) {
            //If the card is a split card we'll rebuild it with a new name and run that instead
            String correctedName = "";

            for (char c : cardName.toCharArray()) {
                if (c != '/') {
                    correctedName += c;
                } else {
                    break;
                }
            }

            if (cardCache.get(correctedName) != null) {
                return cardCache.get(correctedName);
            } else {
                initQuery(correctedName);
                return cardCache.get(correctedName);
            }
        } else {
            if (cardCache.get(cardName) != null) {
                return cardCache.get(cardName);
            } else {
                initQuery(cardName);
                return cardCache.get(cardName);
            }
        }

    }

    /**
     * @return ArrayList of Decks instantiated from files found in the imports folder
     * @see compleat.scripts.DeckScript#Init() Decks are added to this ArrayList here
     */
    public static ArrayList<Deck> getDecks() {
        return deckArray;
    }

    /**
     * Creates a new instance of Deck for deckFile and adds it to our hashmap
     * @param deckFile The file containing contents of an MTGA export dump or MTG card names
     */
    public static void addDeck(File deckFile) {
        System.out.println("Adding deck: " + deckFile.toString());
        Deck deck = new Deck(deckFile);
        deckArray.add(deck);
    }

    /**
     * This synchronized method checks whether or not all decks have been converted, this is used in conjunction with our GUI's start and refresh buttons
     * @see <a href="https://mtg.gamepedia.com/Compleation/">MTG Wiki article on Compleation</a>
     * @return Whether or not ALL decks have been converted by the script
     */
    synchronized public static boolean areDecksCompleat() {
        for (Deck curDeck : getDecks()) {
            if (!curDeck.isCompleat())
                return false;
        }

        return true;
    }

    /**
     * Creates a filter which is used as the query sent to the mtgjson.com DB based on the Card's name, then sends out that query
     * @param cardName The name of the card to start a query for
     * @see #executeQuery(String, List)
     */
    private static void initQuery(String cardName) {
        /*
         * Look up the card and then add it to local memory
         */
        System.out.println("[QUERY] " + cardName);

        List<Card> queryResults = getCards("name=" + cardName);

        if (queryResults == null || queryResults.isEmpty()) {
            System.out.println("[WARNING] No results found for query!");
        } else {
            executeQuery(cardName, queryResults);
        }
    }

    /**
     * Grabs the first non-promo card from a mtgjson.com DB query via the MTG-API SDK
     * @param cardName MTG card by its name
     * @param queryResults Results of MTG-API query
     * @see <a href=https://magicthegathering.io/>MTG-API</a>
     * @see <a href=https://mtgjson.com/>MTG JSON DB</a>
     */
    private static void executeQuery(String cardName, List<Card> queryResults) {
        /*
         * Multiple results for a query can include promotional cards, we do not want promotional cards.
         */
        int index = 0;
        while (index < queryResults.size()) {
            Card c = queryResults.get(index);
            //System.out.println("Result Rarity = "+c.getRarity());

            if (!c.getRarity().equals("Special")) {
                //System.out.println("[CACHE] Adding " + cardName + " to cache ("+queryResults.size()+" Results!) ");
                cardCache.put(cardName, c);
                System.out.println("checking rarity..");
                break;
            }

            //System.out.println("Loop count: " +index);
            index++;
        }
    }

    /**
     * 
     * @param filters Filters for our query, see MTG-API documentation
     * @return Cards returned from our Query (null or empty if there aren't any)
     * @see <a href=https://magicthegathering.io/>MTG-API</a>
     */
    private static ArrayList<Card> getCards(String... filters) {
        ArrayList<String> curFilters = new ArrayList<String>();

        for (String s : filters) {
            curFilters.add(s);
        }

        ArrayList<Card> cards = (ArrayList<Card>) CardAPI.getAllCards(curFilters);

        if (cards == null || cards.isEmpty()) {
            System.out.println("No results for current filters!");
            return null;
        } else {
            return cards;
        }
    }
}

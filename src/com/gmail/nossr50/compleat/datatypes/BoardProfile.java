package com.gmail.nossr50.compleat.datatypes;

import java.util.HashMap;
import java.util.Set;

import com.gmail.nossr50.compleat.datatypes.enums.CardRarityType;
import io.magicthegathering.javasdk.resource.Card;

/**
 * This class acts as a container representing the cards contained within a specific board of a MTG deck (Mainboard or Side), 
 * featuring several functions to return statistical information for cards contained within
 * @author nossr50
 */
public class BoardProfile {

    //Rare Land tracker for export file dump
    int                                      rare_lands = 0;

    private HashMap<Card, Integer>           cards;         //Tracks cards and their numbers in this specific board
    private HashMap<CardRarityType, Integer> rarityMap;     //Tracks rarity of cards

    public BoardProfile() {
        cards = new HashMap<Card, Integer>();
        rarityMap = new HashMap<CardRarityType, Integer>();
    }

    /**
     * Adds a card to this BoardProfile
     * @param card The card to add
     * @param numCards The amount of cards to add
     */
    public void addCard(Card card, int numCards) {
        if (cards.get(card) != null) {
            cards.put(card, numCards + cards.get(card));
        } else {
            cards.put(card, numCards);
        }

        processCardRarity(card, numCards);
    }

    /**
     * 
     * @param crt The rarity of the card
     * @param count The amount of cards with that rarity
     */
    public void addRares(CardRarityType crt, int count) {
        if (rarityMap.get(crt) != null) {
            rarityMap.put(crt, count + rarityMap.get(crt));
        } else {
            rarityMap.put(crt, count);
        }
    }

    /**
     * 
     * @param crt The type of rarity to poll
     * @return The amount of cards with that rarity in this board
     */
    public int getRareCount(CardRarityType crt) {
        if (rarityMap.get(crt) != null) {
            return rarityMap.get(crt);
        } else {
            return 0;
        }
    }

    /**
     * Checks the rarity of the card and adds it to our count
     * @param card The card to process rarity for
     * @param numCards The amount of cards
     */
    private void processCardRarity(Card card, int numCards) {
        CardRarityType crt = CardRarityType.GetRarity(card);

        //Check if its a rare and a land for our Rare Land count
        if (card.getType().equals("LAND") && crt == CardRarityType.RARE) {
            rare_lands += numCards;
        }

        addRares(crt, numCards);
    }

    /**
     * Returns the number of cards in this board of a specific card
     * @param card The card to check the amount of
     * @return The number of cards contained in the board
     */
    public int getCount(Card card) {
        int count = cards.get(card);
        return count;
    }
    
    public int getCountOfType(String type)
    {
        int count = 0;
        
        for(Card c : cards.keySet())
        {
            for(String s : c.getTypes())
            {
                if(s.equals(type))
                    count+=getCount(c);
            }
        }
        
        return count;
    }

    /**
     * Returns all unique cards found in the board (without the number)
     * @return All cards contained within this board (main/side)
     */
    public Set<Card> getCards() {
        return cards.keySet();
    }
}
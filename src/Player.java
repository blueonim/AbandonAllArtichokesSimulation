import java.util.*;
import java.util.stream.Collectors;

class Player {

    private static final int STARTING_DECK_SIZE = 10;
    private static final int REFILL_HAND_SIZE = 5;

    private LinkedList<Card> hand = new LinkedList<>();
    private LinkedList<Card> deck = new LinkedList<>();
    private List<Card> discard = new LinkedList<>();

    private boolean hasHarvested = false;
    private int turnCounter = 0;

    private final String name;
    private final Strategy strategy;

    Player(String name, Strategy strategy) {
        this.name = name;
        this.strategy = strategy;

        // initialize deck
        while (deck.size() < STARTING_DECK_SIZE) {
            deck.add(Card.ARTICHOKE);
        }

        // draw starting hand
        drawHand();
    }

    String getName() {
        return name;
    }

    LinkedList<Card> getHand() {
        return hand;//TODO Collections.unmodifiableList() - protect against adding nulls to hand
    }

    boolean hasCardsToPlay(Game game) {
        return hand.stream().anyMatch(card -> card.canBePlayed(game));
    }

    boolean canHarvest(Game game) {
        return !hasHarvested && !game.getGarden().isEmpty();
    }

    boolean takeTurn(Game game) {
        turnCounter ++;

        // plant a card if your hand is all Artichokes at start of turn
        if (hand.stream().allMatch(card -> card == Card.ARTICHOKE)) {
            addToDiscard(game.drawTop());
        }

        // take actions until none left
        Action action;
        while ((action = strategy.chooseNextAction(game)) != null) {
            if (action.getType() == Action.Type.HARVEST && canHarvest(game)) {
                // remove card from garden and throw if it does not exist
                if (!game.getGarden().remove(action.getCard())) {
                    throw new IllegalStateException("Card does not exist in Garden: " + action.getCard());
                }

                hand.add(action.getCard());
                hasHarvested = true;

            } else if (action.getType() == Action.Type.PLAY && action.getCard().canBePlayed(game)) {
                // remove card from hand and throw if it does not exist
                if (!hand.remove(action.getCard())) {
                    throw new IllegalStateException("Card does not exist in Hand: " + action.getCard());
                }

                discard.add(action.getCard());
                action.getCard().playCard(game);

            } else {
                // throw if invalid action is selected
                throw new IllegalStateException("Invalid action selected: " + action);
            }
        }

        if (canHarvest(game)) {
            throw new IllegalStateException("Players must harvest on their turn, if possible");
        }

        return turnEnd();
    }

    private boolean turnEnd() {
        // reset state
        hasHarvested = false;

        // redraw hand
        discard.addAll(hand);
        hand.clear();
        drawHand();

        // check if player wins
        for (Card card : hand) {
            if (card == Card.ARTICHOKE) return false;
        }
        return true;
    }

    private void drawHand() {
        while (hand.size() < REFILL_HAND_SIZE) {
            Card card = drawTop();
            if (card == null) break;
            hand.add(card);
        }
    }

    Card drawTop() {
        // if deck and discard are empty, card can't be drawn
        if (!deckOrDiscardHasCards()) return null;

        // if deck empty, shuffle in discard
        if (deck.isEmpty()) shuffleDeckAndDiscard();

        return deck.removeFirst();
    }

    void shuffleDeckAndDiscard() {
        deck.addAll(discard);
        discard.clear();
        Collections.shuffle(deck);
    }

    void putCardOnTopOfDeck(Game game) {
        Card cardToPutOnTop = strategy.pickCardForTopOfDeck(game.getCurrentPlayer().getHand());
        if (cardToPutOnTop == null) {
            throw new IllegalStateException("Null card to put on top");
        }

        if (!hand.remove(cardToPutOnTop)) {
            throw new IllegalStateException("Card to put on top not in hand: " + cardToPutOnTop.name());
        }

        deck.addFirst(cardToPutOnTop);
    }

    Player chooseOpponent(List<Player> players) {
        if (players.size() < 1) throw new IllegalStateException("Opponent list is empty");
        if (players.size() == 1) return players.get(0);
        return strategy.chooseOpponent(players);
    }

    boolean doesWantCard(Card card) {
        return strategy.doesWantCard(card);
    }

    void discardNonArtichoke(Game game) {
        Card cardToDiscard = strategy.pickNonArtichokeToDiscard(game.getCurrentPlayer().getHand().stream()
                .filter(card -> card != Card.ARTICHOKE).collect(Collectors.toList()));

        if (cardToDiscard == null) {
            throw new IllegalStateException("Null card to discard");
        }

        if (cardToDiscard == Card.ARTICHOKE) {
            throw new IllegalStateException("Cannot select Artichoke as non-Artichoke");
        }

        if (!hand.remove(cardToDiscard)) {
            throw new IllegalStateException("Card to discard not in hand: " + cardToDiscard.name());
        }

        addToDiscard(cardToDiscard);
    }

    boolean deckHasCards() {
        return !deck.isEmpty();
    }

    boolean deckOrDiscardHasCards() {
        return !deck.isEmpty() || !discard.isEmpty();
    }

    void addToDiscard(Card card) {
        if (card != null) discard.add(card);
    }

    @SuppressWarnings("SameParameterValue")
    boolean doesDiscardContain(Card card) {
        return discard.contains(card);
    }

    void printStatus() {
        System.out.println(name);
        System.out.println("Turns taken: " + turnCounter);
        System.out.println("Hand: " + hand.stream().map(Enum::name).collect(Collectors.joining(", ")));
        System.out.println("Deck: " + deck.size() + ", Discard: " + discard.size());

        List<Card> allCards = new ArrayList<>();
        allCards.addAll(hand);
        allCards.addAll(deck);
        allCards.addAll(discard);
        System.out.println("Total: " + allCards.size());

        Map<Card, Integer> counts = new HashMap<>();
        for (Card card : allCards) {
            if (!counts.containsKey(card)) {
                counts.put(card, 0);
            }
            counts.put(card, counts.get(card) + 1);
        }
        System.out.println(counts.entrySet().stream()
                .map(entry -> entry.getKey().name() + ":" + entry.getValue())
                .collect(Collectors.joining(", ")));

        System.out.println();
    }
}

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class Player {

    private static final int STARTING_DECK_SIZE = 10;
    private static final int REFILL_HAND_SIZE = 5;

    private LinkedList<Card> hand = new LinkedList<>();
    private LinkedList<Card> deck = new LinkedList<>();
    private List<Card> discard = new LinkedList<>();

    private final String name;
    private final Strategy strategy;

    private boolean hasHarvested = false;
    private int turnCounter = 0;

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

    List<Card> getDeck() {
        return Collections.unmodifiableList(deck);
    }

    List<Card> getDiscard() {
        return Collections.unmodifiableList(discard);
    }

    int getNumberOfTurnsTaken() {
        return turnCounter;
    }

    boolean hasCardsToPlay(Game game) {
        return hand.stream().anyMatch(card -> card.canBePlayed(game));
    }

    boolean canHarvest(Game game) {
        return !hasHarvested && !game.getGarden().isEmpty();
    }

    boolean takeTurn(Game game, Set<Logger> loggers) {
        turnCounter ++;

        // plant a card if your hand is all Artichokes at start of turn
        if (hand.stream().allMatch(card -> card == Card.ARTICHOKE)) {
            Card card = game.drawTop();
            loggers.forEach(logger -> logger.cardPlanted(card));
            addToDiscard(card);
        }

        // take actions until none left
        Action action;
        while ((action = strategy.chooseNextAction(game)) != null) {
            for (Logger logger : loggers) {
                logger.actionChosen(action);
            }

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
        return hand.stream().noneMatch(card -> card == Card.ARTICHOKE);
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
        if (game.getCurrentPlayer().getHand().isEmpty()) {
            throw new IllegalStateException("No card in hand to put on top of deck");
        }

        Card card = null;
        if (game.getCurrentPlayer().getHand().size() == 1) card = game.getCurrentPlayer().getHand().get(0);
        if (card == null) card = strategy.pickCardForTopOfDeck(game.getCurrentPlayer().getHand());

        if (card == null) throw new IllegalStateException("Null card to put on top");
        if (!hand.remove(card)) throw new IllegalStateException("Card to put on top not in hand: " + card.name());

        deck.addFirst(card);
    }

    Player chooseOpponent(List<Player> players) {
        if (players.isEmpty()) throw new IllegalStateException("Opponent list is empty");
        if (players.size() == 1) return players.get(0);
        return strategy.chooseOpponent(players);
    }

    boolean doesWantCard(Card card) {
        return strategy.doesWantCard(card);
    }

    void discardNonArtichoke(Game game) {
        List<Card> cards = game.getCurrentPlayer().getHand().stream()
                .filter(card -> card != Card.ARTICHOKE).collect(Collectors.toList());

        if (cards.isEmpty()) throw new IllegalStateException("No valid card to discard");

        Card card = null;
        if (cards.size() == 1) card = cards.get(0);
        if (card == null) card = strategy.pickNonArtichokeToDiscard(cards);

        if (card == null) throw new IllegalStateException("Null card to discard");
        if (card == Card.ARTICHOKE) throw new IllegalStateException("Cannot select Artichoke as non-Artichoke");
        if (!hand.remove(card)) throw new IllegalStateException("Card to discard not in hand: " + card.name());

        addToDiscard(card);
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
}

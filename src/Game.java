import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

class Game {

    private static final int REFILL_GARDEN_SIZE = 5;

    private final LinkedList<Player> players = new LinkedList<>();
    private final LinkedList<Card> deck = new LinkedList<>();
    private final LinkedList<Card> garden = new LinkedList<>();
    private final Set<Logger> loggers = new HashSet<>();

    private Player currentPlayer;

    private Game(List<Player> players, List<Card> deck, Set<Logger> loggers) {
        this.players.addAll(players);
        this.deck.addAll(deck);
        this.loggers.addAll(loggers);
    }

    private void start() {
        refillGarden();
        nextPlayersTurn();
    }

    Player getCurrentPlayer() {
        return currentPlayer;
    }

    List<Player> getOpponents() {
        return players;
    }

    boolean hasOpponentWithHand() {
        return players.stream().anyMatch(player -> !player.getHand().isEmpty());
    }

    boolean hasOpponentWithDeckOrDiscard() {
        return players.stream().anyMatch(Player::deckOrDiscardHasCards);
    }

    private void nextPlayersTurn() {
        if (currentPlayer != null) players.addLast(currentPlayer);
        currentPlayer = players.removeFirst();

        loggers.forEach(logger -> logger.startTurn(currentPlayer));
        boolean didWin = currentPlayer.takeTurn(this, loggers);
        loggers.forEach(logger -> logger.endTurn(currentPlayer));

        if (didWin) {
            loggers.forEach(logger -> logger.gameOver(this));
            return;
        }

        // setup for next turn
        refillGarden();
        nextPlayersTurn();
    }

    private void refillGarden() {
        while (garden.size() < REFILL_GARDEN_SIZE) {
            Card card = drawTop();
            if (card == null) return;
            garden.addFirst(card);
        }
        loggers.forEach(logger -> logger.gardenRefreshed(garden));
    }

    LinkedList<Card> getGarden() {
        return garden;
    }

    Card drawTop() {
        if (deck.isEmpty()) return null;
        return deck.removeFirst();
    }

    void compostCard(Card card, Player owner) {
        if (card == null) return;
        switch (card.getCompostAction()) {
            case ADD_TO_BOTTOM:
                deck.addLast(card);
                break;
            case DISCARD:
                owner.addToDiscard(card);
                break;
            case REMOVE:
            default:
                // card removed from the game
                break;
        }
    }

    static class Builder {
        private List<Player> players = new ArrayList<>();
        private List<Card> deck = new ArrayList<>();
        private Set<Logger> loggers = new HashSet<>();

        Builder addPlayer(Player player) {
            players.add(player);
            return this;
        }

        @SuppressWarnings("SameParameterValue")
        Builder addToDeck(Card card, int number) {
            for (int i = 0; i < number; i++) {
                deck.add(card);
            }
            return this;
        }

        Builder addLogger(Logger logger) {
            loggers.add(logger);
            return this;
        }

        void start() {
            Collections.shuffle(players);
            Collections.shuffle(deck);
            new Game(players, deck, loggers).start();
        }
    }
}

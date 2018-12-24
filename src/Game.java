import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

class Game {

    private static final int REFILL_GARDEN_SIZE = 5;

    //TODO record stats about game (check for first player advantage, ect)

    private LinkedList<Player> players = new LinkedList<>();
    private Player currentPlayer;

    private LinkedList<Card> deck = new LinkedList<>();
    private LinkedList<Card> garden = new LinkedList<>();

    private Game(List<Player> players, List<Card> deck) {
        this.players.addAll(players);
        this.deck.addAll(deck);
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
        for (Player player : players) {
            if (!player.getHand().isEmpty()) return true;
        }
        return false;
    }

    boolean hasOpponentWithDeckOrDiscard() {
        for (Player player : players) {
            if (player.deckOrDiscardHasCards()) return true;
        }
        return false;
    }

    private void nextPlayersTurn() {
        if (currentPlayer != null) players.addLast(currentPlayer);
        currentPlayer = players.removeFirst();

        boolean didWin = currentPlayer.takeTurn(this);

        currentPlayer.printStatus();

        if (didWin) {
            //TODO collection some stats
            System.out.println("Game Over");
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
        printStatus();
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

    private void printStatus() {
        //TODO add more here
        System.out.println("Garden: " + garden.stream().map(Enum::name).collect(Collectors.joining(", ")));
        System.out.println();
    }

    static class Builder {
        private List<Player> players = new ArrayList<>();
        private List<Card> deck = new ArrayList<>();

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

        void start() {
            //TODO add validation
            Collections.shuffle(players);
            Collections.shuffle(deck);
            new Game(players, deck).start();
        }
    }
}

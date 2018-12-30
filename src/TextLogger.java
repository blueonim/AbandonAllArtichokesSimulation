import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class TextLogger implements Logger {

    @Override
    public void gardenRefreshed(List<Card> garden) {
        System.out.println("Garden: " + garden.stream().map(Enum::name).collect(Collectors.joining(", ")));
    }

    @Override
    public void startTurn(Player player) {
        System.out.println();
        System.out.println("Start Turn" + " - " + player.getName());
        printPlayerStatus(player);
    }

    @Override
    public void cardPlanted(Card card) {
        System.out.println("PLANT " + card.name());
    }

    @Override
    public void actionChosen(Action action) {
        System.out.println(action);
    }

    @Override
    public void endTurn(Player player) {
        printPlayerStatus(player);
        System.out.print("End Turn" + " - " + player.getName());
        System.out.println(" - Turns taken: " + player.getNumberOfTurnsTaken());
        System.out.println();
    }

    @Override
    public void gameOver(Game game) {
        System.out.println(game.getCurrentPlayer().getName() + " wins!");
    }

    private void printPlayerStatus(final Player player) {
        System.out.println("Hand: " + player.getHand().stream().map(Enum::name)
                .collect(Collectors.joining(", ")));
        System.out.print("Deck: " + player.getDeck().size() + ", Discard: " + player.getDiscard().size());

        List<Card> allCards = player.getAllCards();
        System.out.println(" Total: " + allCards.size());

        Map<Card, Integer> counts = new HashMap<>();
        for (Card card : allCards) {
            if (!counts.containsKey(card)) counts.put(card, 0);
            counts.put(card, counts.get(card) + 1);
        }
        System.out.println(counts.entrySet().stream()
                .map(entry -> entry.getKey().name() + ":" + entry.getValue())
                .collect(Collectors.joining(", ")));
    }
}

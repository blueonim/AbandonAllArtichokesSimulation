import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

class ManualStrategy implements Strategy {

    private final Scanner scanner;

    ManualStrategy() {
        scanner = new Scanner(System.in);
    }

    @Override
    public Action chooseNextAction(final Game game) {
        if (!game.getCurrentPlayer().canHarvest(game) && !game.getCurrentPlayer().hasCardsToPlay(game)) {
            return null;
        }

        List<Action> actions = new ArrayList<>();
        if (game.getCurrentPlayer().canHarvest(game)) {
            game.getGarden().stream()
                    .map(card -> new Action(Action.Type.HARVEST, card))
                    .forEachOrdered(actions::add);
        }

        game.getCurrentPlayer().getHand().stream()
                .filter(card -> card.canBePlayed(game))
                .map(card -> new Action(Action.Type.PLAY, card))
                .forEachOrdered(actions::add);

        // prompt player
        System.out.println();
        System.out.println("Garden: " + game.getGarden().stream().map(Enum::name)
                .collect(Collectors.joining(", ")));
        System.out.println("Hand: " + game.getCurrentPlayer().getHand().stream().map(Enum::name)
                .collect(Collectors.joining(", ")));
        System.out.print("Deck: " + game.getCurrentPlayer().getDeck().size() + " - Discard: ");
        System.out.println(game.getCurrentPlayer().getDiscard().stream().map(Enum::name)
                .collect(Collectors.joining(", ")));
        System.out.println();
        System.out.println("Choose an action:");

        // number options 1 - n
        int position = 1;
        for (Action action : actions) {
            System.out.println(position + " - " + action.getType() + " " + action.getCard());
            position++;
        }

        boolean hasEndTurnOption = false;
        if (!game.getCurrentPlayer().canHarvest(game)) {
            System.out.println(position + " - End Turn");
            hasEndTurnOption = true;
        }

        // read input
        int input = readInput(actions.size() + (hasEndTurnOption ? 1 : 0));
        if (hasEndTurnOption && input == position) return null;

        // adjust for true position
        return actions.get(input - 1);
    }

    @Override
    public Card pickCardForTopOfDeck(final List<Card> cards) {
        System.out.println("Pick card to put on top of deck:");

        // number options 1 - n
        int position = 1;
        for (Card card : cards) {
            System.out.println(position + " - " + card);
            position++;
        }

        // read input and adjust for true position
        int input = readInput(cards.size());
        return cards.get(input - 1);
    }

    @Override
    public Player chooseOpponent(final List<Player> players) {
        System.out.println("Choose an opponent to target:");

        // number options 1 - n
        int position = 1;
        for (Player player : players) {
            System.out.println(position + " - " + player.getName());
            position++;
        }

        // read input and adjust for true position
        int input = readInput(players.size());
        return players.get(input - 1);
    }

    @Override
    public Card pickNonArtichokeToDiscard(final List<Card> cards) {
        System.out.println("Pick non-Artichoke to discard:");

        // number options 1 - n
        int position = 1;
        for (Card card : cards) {
            System.out.println(position + " - " + card);
            position++;
        }

        // read input and adjust for true position
        int input = readInput(cards.size());
        return cards.get(input - 1);
    }

    @Override
    public boolean doesWantCard(final Card card) {
        System.out.println("Do you want to take " + card + "?");
        System.out.println("1 - Yes");
        System.out.println("2 - No");

        int input = readInput(2);
        return input == 1;
    }

    private int readInput(final int max) {
        int value = scanner.nextInt();
        if (value < 1 || value > max) {
            System.out.println("Number not in valid range: 0 - " + max);
            System.out.println("Try again:");
            return readInput(max);
        }
        return value;
    }
}

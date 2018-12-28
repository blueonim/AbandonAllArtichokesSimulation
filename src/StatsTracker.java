import java.util.*;

class StatsTracker implements Logger {

    private final Map<String, Integer> winTracker = new HashMap<>();
    private final Map<Integer, Integer> gameLengthTracker = new HashMap<>();
    private final Map<Long, Integer> winsByTurnPosition = new HashMap<>();
    private int totalGames = 0;

    void printTotalStats() {
        System.out.println();
        if (totalGames < 1 || winTracker.isEmpty()) {
            System.out.println("No stats to report");
            return;
        }

        System.out.println("Player wins:");
        printMap(winTracker);

        System.out.println();
        System.out.println("Game lengths:");
        printMap(gameLengthTracker);

        System.out.println();
        System.out.println("Wins by turn position:");
        printMap(winsByTurnPosition);
    }

    private void printMap(final Map<?, Integer> map) {
        List<Map.Entry<?, Integer>> playerWins = new ArrayList<>(map.entrySet());
        playerWins.sort(Comparator.comparing(Map.Entry::getValue));
        Collections.reverse(playerWins);
        playerWins.forEach(entry -> System.out.println(entry.getKey() + " - " + entry.getValue() + " (" +
                (Math.round((entry.getValue() * 100f) / totalGames)) + "%)"));
    }

    @Override
    public void gardenRefreshed(List<Card> garden) {
        // no-op
    }

    @Override
    public void startTurn(Player player) {
        // no-op
    }

    @Override
    public void cardPlanted(Card card) {
        // no-op
    }

    @Override
    public void actionChosen(Action action) {
        // no-op
    }

    @Override
    public void endTurn(Player player) {
        // no-op
    }

    @Override
    public void gameOver(Game game) {
        totalGames ++;

        // track player wins
        String name = game.getCurrentPlayer().getName();
        if (!winTracker.containsKey(name)) winTracker.put(name, 0);
        winTracker.put(name, winTracker.get(name) + 1);

        // track game lengths
        int length = game.getCurrentPlayer().getNumberOfTurnsTaken();
        if (!gameLengthTracker.containsKey(length)) gameLengthTracker.put(length, 0);
        gameLengthTracker.put(length, gameLengthTracker.get(length) + 1);

        // track wins by turn position
        long position = game.getOpponents().stream()
                .filter(player -> player.getNumberOfTurnsTaken() >= game.getCurrentPlayer().getNumberOfTurnsTaken())
                .count() + 1;
        if (!winsByTurnPosition.containsKey(position)) winsByTurnPosition.put(position, 0);
        winsByTurnPosition.put(position, winsByTurnPosition.get(position) + 1);
    }
}

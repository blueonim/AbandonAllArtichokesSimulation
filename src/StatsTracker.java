import java.util.*;

class StatsTracker implements Logger {

    private final Map<String, Integer> winTracker = new HashMap<>();
    private final Map<Integer, Integer> gameLengthTracker = new HashMap<>();
    private final Map<Long, Integer> winsByTurnPosition = new HashMap<>();
    private final Map<Integer, Integer> winnerArtichokeRate = new HashMap<>();
    private final Map<Integer, Integer> allArtichokeRate = new HashMap<>();
    private final Map<Integer, Integer> gardenSize = new HashMap<>();
    private int totalGames = 0;
    private int totalDecks = 0;

    void printTotalStats() {
        System.out.println();
        if (totalGames < 1 || winTracker.isEmpty()) {
            System.out.println("No stats to report");
            return;
        }

        System.out.println("Player wins:");
        printMap(winTracker, totalGames);

        System.out.println();
        System.out.println("Game lengths:");
        printMap(gameLengthTracker, totalGames);

        System.out.println();
        System.out.println("Wins by turn position:");
        printMap(winsByTurnPosition, totalGames);

        System.out.println();
        System.out.println("Remaining garden size:");
        printMap(gardenSize, totalGames);

        System.out.println();
        System.out.println("Winner percentage of Artichokes:");
        printMap(winnerArtichokeRate, totalGames);

        System.out.println();
        System.out.println("All percentage of Artichokes:");
        printMap(allArtichokeRate, totalDecks);
    }

    private void printMap(final Map<?, Integer> map, final int percentBase) {
        List<Map.Entry<?, Integer>> playerWins = new ArrayList<>(map.entrySet());
        playerWins.sort(Comparator.comparing(Map.Entry::getValue));
        Collections.reverse(playerWins);
        playerWins.forEach(entry -> System.out.println(entry.getKey() + " - " + entry.getValue() + " (" +
                (Math.round((entry.getValue() * 100f) / percentBase)) + "%)"));
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
        totalDecks = totalDecks + game.getOpponents().size() + 1;

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

        // track Artichoke rate in winning deck
        int winnerRateBucket = (int) Math.ceil(game.getCurrentPlayer().getArtichokeRate() * 10) * 10;
        if (!winnerArtichokeRate.containsKey(winnerRateBucket)) winnerArtichokeRate.put(winnerRateBucket, 0);
        winnerArtichokeRate.put(winnerRateBucket, winnerArtichokeRate.get(winnerRateBucket) + 1);

        // track Artichoke rate in all decks
        if (!allArtichokeRate.containsKey(winnerRateBucket)) allArtichokeRate.put(winnerRateBucket, 0);
        allArtichokeRate.put(winnerRateBucket, allArtichokeRate.get(winnerRateBucket) + 1);
        for (Player player : game.getOpponents()) {
            int rateBucket = (int) Math.ceil(player.getArtichokeRate() * 10) * 10;
            if (!allArtichokeRate.containsKey(rateBucket)) allArtichokeRate.put(rateBucket, 0);
            allArtichokeRate.put(rateBucket, allArtichokeRate.get(rateBucket) + 1);
        }

        // track garden size
        int remainingGarden = game.getRemainingGardenSize();
        if (!gardenSize.containsKey(remainingGarden)) gardenSize.put(remainingGarden, 0);
        gardenSize.put(remainingGarden, gardenSize.get(remainingGarden) + 1);
    }
}

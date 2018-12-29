public class Runner {

    //TODO add unit tests, javadocs

    private static final int NUMBER_OF_GAMES = 10000;

    public static void main(String[] args) {
        StatsTracker statsTracker = new StatsTracker();
        for (int i = 0; i < NUMBER_OF_GAMES; i ++) {
            new Game.Builder()
                    //.addPlayer(new Player("PhilManual", new ManualStrategy()))
                    .addPlayer(new Player("CarolWildcard", new RandomStrategy()))
                    .addPlayer(new Player("JessWildcard", new RandomStrategy()))
                    .addPlayer(new Player("BobBulk", new BulkNaiveStrategy()))
                    .addPlayer(new Player("FrancisSlim", new SlimNaiveStrategy()))
                    .addPlayer(new Player("DebbieDeck", new DeckNaiveStrategy()))
                    .addToDeck(Card.POTATO, 6)
                    .addToDeck(Card.CARROT, 6)
                    .addToDeck(Card.BROCCOLI_V2, 6)
                    .addToDeck(Card.ONION, 6)
                    .addToDeck(Card.BANANA, 6)
                    .addToDeck(Card.AVOCADO, 6)
                    .addToDeck(Card.RADISH, 6)
                    .addToDeck(Card.LEMON, 6)
                    .addToDeck(Card.PEAR_V2, 6)
                    //.addLogger(new TextLogger())
                    .addLogger(statsTracker)
                    .start();
        }
        statsTracker.printTotalStats();
    }
}

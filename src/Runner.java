public class Runner {

    public static void main(String[] args) {
        new Game.Builder()
                .addPlayer(new Player("PhilManual", new ManualStrategy()))
                //.addPlayer(new Player("CarolWildcard", new RandomStrategy()))
                .addPlayer(new Player("BobBulk", new BulkNaiveStrategy()))
                .addPlayer(new Player("FrancisSlim", new SlimNaiveStrategy()))
                //.addPlayer(new Player("DebbieDeck", new DeckNaiveStrategy()))
                .addToDeck(Card.POTATO, 6)
                .addToDeck(Card.CARROT, 6)
                .addToDeck(Card.BROCCOLI, 6)
                .addToDeck(Card.ONION, 6)
                .addToDeck(Card.BANANA, 6)
                .addToDeck(Card.AVOCADO, 6)
                .addToDeck(Card.RADISH, 6)
                .addToDeck(Card.LEMON, 6)
                .addToDeck(Card.PEAR, 6)
                .addLogger(new TextLogger())
                .start();
    }
    //TODO add unit tests, javadocs

    //TODO add output interface for logging and stats
}

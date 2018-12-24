public class Runner {

    public static void main(String[] args) {
        new Game.Builder()
                .addPlayer(new Player("Carol", new RandomStrategy()))
                .addPlayer(new Player("Bob", new RandomStrategy()))
                .addPlayer(new Player("Francis", new RandomStrategy()))
                .addToDeck(Card.POTATO, 6)
                .addToDeck(Card.CARROT, 6)
                .addToDeck(Card.BROCCOLI, 6)
                .addToDeck(Card.ONION, 6)
                .addToDeck(Card.BANANA, 6)
                .addToDeck(Card.AVOCADO, 6)
                .addToDeck(Card.RADISH, 6)
                .addToDeck(Card.LEMON, 6)
                .addToDeck(Card.PEAR, 6)
                .start();
    }
    //TODO add unit tests

    //TODO version control
}

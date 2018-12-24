import java.util.List;

public interface Strategy {

    Action chooseNextAction(final Game game);

    Card pickCardForTopOfDeck(final Game game);

    Player chooseOpponent(final List<Player> players);

    Card pickNonArtichokeToDiscard(final Game game);

    boolean doesWantCard(Card card);

    //TODO strategies - Random, Bulk, Slim, Manipulate

    //TODO pass in strategy (strategy just selects card to play or pick? maybe stores some pick knowledge - should be a class, not an enum)
}

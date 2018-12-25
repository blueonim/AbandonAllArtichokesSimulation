import java.util.List;

public interface Strategy {

    Action chooseNextAction(final Game game);

    Card pickCardForTopOfDeck(final Game game);

    Player chooseOpponent(final List<Player> players);

    Card pickNonArtichokeToDiscard(final Game game);

    boolean doesWantCard(Card card);
}

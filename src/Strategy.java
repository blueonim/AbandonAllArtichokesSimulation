import java.util.List;

public interface Strategy {

    Action chooseNextAction(final Game game);

    Card pickCardForTopOfDeck(final List<Card> cards);

    Player chooseOpponent(final List<Player> players);

    Card pickNonArtichokeToDiscard(final List<Card> cards);

    Card pickCardToGiveOpponent(final List<Card> cards);

    Card pickNonArtichokeToGiveOpponent(final List<Card> cards);

    boolean doesWantCard(final Card card);
}

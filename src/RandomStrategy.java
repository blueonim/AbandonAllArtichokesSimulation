import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class RandomStrategy implements Strategy {

    @Override
    public Action chooseNextAction(final Game game) {
        // harvest a card, if possible
        if (game.getCurrentPlayer().canHarvest(game)) {
            List<Card> garden = new ArrayList<>(game.getGarden());
            Collections.shuffle(garden);
            return new Action(Action.Type.HARVEST, garden.get(0));
        }

        // choose random card to play, if any are available
        if (game.getCurrentPlayer().hasCardsToPlay(game)) {
            List<Card> hand = new ArrayList<>(game.getCurrentPlayer().getHand());
            Collections.shuffle(hand);
            Optional<Card> optionalCard = hand.stream().filter(card -> card.canBePlayed(game)).findFirst();
            if (optionalCard.isPresent()) {
                return new Action(Action.Type.PLAY, optionalCard.get());
            }
        }

        // return null if there are no valid actions
        return null;
    }

    @Override
    public Card pickCardForTopOfDeck(final List<Card> cards) {
        if (cards.isEmpty()) return null;
        if (cards.stream().anyMatch(card -> card == Card.ARTICHOKE)) return Card.ARTICHOKE;

        List<Card> hand = new ArrayList<>(cards);
        Collections.shuffle(hand);
        return hand.get(0);
    }

    @Override
    public Player chooseOpponent(final List<Player> players) {
        List<Player> opponentList = new ArrayList<>(players);
        Collections.shuffle(opponentList);
        return opponentList.get(0);
    }

    @Override
    public Card pickNonArtichokeToDiscard(final List<Card> cards) {
        List<Card> hand = new ArrayList<>(cards);
        Collections.shuffle(hand);
        return hand.stream().filter(card -> card != Card.ARTICHOKE).findFirst().orElse(null);
    }

    @Override
    public boolean doesWantCard(final Card card) {
        return card != Card.ARTICHOKE;
    }
}

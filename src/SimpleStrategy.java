import java.util.List;

abstract class SimpleStrategy extends RandomStrategy {

    abstract List<Action> playOrder(Game game);

    @Override
    public Action chooseNextAction(final Game game) {
        // check the pick order for valid actions
        for (Action action : playOrder(game)) {
            if (action.getType() == Action.Type.HARVEST
                    && game.getCurrentPlayer().canHarvest(game)
                    && game.getGarden().contains(action.getCard())) {
                return action;
            } else if (action.getType() == Action.Type.PLAY
                    && game.getCurrentPlayer().getHand().contains(action.getCard())
                    && action.getCard().canBePlayed(game)) {
                return action;
            }
        }

        // fallback to random action if no picks are valid
        return super.chooseNextAction(game);
    }
}

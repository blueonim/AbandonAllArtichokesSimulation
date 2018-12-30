import java.util.List;

abstract class SimpleStrategy extends RandomStrategy {

    abstract List<Action> playOrder(final Game game);

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

    @Override
    public Player chooseOpponent(final List<Player> players) {
        if (players.isEmpty()) throw new IllegalStateException("Opponent list is empty");
        if (players.size() == 1) return players.get(0);

        //TODO this is sort of cheating
        //TODO in the future the player information should be restricted like in a real game
        //TODO will need to track things over the game and make some guesses who the "leader" is

        // attack the leader - lowest percentage of Artichokes
        Player leader = null;
        float bestRate = 1f;
        for (Player opponent : players) {
            float rate = opponent.getArtichokeRate();
            if (leader == null || rate < bestRate) {
                leader = opponent;
                bestRate = rate;
            }
        }
        return leader;
    }
}

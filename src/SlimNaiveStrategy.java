import java.util.ArrayList;
import java.util.List;

class SlimNaiveStrategy extends SimpleStrategy {

    private static final List<Action> playOrder = new ArrayList<Action>() {{
        add(new Action(Action.Type.HARVEST, Card.POTATO));
        add(new Action(Action.Type.HARVEST, Card.BROCCOLI_V1));
        add(new Action(Action.Type.HARVEST, Card.RADISH));
        add(new Action(Action.Type.PLAY, Card.BROCCOLI_V1));
        add(new Action(Action.Type.PLAY, Card.POTATO));
        add(new Action(Action.Type.HARVEST, Card.ONION));
        add(new Action(Action.Type.HARVEST, Card.PEAR_V1));
        add(new Action(Action.Type.HARVEST, Card.PEAR_V2));
        add(new Action(Action.Type.HARVEST, Card.BROCCOLI_V2));
        add(new Action(Action.Type.HARVEST, Card.AVOCADO));
        add(new Action(Action.Type.HARVEST, Card.BANANA));
        add(new Action(Action.Type.HARVEST, Card.LEMON));
        add(new Action(Action.Type.HARVEST, Card.CARROT));
        add(new Action(Action.Type.PLAY, Card.ONION));
        add(new Action(Action.Type.PLAY, Card.RADISH));
        add(new Action(Action.Type.PLAY, Card.PEAR_V1));
        add(new Action(Action.Type.PLAY, Card.PEAR_V2));
        add(new Action(Action.Type.PLAY, Card.LEMON));
        add(new Action(Action.Type.PLAY, Card.CARROT));
        add(new Action(Action.Type.PLAY, Card.BANANA));
        add(new Action(Action.Type.PLAY, Card.BROCCOLI_V2));
        add(new Action(Action.Type.PLAY, Card.AVOCADO));
    }};

    @Override
    List<Action> playOrder(final Game game) {
        return playOrder;
    }
}

class Action {

    private final Type type;
    private final Card card;

    Action(Type type, Card card) {
        this.type = type;
        this.card = card;
    }

    Type getType() {
        return type;
    }

    Card getCard() {
        return card;
    }

    @Override
    public String toString() {
        return type.name() + " - " + card.name();
    }

    public enum Type {
        HARVEST,
        PLAY
    }
}

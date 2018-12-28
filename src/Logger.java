import java.util.List;

interface Logger {

    void gardenRefreshed(List<Card> garden);

    void startTurn(Player player);

    void cardPlanted(Card card);

    void actionChosen(Action action);

    void endTurn(Player player);

    void gameOver(Game game);
}

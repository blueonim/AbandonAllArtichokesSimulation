import java.util.Collections;

public enum Card {

    ARTICHOKE(CompostAction.REMOVE) {
        @Override
        boolean canBePlayed(final Game game) {
            return false;
        }

        @Override
        void playCard(final Game game) {
            // no-op
        }
    },
    POTATO(CompostAction.ADD_TO_BOTTOM) {
        @Override
        boolean canBePlayed(final Game game) {
            return game.getCurrentPlayer().deckOrDiscardHasCards();
        }

        @Override
        void playCard(final Game game) {
            game.compostCard(game.getCurrentPlayer().drawTop(), game.getCurrentPlayer());
        }
    },
    CARROT(CompostAction.ADD_TO_BOTTOM) {
        @Override
        boolean canBePlayed(final Game game) {
            return !game.getGarden().isEmpty();
        }

        @Override
        void playCard(final Game game) {
            game.getCurrentPlayer().addToDiscard(game.getGarden().removeLast());
        }
    },
    BROCCOLI(CompostAction.ADD_TO_BOTTOM) {
        @Override
        boolean canBePlayed(final Game game) {
            // has at least 2 cards (including the Broccoli)
            if (game.getCurrentPlayer().getHand().size() < 2) return false;

            return game.getCurrentPlayer().deckOrDiscardHasCards();
        }

        @Override
        void playCard(final Game game) {
            Card card = game.getCurrentPlayer().drawTop();
            game.getCurrentPlayer().putCardOnTopOfDeck(game);
            game.getCurrentPlayer().getHand().add(card);
        }
    },
    ONION(CompostAction.ADD_TO_BOTTOM) {
        @Override
        boolean canBePlayed(final Game game) {
            // has at least 2 non-Artichokes (including the Onion)
            long count = game.getCurrentPlayer().getHand().stream().filter(card -> card != ARTICHOKE).count();
            if (count < 2) return false;

            return game.hasOpponentWithHand();
        }

        @Override
        void playCard(final Game game) {
            // choose opponent, compost random card
            Player opponent = game.getCurrentPlayer().chooseOpponent(game.getOpponents());
            Collections.shuffle(opponent.getHand());
            game.compostCard(opponent.getHand().removeFirst(), opponent);

            // compost random card from hand
            Collections.shuffle(game.getCurrentPlayer().getHand());
            game.compostCard(game.getCurrentPlayer().getHand().removeFirst(), game.getCurrentPlayer());
        }
    },
    BANANA(CompostAction.ADD_TO_BOTTOM) {
        @Override
        boolean canBePlayed(final Game game) {
            return game.getCurrentPlayer().deckHasCards();
        }

        @Override
        void playCard(final Game game) {
            while (game.getCurrentPlayer().deckHasCards()) {
                Card card = game.getCurrentPlayer().drawTop();
                if (card == ARTICHOKE) {
                    game.getCurrentPlayer().getHand().add(card);
                    break;
                }
                game.getCurrentPlayer().addToDiscard(card);
            }
        }
    },
    AVOCADO(CompostAction.DISCARD) {
        @Override
        boolean canBePlayed(final Game game) {
            return game.getCurrentPlayer().deckOrDiscardHasCards();
        }

        @Override
        void playCard(final Game game) {
            game.getCurrentPlayer().shuffleDeckAndDiscard();
        }
    },
    RADISH(CompostAction.DISCARD) {
        @Override
        boolean canBePlayed(final Game game) {
            // has at least 1 Artichoke to compost
            long artichokeCount = game.getCurrentPlayer().getHand().stream().filter(card -> card == ARTICHOKE).count();
            if (artichokeCount < 1) return false;

            return game.getCurrentPlayer().doesDiscardContain(Card.RADISH);
        }

        @Override
        void playCard(final Game game) {
            for (Card card : game.getCurrentPlayer().getHand()) {
                if (card == ARTICHOKE) {
                    game.getCurrentPlayer().getHand().remove(card);
                    game.compostCard(card, game.getCurrentPlayer());
                    break;
                }
            }
        }
    },
    LEMON(CompostAction.ADD_TO_BOTTOM) {
        @Override
        boolean canBePlayed(final Game game) {
            return game.hasOpponentWithDeckOrDiscard();
        }

        @Override
        void playCard(final Game game) {
            Player opponent = game.getCurrentPlayer().chooseOpponent(game.getOpponents());
            Card card = opponent.drawTop();

            if (game.getCurrentPlayer().doesWantCard(card)) {
                game.getCurrentPlayer().getHand().add(card);
            } else {
                game.compostCard(card, opponent);
            }
        }
    },
    PEAR(CompostAction.ADD_TO_BOTTOM) {
        @Override
        boolean canBePlayed(final Game game) {
            // has at least 2 non-Artichokes (including the Pear)
            long nonCount = game.getCurrentPlayer().getHand().stream().filter(card -> card != ARTICHOKE).count();
            if (nonCount < 2) return false;

            // has at least 1 Artichoke to compost
            long artichokeCount = game.getCurrentPlayer().getHand().stream().filter(card -> card == ARTICHOKE).count();
            return artichokeCount > 0;
        }

        @Override
        void playCard(final Game game) {
            game.getCurrentPlayer().discardNonArtichoke(game);
            for (Card card : game.getCurrentPlayer().getHand()) {
                if (card == ARTICHOKE) {
                    game.getCurrentPlayer().getHand().remove(card);
                    game.compostCard(card, game.getCurrentPlayer());
                    break;
                }
            }
        }
    };

    private final CompostAction compostAction;

    Card(CompostAction compostAction) {
        this.compostAction = compostAction;
    }

    public CompostAction getCompostAction() {
        return compostAction;
    }

    abstract boolean canBePlayed(final Game game);

    abstract void playCard(final Game game);
}

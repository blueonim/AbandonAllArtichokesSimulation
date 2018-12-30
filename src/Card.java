import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public enum Card {

    ARTICHOKE(HarvestAction.ADD_TO_HAND, CompostAction.REMOVE) {
        @Override
        boolean canBePlayed(final Game game) {
            return false;
        }

        @Override
        void playCard(final Game game) {
            // no-op
        }
    },
    POTATO(HarvestAction.ADD_TO_HAND, CompostAction.ADD_TO_BOTTOM) {
        @Override
        boolean canBePlayed(final Game game) {
            return game.getCurrentPlayer().deckOrDiscardHasCards();
        }

        @Override
        void playCard(final Game game) {
            game.compostCard(game.getCurrentPlayer().drawTop(), game.getCurrentPlayer());
        }
    },
    CARROT(HarvestAction.ADD_TO_HAND, CompostAction.ADD_TO_BOTTOM) {
        @Override
        boolean canBePlayed(final Game game) {
            return !game.getGarden().isEmpty();
        }

        @Override
        void playCard(final Game game) {
            game.getCurrentPlayer().addToDiscard(game.getGarden().removeLast());
        }
    },
    BROCCOLI_V1(HarvestAction.ADD_TO_HAND, CompostAction.ADD_TO_BOTTOM) {
        @Override
        boolean canBePlayed(final Game game) {
            // has at least 2 cards (including the Broccoli)
            if (game.getCurrentPlayer().getHand().size() < 2) return false;

            return game.getCurrentPlayer().deckOrDiscardHasCards();
        }

        @Override
        void playCard(final Game game) {
            Card card = game.getCurrentPlayer().drawTop();
            game.getCurrentPlayer().pickCardForTopOfDeck();
            game.getCurrentPlayer().getHand().add(card);
        }
    },
    BROCCOLI_V2(HarvestAction.ADD_TO_DISCARD, CompostAction.ADD_TO_BOTTOM) {
        @Override
        boolean canBePlayed(Game game) {
            return game.getCurrentPlayer().getHand().stream().filter(card -> card == ARTICHOKE).count() > 2;
        }

        @Override
        void playCard(Game game) {
            // compost 2 Artichokes from hand
            if (!game.getCurrentPlayer().getHand().remove(ARTICHOKE)) {
                throw new IllegalStateException("No Artichoke in hand to compost");
            }
            game.compostCard(ARTICHOKE, game.getCurrentPlayer());
        }
    },
    ONION(HarvestAction.ADD_TO_HAND, CompostAction.ADD_TO_BOTTOM) {
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
            List<Player> opponentsWithHand = game.getOpponents().stream()
                    .filter(player -> !player.getHand().isEmpty())
                    .collect(Collectors.toList());
            Player opponent = game.getCurrentPlayer().chooseOpponent(opponentsWithHand);
            Collections.shuffle(opponent.getHand());
            game.compostCard(opponent.getHand().removeFirst(), opponent);
            //TODO add illegal state checks

            // compost random card from hand
            Collections.shuffle(game.getCurrentPlayer().getHand());
            game.compostCard(game.getCurrentPlayer().getHand().removeFirst(), game.getCurrentPlayer());
        }
    },
    BANANA(HarvestAction.ADD_TO_HAND, CompostAction.ADD_TO_BOTTOM) {
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
    OLD_BANANA(HarvestAction.ADD_TO_HAND, CompostAction.ADD_TO_BOTTOM) {
        @Override
        boolean canBePlayed(Game game) {
            // has at least 2 cards (including the Broccoli)
            if (game.getCurrentPlayer().getHand().size() < 2) return false;

            return game.hasOpponentWithHand();
        }

        @Override
        void playCard(Game game) {
            // choose card to give from hand
            Card cardToGive = game.getCurrentPlayer().giveCardToOpponent();

            // choose opponent, take random card
            List<Player> opponentsWithHand = game.getOpponents().stream()
                    .filter(player -> !player.getHand().isEmpty())
                    .collect(Collectors.toList());
            Player opponent = game.getCurrentPlayer().chooseOpponent(opponentsWithHand);
            Collections.shuffle(opponent.getHand());
            Card stolenCard = opponent.getHand().removeFirst();
            //TODO add illegal state checks

            // give chosen card to opponent and add stolen card to hand
            opponent.getHand().add(cardToGive);
            game.getCurrentPlayer().getHand().add(stolenCard);
        }
    },
    AVOCADO(HarvestAction.ADD_TO_HAND, CompostAction.DISCARD) {
        @Override
        boolean canBePlayed(final Game game) {
            return game.getCurrentPlayer().deckOrDiscardHasCards();
        }

        @Override
        void playCard(final Game game) {
            game.getCurrentPlayer().shuffleDeckAndDiscard();
        }
    },
    RADISH(HarvestAction.ADD_TO_HAND, CompostAction.DISCARD) {
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
    LEMON(HarvestAction.ADD_TO_HAND, CompostAction.ADD_TO_BOTTOM) {
        @Override
        boolean canBePlayed(final Game game) {
            return game.hasOpponentWithDeckOrDiscard();
        }

        @Override
        void playCard(final Game game) {
            List<Player> opponentsWithDeckOrDiscard = game.getOpponents().stream()
                    .filter(Player::deckOrDiscardHasCards)
                    .collect(Collectors.toList());
            Player opponent = game.getCurrentPlayer().chooseOpponent(opponentsWithDeckOrDiscard);
            Card card = opponent.drawTop();
            //TODO add illegal state checks

            if (game.getCurrentPlayer().doesWantCard(card)) {
                game.getCurrentPlayer().getHand().add(card);
            } else {
                game.compostCard(card, opponent);
            }
        }
    },
    PEAR_V1(HarvestAction.ADD_TO_HAND, CompostAction.ADD_TO_BOTTOM) {
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
            game.getCurrentPlayer().discardNonArtichoke();
            for (Card card : game.getCurrentPlayer().getHand()) {
                if (card == ARTICHOKE) {
                    game.getCurrentPlayer().getHand().remove(card);
                    game.compostCard(card, game.getCurrentPlayer());
                    break;
                }
            }
        }
    },
    POTATO_V2(HarvestAction.ADD_TO_HAND, CompostAction.ADD_TO_BOTTOM) {
        @Override
        boolean canBePlayed(Game game) {
            return true;
        }

        @Override
        void playCard(Game game) {
            // check top of current player's deck
            Card currentPlayerCard = game.getCurrentPlayer().drawTop();
            if (currentPlayerCard != null) {
                if (currentPlayerCard == ARTICHOKE) {
                    game.compostCard(currentPlayerCard, game.getCurrentPlayer());
                } else {
                    game.getCurrentPlayer().addToTopOfDeck(currentPlayerCard);
                }
            }

            // check top of all opponent's decks
            for (Player player : game.getOpponents()) {
                Card playerCard = player.drawTop();
                if (playerCard != null) {
                    if (playerCard == ARTICHOKE) {
                        game.compostCard(playerCard, player);
                    } else {
                        player.addToTopOfDeck(playerCard);
                    }
                }
            }
        }
    },
    TRADE(HarvestAction.ADD_TO_HAND, CompostAction.ADD_TO_BOTTOM) {
        @Override
        boolean canBePlayed(Game game) {
            // has at least 2 non-Artichokes (including the this card)
            long nonCount = game.getCurrentPlayer().getHand().stream().filter(card -> card != ARTICHOKE).count();
            if (nonCount < 2) return false;

            return game.hasOpponentWithHand();
        }

        @Override
        void playCard(Game game) {
            // choose opponent, they choose non-Artichoke card if it exists
            List<Player> opponentsWithHand = game.getOpponents().stream()
                    .filter(player -> !player.getHand().isEmpty())
                    .collect(Collectors.toList());
            Player opponent = game.getCurrentPlayer().chooseOpponent(opponentsWithHand);

            // check if opponent has non-Artichokes
            if (opponent.getHand().stream().anyMatch(card -> card != ARTICHOKE)) {
                // each player chooses non-Artichoke to trade
                Card playerCard = game.getCurrentPlayer().giveNonArtichokeToOpponent();
                Card opponentCard = opponent.giveNonArtichokeToOpponent();

                // trade cards
                game.getCurrentPlayer().getHand().add(opponentCard);
                opponent.getHand().add(playerCard);

            } else if (!game.getGarden().isEmpty()) {
                // swap with top of garden deck
                Card playerCard = game.getCurrentPlayer().giveNonArtichokeToOpponent();
                Card gardenCard = game.drawTop();
                game.getCurrentPlayer().getHand().add(gardenCard);
                game.addToTopOfDeck(playerCard);
            }

            // otherwise nothing happens
        }
    };

    private final HarvestAction harvestAction;
    private final CompostAction compostAction;

    Card(HarvestAction harvestAction, CompostAction compostAction) {
        this.harvestAction = harvestAction;
        this.compostAction = compostAction;
    }

    public HarvestAction getHarvestAction() {
        return harvestAction;
    }

    public CompostAction getCompostAction() {
        return compostAction;
    }

    abstract boolean canBePlayed(final Game game);

    abstract void playCard(final Game game);
}

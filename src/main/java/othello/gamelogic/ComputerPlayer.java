package othello.gamelogic;

import othello.gamelogic.strategy.*;

public class ComputerPlayer extends Player {
    private final Strategy strategy;

    /**
     * Constructs a computer player using a specific strategy name.
     * @param strategyName the name of the strategy, like "minimax", "mcts", etc.
     */
    public ComputerPlayer(String strategyName) {
        this.strategy = StrategyFactory.create(strategyName.toLowerCase());
    }

    /**
     * Chooses a move by delegating to the selected strategy.
     * @param game the current game state
     * @return the chosen BoardSpace to play
     */
    public BoardSpace chooseMove(OthelloGame game) {
        return strategy.chooseMove(game, this);
    }

}

package othello.gamelogic.strategy;

import othello.gamelogic.BoardSpace;
import othello.gamelogic.OthelloGame;
import othello.gamelogic.Player;

/**
 * Strategy interface for AI players.
 * Each implementation defines a different move selection algorithm.
 */
public interface Strategy {
    /**
     * Returns the best move based on the current game state.
     *
     * @param game the current Othello game
     * @return the chosen BoardSpace to place a disc
     */

    BoardSpace chooseMove(OthelloGame game, Player me);
}

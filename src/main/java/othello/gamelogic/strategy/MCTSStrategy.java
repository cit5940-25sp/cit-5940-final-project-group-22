package othello.gamelogic.strategy;

import othello.gamelogic.BoardSpace;
import othello.gamelogic.OthelloGame;
import othello.gamelogic.Player;

/**
 * AI strategy using Monte Carlo Tree Search (MCTS).
 * Performs randomized simulations to find the most promising move.
 */
public class MCTSStrategy implements Strategy {
    @Override
    public BoardSpace chooseMove(OthelloGame game, Player me) {
        // TODO: Implement MCTS: Selection, Expansion, Simulation, and Backpropagation
        return null;
    }
}

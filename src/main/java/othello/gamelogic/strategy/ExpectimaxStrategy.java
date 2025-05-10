package othello.gamelogic.strategy;

import othello.gamelogic.BoardSpace;
import othello.gamelogic.OthelloGame;
import othello.gamelogic.Player;

/**
 * AI strategy using the Expectimax algorithm.
 * Models the opponent's behavior probabilistically rather than assuming optimal play.
 */
public class ExpectimaxStrategy implements Strategy {
    @Override
    public BoardSpace chooseMove(OthelloGame game, Player me) {
        // TODO: Implement Expectimax algorithm to choose the move with highest expected value
        return null;
    }
}

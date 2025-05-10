package othello.gamelogic.strategy;

import othello.gamelogic.BoardSpace;
import othello.gamelogic.OthelloGame;
import othello.gamelogic.Player;

import java.util.List;
import java.util.Map;

/**
 * Custom AI strategy implementation.
 * You can define your own approach based on heuristics, statistics, or mixed algorithms.
 */
public class CustomStrategy implements Strategy {
    @Override
    public BoardSpace chooseMove(OthelloGame game, Player me) {
        Map<BoardSpace, List<BoardSpace>> moves = game.getAvailableMoves(me);
        return moves.isEmpty()
                ? null
                : moves.keySet().iterator().next();
    }
}


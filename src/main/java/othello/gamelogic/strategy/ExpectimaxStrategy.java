package othello.gamelogic.strategy;

import java.util.*;
import othello.Constants;
import othello.gamelogic.BoardSpace;
import othello.gamelogic.OthelloGame;
import othello.gamelogic.Player;
import othello.gamelogic.HumanPlayer;

/**
 * AI strategy using the Expectimax algorithm.
 * Models the opponent's behavior probabilistically rather than assuming optimal play.
 */
public class ExpectimaxStrategy implements Strategy {
    private static final int MAX_DEPTH = 2;

    @Override
    public BoardSpace chooseMove(OthelloGame game, Player me) {
        // Identify opponent by color
        Player opponent = (game.getPlayerOne() == me)
                ? game.getPlayerTwo()
                : game.getPlayerOne();

        // Get legal moves for the maximizing player
        Map<BoardSpace, List<BoardSpace>> moves = game.getAvailableMoves(me);
        if (moves == null || moves.isEmpty()) {
            return null;
        }

        BoardSpace bestMove = null;
        double bestValue = Double.NEGATIVE_INFINITY;

        // Evaluate each possible move by its expected value
        for (BoardSpace dest : moves.keySet()) {
            // Clone game and apply move
            OthelloGame copy = cloneGameFully(game);
            Player meCopy = (copy.getPlayerOne().getColor() == me.getColor())
                    ? copy.getPlayerOne() : copy.getPlayerTwo();
            Player opCopy = (meCopy == copy.getPlayerOne())
                    ? copy.getPlayerTwo() : copy.getPlayerOne();
            copy.takeSpaces(meCopy, opCopy, copy.getAvailableMoves(meCopy), dest);

            // Compute expectimax value: false indicates next is chance node (opponent)
            double value = expectimax(copy, meCopy, opCopy, MAX_DEPTH - 1, false);
            if (value > bestValue) {
                bestValue = value;
                bestMove = dest;
            }
        }
        return bestMove;
    }

    /**
     * Recursive expectimax evaluation.
     * @param game current game state
     * @param me maximizing player
     * @param opponent chance node player
     * @param depth remaining depth
     * @param isMaximizing true if this is a max node, false for chance node
     * @return expected utility value
     */
    private double expectimax(OthelloGame game,
                              Player me,
                              Player opponent,
                              int depth,
                              boolean isMaximizing) {
        Player current = isMaximizing ? me : opponent;
        Player other   = isMaximizing ? opponent : me;

        Map<BoardSpace, List<BoardSpace>> moves = game.getAvailableMoves(current);
        // Depth limit: evaluate statically
        if (depth == 0) {
            return evaluateBoard(game, me, opponent);
        }

        // No moves: skip turn without decreasing depth
        if (moves.isEmpty()) {
            Map<BoardSpace, List<BoardSpace>> nextMoves = game.getAvailableMoves(other);
            if (nextMoves.isEmpty()) {
                // Game over
                return evaluateBoard(game, me, opponent);
            }
            return expectimax(game, me, opponent, depth, !isMaximizing);
        }

        if (isMaximizing) {
            double best = Double.NEGATIVE_INFINITY;
            for (BoardSpace dest : moves.keySet()) {
                OthelloGame copy = cloneGameFully(game);
                Player curCopy = (copy.getPlayerOne().getColor() == current.getColor())
                        ? copy.getPlayerOne() : copy.getPlayerTwo();
                Player othCopy = (curCopy == copy.getPlayerOne())
                        ? copy.getPlayerTwo() : copy.getPlayerOne();
                copy.takeSpaces(curCopy, othCopy, copy.getAvailableMoves(curCopy), dest);
                best = Math.max(best, expectimax(copy, me, opponent, depth - 1, false));
            }
            return best;
        } else {
            // Chance node: average over all possible opponent moves
            double sum = 0;
            int count = 0;
            for (BoardSpace dest : moves.keySet()) {
                OthelloGame copy = cloneGameFully(game);
                Player curCopy = (copy.getPlayerOne().getColor() == current.getColor())
                        ? copy.getPlayerOne() : copy.getPlayerTwo();
                Player othCopy = (curCopy == copy.getPlayerOne())
                        ? copy.getPlayerTwo() : copy.getPlayerOne();
                copy.takeSpaces(curCopy, othCopy, copy.getAvailableMoves(curCopy), dest);
                sum += expectimax(copy, me, opponent, depth - 1, true);
                count++;
            }
            return sum / count;
        }
    }

    /**
     * Static board evaluation: sum of weights for me minus opponent.
     */
    private int evaluateBoard(OthelloGame game, Player me, Player opponent) {
        int score = 0;
        BoardSpace[][] board = game.getBoard();
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                BoardSpace.SpaceType type = board[x][y].getType();
                int w = Constants.BOARD_WEIGHTS[x][y];
                if (type == me.getColor())      score += w;
                else if (type == opponent.getColor()) score -= w;
            }
        }
        return score;
    }

    /**
     * Deep clone of the game state: clones board and reconstructs owned spaces.
     * Uses two generic HumanPlayer clones so that takeSpaces works correctly.
     */
    private OthelloGame cloneGameFully(OthelloGame original) {
        Player p1 = new HumanPlayer();
        Player p2 = new HumanPlayer();
        p1.setColor(original.getPlayerOne().getColor());
        p2.setColor(original.getPlayerTwo().getColor());

        OthelloGame copy = new OthelloGame(p1, p2);
        BoardSpace[][] orig = original.getBoard();
        BoardSpace[][] cloned = new BoardSpace[orig.length][orig[0].length];
        for (int i = 0; i < orig.length; i++) {
            for (int j = 0; j < orig[i].length; j++) {
                cloned[i][j] = new BoardSpace(orig[i][j]);
            }
        }
        copy.setBoard(cloned);

        // Rebuild owned spaces for each player
        for (BoardSpace[] row : cloned) {
            for (BoardSpace bs : row) {
                if (bs.getType() == p1.getColor()) {
                    p1.getPlayerOwnedSpacesSpaces().add(bs);
                } else if (bs.getType() == p2.getColor()) {
                    p2.getPlayerOwnedSpacesSpaces().add(bs);
                }
            }
        }
        return copy;
    }
}

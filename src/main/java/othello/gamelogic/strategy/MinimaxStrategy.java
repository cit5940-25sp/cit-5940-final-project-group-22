package othello.gamelogic.strategy;

import othello.Constants;
import othello.gamelogic.*;
import java.util.List;
import java.util.Map;

/**
 * Strategy that uses Minimax with a fixed depth and positional weights.
 */
public class MinimaxStrategy implements Strategy {
    private static final int MAX_DEPTH = 2;

    @Override
    public BoardSpace chooseMove(OthelloGame game, Player me) {
        Player opponent = (game.getPlayerOne() == me)
                ? game.getPlayerTwo()
                : game.getPlayerOne();

        Map<BoardSpace, List<BoardSpace>> moves = game.getAvailableMoves(me);
        if (moves == null || moves.isEmpty()) {
            return null;
        }

        BoardSpace bestMove = null;
        int bestScore = Integer.MIN_VALUE;
        for (BoardSpace dest : moves.keySet()) {
            // clone the game fully (board + ownedSpaces) using generic HumanPlayer
            OthelloGame copy = cloneGameFully(game);
            // determine copies corresponding to me/opponent by color
            Player meCopy  = copy.getPlayerOne().getColor()  == me.getColor()
                    ? copy.getPlayerOne() : copy.getPlayerTwo();
            Player opCopy  = meCopy == copy.getPlayerOne()
                    ? copy.getPlayerTwo() : copy.getPlayerOne();

            copy.takeSpaces(meCopy, opCopy, copy.getAvailableMoves(meCopy), dest);
            int score = minimax(copy, meCopy, opCopy, MAX_DEPTH - 1, false);
            if (score > bestScore) {
                bestScore = score;
                bestMove  = dest;
            }
        }
        return bestMove;
    }

    private int minimax(OthelloGame game, Player me, Player opponent,
                        int depth, boolean maximizing) {
        Player current = maximizing ? me : opponent;
        Player other   = maximizing ? opponent : me;

        Map<BoardSpace, List<BoardSpace>> moves = game.getAvailableMoves(current);
        //  Depth 0 → evaluate statically
        if (depth == 0) {
            return evaluateBoard(game, me, opponent);
        }

        if (moves.isEmpty()) {
        //  No moves and skip turn, do not decrement depth
        Map<BoardSpace, List<BoardSpace>> oppMoves = game.getAvailableMoves(other);
        // If the opponent also has no moves, the game is over—evaluate final board
        if (oppMoves.isEmpty()) {
            return evaluateBoard(game, me, opponent);
        }
        // Otherwise skip this player’s turn (do not decrease depth)
        return minimax(game, me, opponent, depth, !maximizing);
    }

        int best = maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (BoardSpace dest : moves.keySet()) {
            OthelloGame copy = cloneGameFully(game);
            Player curCopy = copy.getPlayerOne().getColor() == current.getColor()
                    ? copy.getPlayerOne() : copy.getPlayerTwo();
            Player othCopy = curCopy == copy.getPlayerOne()
                    ? copy.getPlayerTwo() : copy.getPlayerOne();

            copy.takeSpaces(curCopy, othCopy, copy.getAvailableMoves(curCopy), dest);
            int val = minimax(copy, me, opponent, depth - 1, !maximizing);
            best = maximizing ? Math.max(best, val) : Math.min(best, val);
        }
        return best;
    }

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
     * Deep clone of the game state: copies board and reconstructs ownedSpaces
     * using simple HumanPlayer clones so that takeSpaces works correctly.
     */
    private OthelloGame cloneGameFully(OthelloGame original) {
        // clone two generic players with same colors
        Player p1 = new HumanPlayer();
        Player p2 = new HumanPlayer();
        p1.setColor(original.getPlayerOne().getColor());
        p2.setColor(original.getPlayerTwo().getColor());

        // construct new game and inject cloned board
        OthelloGame copy = new OthelloGame(p1, p2);
        BoardSpace[][] orig = original.getBoard();
        BoardSpace[][] cloned = new BoardSpace[orig.length][orig[0].length];

        for (int i = 0; i < orig.length; i++) {
            for (int j = 0; j < orig[i].length; j++) {
                cloned[i][j] = new BoardSpace(orig[i][j]);
            }
        }
        copy.setBoard(cloned);

        // rebuild ownedSpaces lists based on cloned board
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

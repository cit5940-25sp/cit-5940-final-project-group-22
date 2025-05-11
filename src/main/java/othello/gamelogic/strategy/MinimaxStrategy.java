package othello.gamelogic.strategy;

import othello.gamelogic.BoardSpace;
import othello.gamelogic.HumanPlayer;
import othello.gamelogic.OthelloGame;
import othello.gamelogic.Player;

import java.util.*;

/**
 * Custom strategy: one-level opponent minimization response + heuristic evaluation
 */
public class CustomStrategy implements Strategy {
    // Positional weight table
    private static final int[][] WEIGHTS = {
            { 100, -20,  10,   5,   5,  10, -20, 100},
            { -20, -50,  -2,  -2,  -2,  -2, -50, -20},
            {  10,  -2,   2,   2,   2,   2,  -2,  10},
            {   5,  -2,   2,   0,   0,   2,  -2,   5},
            {   5,  -2,   2,   0,   0,   2,  -2,   5},
            {  10,  -2,   2,   2,   2,   2,  -2,  10},
            { -20, -50,  -2,  -2,  -2,  -2, -50, -20},
            { 100, -20,  10,   5,   5,  10, -20, 100}
    };

    @Override
    public BoardSpace chooseMove(OthelloGame game, Player me) {
        Map<BoardSpace, List<BoardSpace>> moves = game.getAvailableMoves(me);
        if (moves.isEmpty()) return null;

        // Identify opponent
        Player opp = (game.getPlayerOne() == me)
                ? game.getPlayerTwo() : game.getPlayerOne();

        BoardSpace bestMove = null;
        double     bestScore = Double.NEGATIVE_INFINITY;

        // Evaluate each candidate move with one opponent response
        for (BoardSpace move : moves.keySet()) {
            // 1) Clone the current game and apply the move
            OthelloGame sim1 = cloneGame(game);
            Player simMe  = sim1.getPlayerOne().getColor() == me.getColor()
                    ? sim1.getPlayerOne() : sim1.getPlayerTwo();
            Player simOpp = (simMe == sim1.getPlayerOne())
                    ? sim1.getPlayerTwo() : sim1.getPlayerOne();
            sim1.takeSpaces(simMe, simOpp,
                    sim1.getAvailableMoves(simMe),
                    move);

            // 2) For each opponent response, compute the worst (minimum) score
            Map<BoardSpace, List<BoardSpace>> oppMoves =
                    sim1.getAvailableMoves(simOpp);
            double worst = Double.POSITIVE_INFINITY;
            if (oppMoves.isEmpty()) {
                // No moves for opponent, evaluate directly
                worst = evaluate(sim1, simMe, simOpp);
            } else {
                for (BoardSpace r : oppMoves.keySet()) {
                    OthelloGame sim2 = cloneGame(sim1);
                    Player m2 = sim2.getPlayerOne().getColor() == me.getColor()
                            ? sim2.getPlayerOne() : sim2.getPlayerTwo();
                    Player o2 = (m2 == sim2.getPlayerOne())
                            ? sim2.getPlayerTwo() : sim2.getPlayerOne();
                    sim2.takeSpaces(o2, m2,
                            sim2.getAvailableMoves(o2),
                            r);
                    double score = evaluate(sim2, m2, o2);
                    worst = Math.min(worst, score);
                }
            }

            // 3) Choose move with the highest worst-case score
            if (worst > bestScore) {
                bestScore = worst;
                bestMove  = move;
            }
        }

        // Return the BoardSpace from the original game board
        return game.getBoard()[bestMove.getX()][bestMove.getY()];
    }

    /**
     * Heuristic evaluation function: positional weight + disc difference
     * + mobility + frontier disc penalty
     */
    private double evaluate(OthelloGame g, Player me, Player opp) {
        BoardSpace[][] b = g.getBoard();
        double score = 0;

        // Positional weights
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (b[i][j].getType() == me.getColor())      score += WEIGHTS[i][j];
                else if (b[i][j].getType() == opp.getColor()) score -= WEIGHTS[i][j];
            }
        }

        // Disc count difference
        score += 2 * (me.getPlayerOwnedSpacesSpaces().size()
                - opp.getPlayerOwnedSpacesSpaces().size());

        // Mobility difference
        score += 5 * (g.getAvailableMoves(me).size()
                - g.getAvailableMoves(opp).size());

        // Frontier disc penalty
        score -= 3 * (frontierCount(g, me) - frontierCount(g, opp));

        return score;
    }

    /**
     * Count frontier discs: discs adjacent to empty squares
     */
    private int frontierCount(OthelloGame g, Player p) {
        int cnt = 0;
        BoardSpace[][] b = g.getBoard();
        for (BoardSpace bs : p.getPlayerOwnedSpacesSpaces()) {
            int x = bs.getX(), y = bs.getY();
            boolean isFront = false;
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    int nx = x + dx, ny = y + dy;
                    if (nx >= 0 && ny >= 0 && nx < 8 && ny < 8
                            && b[nx][ny].getType() == BoardSpace.SpaceType.EMPTY) {
                        isFront = true;
                    }
                }
            }
            if (isFront) cnt++;
        }
        return cnt;
    }

    /**
     * Clone the entire game state, including board and ownedSpaces lists
     */
    private OthelloGame cloneGame(OthelloGame orig) {
        // 1. Create new players
        Player p1 = new HumanPlayer();
        Player p2 = new HumanPlayer();
        p1.setColor(orig.getPlayerOne().getColor());
        p2.setColor(orig.getPlayerTwo().getColor());

        // 2. Initialize a new game and copy the board
        OthelloGame copy = new OthelloGame(p1, p2);
        BoardSpace[][] ob = orig.getBoard();
        BoardSpace[][] nb = new BoardSpace[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                nb[i][j] = new BoardSpace(ob[i][j]);
            }
        }
        copy.setBoard(nb);

        // 3. Rebuild ownedSpaces lists
        for (BoardSpace[] row : nb) {
            for (BoardSpace bs : row) {
                if (bs.getType() == p1.getColor())      p1.getPlayerOwnedSpacesSpaces().add(bs);
                else if (bs.getType() == p2.getColor()) p2.getPlayerOwnedSpacesSpaces().add(bs);
            }
        }
        return copy;
    }
}

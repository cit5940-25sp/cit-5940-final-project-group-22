package othello.gamelogic.strategy;

import othello.gamelogic.BoardSpace;
import othello.gamelogic.OthelloGame;
import othello.gamelogic.Player;
import othello.gamelogic.HumanPlayer;

import java.util.*;

/**
 * CustomStrategy: a corner-priority + flat Monte Carlo rollout AI.
 *
 * 1) If any corner moves are available, pick one at random.
 * 2) Otherwise, for each legal move:
 *    – Perform a fixed number of random-playout simulations.
 *    – Estimate win rate from those simulations.
 *    – Choose the move with the highest estimated win rate.
 *
 * Complexity: O(M * R) playouts per move decision, where
 * M ≈ average branching factor and R = number of rollouts.
 */
public class CustomStrategy implements Strategy {
    // Number of random playouts per candidate move
    private static final int ROLLOUTS = 50;
    private static final Random RANDOM = new Random();

    @Override
    public BoardSpace chooseMove(OthelloGame game, Player me) {
        Map<BoardSpace, List<BoardSpace>> legalMoves = game.getAvailableMoves(me);
        if (legalMoves == null || legalMoves.isEmpty()) {
            return null;  // No legal move
        }

        // 1) Corner priority: check the four board corners first
        int size = game.getBoard().length;
        int[][] corners = {{0, 0}, {0, size-1}, {size-1, 0}, {size-1, size-1}};
        List<BoardSpace> cornerOptions = new ArrayList<>();
        for (int[] c : corners) {
            BoardSpace bs = game.getBoard()[c[0]][c[1]];
            if (legalMoves.containsKey(bs)) {
                cornerOptions.add(bs);
            }
        }
        if (!cornerOptions.isEmpty()) {
            return cornerOptions.get(RANDOM.nextInt(cornerOptions.size()));
        }

        // 2) Monte Carlo rollouts for non-corner moves
        double bestWinRate = -1.0;
        BoardSpace bestMove = null;
        for (BoardSpace move : legalMoves.keySet()) {
            int wins = 0;
            for (int i = 0; i < ROLLOUTS; i++) {
                if (simulatePlayout(game, me, move)) {
                    wins++;
                }
            }
            double winRate = (double) wins / ROLLOUTS;
            if (winRate > bestWinRate) {
                bestWinRate = winRate;
                bestMove = move;
            }
        }
        return bestMove;
    }

    /**
     * Run one random-playout from the given move and return true if 'me' wins.
     */
    private boolean simulatePlayout(OthelloGame original, Player me, BoardSpace move) {
        // 1) Clone the game state
        OthelloGame sim = cloneGame(original);
        Player simMe = matchPlayer(sim, me);
        Player simOpp = (simMe == sim.getPlayerOne()) ? sim.getPlayerTwo() : sim.getPlayerOne();

        // 2) Play the chosen move
        Map<BoardSpace, List<BoardSpace>> avail = sim.getAvailableMoves(simMe);
        sim.takeSpaces(simMe, simOpp, avail, move);

        // 3) Alternate random moves until game end
        Player current = simOpp;
        while (true) {
            Map<BoardSpace, List<BoardSpace>> moves = sim.getAvailableMoves(current);
            if (moves.isEmpty()) {
                // pass turn if no moves
                current = (current == simMe) ? simOpp : simMe;
                moves = sim.getAvailableMoves(current);
                if (moves.isEmpty()) break;  // both players pass → end
            }
            List<BoardSpace> choices = new ArrayList<>(moves.keySet());
            BoardSpace pick = choices.get(RANDOM.nextInt(choices.size()));
            Player other = (current == sim.getPlayerOne()) ? sim.getPlayerTwo() : sim.getPlayerOne();
            sim.takeSpaces(current, other, moves, pick);
            current = other;
        }

        // 4) Count final discs to decide winner
        BoardSpace.SpaceType myColor = simMe.getColor();
        BoardSpace.SpaceType opColor = simOpp.getColor();
        int myCount = 0, opCount = 0;
        for (BoardSpace[] row : sim.getBoard()) {
            for (BoardSpace bs : row) {
                if (bs.getType() == myColor) myCount++;
                else if (bs.getType() == opColor) opCount++;
            }
        }
        return myCount > opCount;
    }

    /**
     * Deep clone the game: copy board and recreate players.
     */
    private OthelloGame cloneGame(OthelloGame orig) {
        Player p1 = new HumanPlayer();
        Player p2 = new HumanPlayer();
        p1.setColor(orig.getPlayerOne().getColor());
        p2.setColor(orig.getPlayerTwo().getColor());
        OthelloGame copy = new OthelloGame(p1, p2);

        BoardSpace[][] board = orig.getBoard();
        BoardSpace[][] clone = new BoardSpace[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                clone[i][j] = new BoardSpace(board[i][j]);
            }
        }
        copy.setBoard(clone);
        return copy;
    }

    /**
     * Match the cloned player instance by color.
     */
    private Player matchPlayer(OthelloGame sim, Player orig) {
        return sim.getPlayerOne().getColor() == orig.getColor()
                ? sim.getPlayerOne()
                : sim.getPlayerTwo();
    }
}

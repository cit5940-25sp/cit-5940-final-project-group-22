package othello.gamelogic.strategy;

import othello.gamelogic.BoardSpace;
import othello.gamelogic.OthelloGame;
import othello.gamelogic.Player;
import othello.gamelogic.HumanPlayer;

import java.util.*;

/**
 * Monte Carlo Tree Search (MCTS) strategy for Othello.
 * Implements Selection, Expansion, Simulation, and Backpropagation.
 */
public class MCTSStrategy implements Strategy {
    private static final int ITERATIONS = 100;                     // number of MCTS iterations
    private static final double EXPLORATION_PARAM = Math.sqrt(2);  // UCT exploration constant

    @Override
    public BoardSpace chooseMove(OthelloGame game, Player me) {
        // 1. Identify opponent in the real game
        Player opp = (game.getPlayerOne() == me)
                ? game.getPlayerTwo()
                : game.getPlayerOne();

        // 2. Create a cloned root state for simulations
        OthelloGame rootState = cloneGame(game, me, opp);
        Player simMe  = findSimPlayer(rootState, me);
        Player simOpp = (simMe == rootState.getPlayerOne())
                ? rootState.getPlayerTwo()
                : rootState.getPlayerOne();

        // 3. Initialize the root of the MCTS tree (no move led here; it’s me to move)
        Node root = new Node(null, rootState, simMe, simOpp, simMe);

        // 4. MCTS main loop
        for (int i = 0; i < ITERATIONS; i++) {
            // -- Selection
            Node node = root;
            while (node.untriedMoves.isEmpty() && !node.children.isEmpty()) {
                node = selectUCT(node);
            }
            // -- Expansion
            if (!node.untriedMoves.isEmpty()) {
                // pick and remove a random untried move
                BoardSpace m = node.untriedMoves
                        .remove(new Random().nextInt(node.untriedMoves.size()));
                node = node.expand(m);
            }
            // -- Simulation
            boolean win = simulateRandomPlayout(node, simMe, simOpp);
            // -- Backpropagation
            while (node != null) {
                node.visits++;
                if (win) {
                    node.wins++;
                }
                node = node.parent;
            }
        }

        // 5. Select the child with the highest visit count
        Node bestChild = Collections.max(root.children,
                Comparator.comparing(c -> c.visits));
        BoardSpace bestMove = bestChild.move;

        // 6. Return the corresponding space from the original game board
        return game.getBoard()[bestMove.getX()][bestMove.getY()];
    }

    /**
     * Tree node for MCTS.
     */
    private class Node {
        Node parent;
        List<Node> children = new ArrayList<>();
        double wins = 0;
        int visits = 0;

        Map<BoardSpace, List<BoardSpace>> moveMap;  // legal moves at this node
        List<BoardSpace> untriedMoves;               // subset of moveMap.keySet()

        BoardSpace move;     // the move that led from parent→this
        OthelloGame state;   // cloned game state at this node
        Player toMove;       // which player moves next in this state
        Player simMe, simOpp;

        /**
         * Constructor for root (move==null).
         */
        Node(BoardSpace move,
             OthelloGame state,
             Player simMe,
             Player simOpp,
             Player toMove) {
            this.parent       = null;
            this.move         = move;
            this.state        = state;
            this.simMe        = simMe;
            this.simOpp       = simOpp;
            this.toMove       = toMove;
            this.moveMap      = state.getAvailableMoves(toMove);
            this.untriedMoves = new ArrayList<>(moveMap.keySet());
        }

        /**
         * Constructor for child nodes.
         */
        Node(Node parent,
             BoardSpace move,
             OthelloGame state,
             Player simMe,
             Player simOpp,
             Player toMove) {
            this(move, state, simMe, simOpp, toMove);
            this.parent = parent;
        }

        /**
         * Expand by applying the given move, creating a child node.
         */
        Node expand(BoardSpace move) {
            // 1) Clone this node’s state
            OthelloGame nextState = cloneGame(this.state, simMe, simOpp);
            // 2) Determine the opponent in the child state
            Player other = (toMove == nextState.getPlayerOne())
                    ? nextState.getPlayerTwo()
                    : nextState.getPlayerOne();
            // 3) Apply the move
            nextState.takeSpaces(toMove, other, this.moveMap, move);
            // 4) Child is now other’s turn
            Node child = new Node(this, move, nextState, simMe, simOpp, other);
            this.children.add(child);
            return child;
        }
    }

    /**
     * UCT selection: pick the child maximizing
     *    (wins/visits) + c * sqrt( ln(parent.visits) / visits )
     */
    private Node selectUCT(Node node) {
        double logParentVisits = Math.log(node.visits);
        return Collections.max(node.children, Comparator.comparing(c ->
                (c.wins / c.visits)
                        + EXPLORATION_PARAM * Math.sqrt(logParentVisits / c.visits)
        ));
    }

    /**
     * Simulate a random playout from the given node.
     * Returns true if simMe wins.
     */
    private boolean simulateRandomPlayout(Node node, Player simMe, Player simOpp) {
        // 1) Deep‐clone the state to avoid polluting the tree
        OthelloGame sim = cloneGame(node.state, simMe, simOpp);
        Player current = node.toMove;

        // 2) Play random moves until neither can move
        while (true) {
            Map<BoardSpace, List<BoardSpace>> avail = sim.getAvailableMoves(current);
            if (avail.isEmpty()) {
                // skip turn
                current = (current == simMe) ? simOpp : simMe;
                avail = sim.getAvailableMoves(current);
                if (avail.isEmpty()) break;  // game over
            }
            // pick a random legal move
            List<BoardSpace> choices = new ArrayList<>(avail.keySet());
            BoardSpace move = choices.get(new Random().nextInt(choices.size()));
            Player other = (current == sim.getPlayerOne())
                    ? sim.getPlayerTwo()
                    : sim.getPlayerOne();
            sim.takeSpaces(current, other, avail, move);
            current = other;
        }

        // 3) Count final discs on the board
        int meCount = 0, oppCount = 0;
        BoardSpace.SpaceType meColor  = simMe.getColor();
        BoardSpace.SpaceType oppColor = simOpp.getColor();
        for (BoardSpace[] row : sim.getBoard()) {
            for (BoardSpace s : row) {
                if (s.getType() == meColor)  meCount++;
                else if (s.getType() == oppColor) oppCount++;
            }
        }
        return meCount > oppCount;
    }

    /**
     * Deep‐copies a game state: clones the board and sets up two new players
     * with the same colors. Does not preserve playerOwnedSpaces lists, but
     * simulation relies on board scans for results.
     */
    private OthelloGame cloneGame(OthelloGame orig, Player me, Player opp) {
        // clone the board array
        BoardSpace[][] origBoard = orig.getBoard();
        int size = origBoard.length;
        BoardSpace[][] copy = new BoardSpace[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                BoardSpace s = origBoard[i][j];
                copy[i][j] = new BoardSpace(i, j, s.getType());
            }
        }
        // create new player instances with same colors
        Player simMe  = new HumanPlayer();
        simMe.setColor(me.getColor());
        Player simOpp = new HumanPlayer();
        simOpp.setColor(opp.getColor());
        // build a fresh game and inject the cloned board
        OthelloGame simGame = new OthelloGame(simMe, simOpp);
        simGame.setBoard(copy);
        return simGame;
    }

    /**
     * Given an original Player from the real game, finds the matching
     * Player instance in the cloned game state.
     */
    private Player findSimPlayer(OthelloGame state, Player orig) {
        if (state.getPlayerOne().getColor() == orig.getColor()) {
            return state.getPlayerOne();
        } else {
            return state.getPlayerTwo();
        }
    }
}

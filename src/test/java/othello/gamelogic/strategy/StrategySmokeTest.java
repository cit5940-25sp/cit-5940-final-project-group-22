package othello.gamelogic.strategy;

import othello.gamelogic.ComputerPlayer;
import othello.gamelogic.OthelloGame;
import othello.gamelogic.BoardSpace;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Smoke tests for each AI Strategy.
 * Verifies that chooseMove() always returns one of the legal destinations.
 */
public class StrategySmokeTest {

    /**
     * Helper: instantiate a ComputerPlayer with given name,
     * start a fresh OthelloGame, and assert that chooseMove()
     * returns a key from getAvailableMoves().
     */
    private void assertLegal(String strategyName) {
        ComputerPlayer ai = new ComputerPlayer(strategyName);
        ComputerPlayer dummy = new ComputerPlayer("minimax"); // just to fill second slot
        // Black goes first
        OthelloGame game = new OthelloGame(ai, dummy);

        Map<BoardSpace, ?> moves = game.getAvailableMoves(ai);
        assertFalse(moves.isEmpty(),
                () -> "[" + strategyName + "] no legal moves on initial board");

        BoardSpace pick = ai.chooseMove(game);
        assertNotNull(pick,
                () -> "[" + strategyName + "] chooseMove() returned null");
        assertTrue(moves.containsKey(pick),
                () -> "[" + strategyName + "] picked illegal move: " + pick);
    }

    @Test
    public void testMinimaxStrategy() {
        assertLegal("minimax");
    }

    @Test
    public void testExpectimaxStrategy() {
        assertLegal("expectimax");
    }

    @Test
    public void testMCTSStrategy() {
        assertLegal("mcts");
    }

    @Test
    public void testCustomStrategy() {
        assertLegal("custom");
    }
}


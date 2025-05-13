package othello.gamelogic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MCTSStrategy.chooseMove.
 * These tests verify that on a normal board MCTS picks a legal move,
 * and when no moves are available it throws the expected exception.
 */
public class MCTSStrategyTest {
    private ComputerPlayer mctsPlayer;
    private ComputerPlayer minimaxPlayer;
    private OthelloGame game;

    @BeforeEach
    void setUp() {
        mctsPlayer   = new ComputerPlayer("mcts");
        minimaxPlayer = new ComputerPlayer("minimax");
        // Black goes first
        game = new OthelloGame(mctsPlayer, minimaxPlayer);
    }

    @Test
    void testChooseMoveOnInitialBoard() {
        // On the standard 8×8 start, Black (mctsPlayer) has legal moves.
        Map<BoardSpace, ?> moves = game.getAvailableMoves(mctsPlayer);
        assertFalse(moves.isEmpty(), "Initial moves should exist for Black");
        BoardSpace choice = game.computerDecision(mctsPlayer);
        assertNotNull(choice, "MCTS should return a non-null move when moves exist");
        assertTrue(moves.containsKey(choice), "Returned move must be one of the available moves");
    }

    @Test
    void testChooseMoveWhenNoMoves() {
        // Fill the entire board white → Black has no moves.
        for (BoardSpace[] row : game.getBoard()) {
            for (BoardSpace s : row) {
                s.setType(BoardSpace.SpaceType.WHITE);
            }
        }
        Map<BoardSpace, ?> moves = game.getAvailableMoves(mctsPlayer);
        assertTrue(moves.isEmpty(), "There should be no available moves after filling board");

        // Since MCTSStrategy doesn't guard this case, Collections.max() on an empty list will fail.
        assertThrows(NoSuchElementException.class,
                () -> game.computerDecision(mctsPlayer),
                "Expect NoSuchElementException when MCTS has no children to select");
    }
}


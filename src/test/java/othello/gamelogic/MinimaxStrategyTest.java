package othello.gamelogic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the MinimaxStrategy implementation.
 * Verifies that MinimaxStrategy.chooseMove selects a legal move
 * or returns null when no moves are available.
 */
public class MinimaxStrategyTest {

    private ComputerPlayer minimaxPlayer;
    private Player opponent;
    private OthelloGame game;

    @BeforeEach
    void setUp() {
        // create a computer player using minimax strategy and a dummy opponent
        minimaxPlayer = new ComputerPlayer("minimax");
        opponent = new HumanPlayer();
        minimaxPlayer.setColor(BoardSpace.SpaceType.BLACK);
        opponent.setColor(BoardSpace.SpaceType.WHITE);
        // initialize game with minimax as player one to move first
        game = new OthelloGame(minimaxPlayer, opponent);
    }

    /**
     * Verifies chooseMove returns one of the available moves on the initial board.
     */
    @Test
    void testChooseMoveOnInitialBoard() {
        Map<BoardSpace, ?> moves = game.getAvailableMoves(minimaxPlayer);
        assertFalse(moves.isEmpty(), "Minimax should have legal moves at game start");

        BoardSpace choice = minimaxPlayer.chooseMove(game);
        assertNotNull(choice, "chooseMove should not return null when moves exist");
        assertTrue(moves.containsKey(choice),
                "chooseMove should pick a legal move, but picked: (" + choice.getX() + "," + choice.getY() + ")");
    }

    /**
     * Verifies chooseMove returns null when no moves are available.
     */
    @Test
    void testChooseMoveWhenNoMoves() {
        // fill board entirely with opponent pieces to block moves
        BoardSpace[][] board = game.getBoard();
        for (BoardSpace[] row : board) {
            for (BoardSpace s : row) {
                s.setType(BoardSpace.SpaceType.WHITE);
            }
        }
        // now minimax has no legal moves
        Map<BoardSpace, ?> moves = game.getAvailableMoves(minimaxPlayer);
        assertTrue(moves.isEmpty(), "There should be no available moves");

        BoardSpace choice = minimaxPlayer.chooseMove(game);
        assertNull(choice, "chooseMove should return null when no moves exist");
    }
}

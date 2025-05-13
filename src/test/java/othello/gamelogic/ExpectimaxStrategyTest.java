package othello.gamelogic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ExpectimaxStrategy implementation.
 * Verifies that ExpectimaxStrategy.chooseMove returns a legal move
 * on the initial board and null when no moves are available.
 */
public class ExpectimaxStrategyTest {

    private ComputerPlayer expectimaxPlayer;
    private Player opponent;
    private OthelloGame game;

    @BeforeEach
    void setUp() {
        // create a computer player using expectimax strategy and a dummy opponent
        expectimaxPlayer = new ComputerPlayer("expectimax");
        opponent = new HumanPlayer();
        expectimaxPlayer.setColor(BoardSpace.SpaceType.BLACK);
        opponent.setColor(BoardSpace.SpaceType.WHITE);
        // initialize game with expectimax as player one to move first
        game = new OthelloGame(expectimaxPlayer, opponent);
    }

    /**
     * Verifies chooseMove returns one of the available moves on the initial board.
     */
    @Test
    void testChooseMoveOnInitialBoard() {
        Map<BoardSpace, ?> moves = game.getAvailableMoves(expectimaxPlayer);
        assertFalse(moves.isEmpty(), "Expectimax should have legal moves at game start");

        BoardSpace choice = expectimaxPlayer.chooseMove(game);
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
        Map<BoardSpace, ?> moves = game.getAvailableMoves(expectimaxPlayer);
        assertTrue(moves.isEmpty(), "There should be no available moves");

        BoardSpace choice = expectimaxPlayer.chooseMove(game);
        assertNull(choice, "chooseMove should return null when no moves exist");
    }
}


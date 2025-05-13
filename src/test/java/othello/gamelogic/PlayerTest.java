package othello.gamelogic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Player.getAvailableMoves method.
 * <p>
 * These tests verify that the method correctly identifies valid move locations
 * and their corresponding origin spaces for flipping opponent pieces under
 * various board configurations.
 */
public class PlayerTest {

    private HumanPlayer black;
    private HumanPlayer white;
    private BoardSpace[][] board;

    @BeforeEach
    public void setUp() {
        black = new HumanPlayer();
        white = new HumanPlayer();
        black.setColor(BoardSpace.SpaceType.BLACK);
        white.setColor(BoardSpace.SpaceType.WHITE);

        // initialize a fresh OthelloGame and grab its board
        OthelloGame game = new OthelloGame(black, white);
        board = game.getBoard();
    }

    @Test
    public void testInitialMovesForBlack() {
        // On the standard starting position, black has exactly 4 legal moves
        Map<BoardSpace, List<BoardSpace>> moves = black.getAvailableMoves(board);
        assertNotNull(moves, "Expected non-null map of moves");
        assertEquals(4, moves.size(), "Black should have 4 moves at game start");

        // verify that each destination is empty and its origins are correct
        for (Map.Entry<BoardSpace, List<BoardSpace>> entry : moves.entrySet()) {
            BoardSpace dest = entry.getKey();
            List<BoardSpace> origins = entry.getValue();

            assertEquals(BoardSpace.SpaceType.EMPTY, dest.getType(),
                    "Destination must be empty");

            // each origin must be a BLACK piece adjacent in a straight line
            assertFalse(origins.isEmpty(), "Each move must have at least one origin");
            for (BoardSpace origin : origins) {
                assertEquals(BoardSpace.SpaceType.BLACK, origin.getType(),
                        "Origin must be black");
                // must lie in straight line for game
                int dx = Integer.signum(origin.getX() - dest.getX());
                int dy = Integer.signum(origin.getY() - dest.getY());
                assertTrue(Math.abs(dx) <= 1 && Math.abs(dy) <= 1,
                        "Origin must be adjacent in one direction");
            }
        }
    }

    @Test
    public void testInitialMovesForWhite() {
        // At the very start, white also has 4 moves (but can't play until black has moved)
        Map<BoardSpace, List<BoardSpace>> moves = white.getAvailableMoves(board);
        assertNotNull(moves, "Expected non-null map of moves");
        assertEquals(4, moves.size(), "White should have 4 moves at game start");
    }

    @Test
    public void testNoMovesOnFullBoard() {
        // fill the board completely with BLACK
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j].setType(BoardSpace.SpaceType.BLACK);
            }
        }
        Map<BoardSpace, List<BoardSpace>> moves = black.getAvailableMoves(board);
        assertTrue(moves.isEmpty(), "No moves should be available on a full board");
    }

    @Test
    public void testCornerFlip() {
        // create a custom small scenario: one white in (1,1), black at (2,2) and (0,0) empty
        // clear board first
        for (BoardSpace[] row : board) {
            for (BoardSpace s : row) {
                s.setType(BoardSpace.SpaceType.EMPTY);
            }
        }
        board[1][1].setType(BoardSpace.SpaceType.WHITE);
        board[2][2].setType(BoardSpace.SpaceType.BLACK);

        black.getPlayerOwnedSpacesSpaces().clear();
        white.getPlayerOwnedSpacesSpaces().clear();
        black.getPlayerOwnedSpacesSpaces().add(board[2][2]);
        white.getPlayerOwnedSpacesSpaces().add(board[1][1]);

        Map<BoardSpace, List<BoardSpace>> moves = black.getAvailableMoves(board);
        // only possible flip is at (0,0) flipping the white at (1,1)
        BoardSpace dest = board[0][0];
        assertTrue(moves.containsKey(dest), "Black should be able to move at corner (0,0)");

        List<BoardSpace> origins = moves.get(dest);
        assertEquals(1, origins.size(), "There should be exactly one origin");
        assertEquals(board[2][2], origins.get(0), "Origin must be the black piece at (2,2)");
    }
}

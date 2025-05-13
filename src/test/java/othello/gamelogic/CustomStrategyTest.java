package othello.gamelogic;

import othello.gamelogic.strategy.CustomStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CustomStrategy.chooseMove.
 *
 * 1) On the standard start board, it should return a legal move.
 * 2) When a true corner move is available, it must pick that corner.
 * 3) When no moves exist, it should return null.
 */
public class CustomStrategyTest {

    private CustomStrategy strat;
    private ComputerPlayer customPlayer;
    private HumanPlayer humanPlayer;
    private OthelloGame game;

    @BeforeEach
    void setUp() {
        strat         = new CustomStrategy();
        customPlayer  = new ComputerPlayer("custom");
        humanPlayer   = new HumanPlayer();

        // Black = customStrategy, White = human
        customPlayer.setColor(BoardSpace.SpaceType.BLACK);
        humanPlayer.setColor(BoardSpace.SpaceType.WHITE);

        game = new OthelloGame(customPlayer, humanPlayer);
    }

    @Test
    void testChooseMoveOnInitialBoard() {
        // At game start, CustomStrategy should have at least one legal move
        Map<BoardSpace, ?> moves = game.getAvailableMoves(customPlayer);
        assertFalse(moves.isEmpty(), "Initial moves should exist for custom strategy");

        // And the chosen move must be one of them
        BoardSpace choice = strat.chooseMove(game, customPlayer);
        assertNotNull(choice, "chooseMove should not return null when moves exist");
        assertTrue(moves.containsKey(choice),
                "Returned move must be legal: " + choice.getX() + "," + choice.getY());
    }

    @Test
    void testCornerPriority() {
        // Clear the board and set up a white->black chain ending at (0,0)
        BoardSpace[][] b = game.getBoard();
        // empty out everything
        for (BoardSpace[] row : b) {
            for (BoardSpace s : row) {
                s.setType(BoardSpace.SpaceType.EMPTY);
            }
        }
        customPlayer.getPlayerOwnedSpacesSpaces().clear();
        humanPlayer.getPlayerOwnedSpacesSpaces().clear();

        // place WHITE at (0,1) and BLACK at (0,2), so (0,0) is a legal corner move
        b[0][1].setType(BoardSpace.SpaceType.WHITE);
        humanPlayer.getPlayerOwnedSpacesSpaces().add(b[0][1]);
        b[0][2].setType(BoardSpace.SpaceType.BLACK);
        customPlayer.getPlayerOwnedSpacesSpaces().add(b[0][2]);

        // verify getAvailableMoves sees (0,0)
        Map<BoardSpace, ?> moves = game.getAvailableMoves(customPlayer);
        assertTrue(moves.containsKey(b[0][0]), "(0,0) should be legal corner");

        // and chooseMove must pick exactly that corner
        BoardSpace choice = strat.chooseMove(game, customPlayer);
        assertEquals(0, choice.getX(), "Should pick corner X=0");
        assertEquals(0, choice.getY(), "Should pick corner Y=0");
    }

    @Test
    void testNullWhenNoMoves() {
        // Fill the entire board with WHITE → BLACK has no moves
        for (BoardSpace[] row : game.getBoard()) {
            for (BoardSpace s : row) {
                s.setType(BoardSpace.SpaceType.WHITE);
            }
        }
        humanPlayer.getPlayerOwnedSpacesSpaces().clear();
        for (BoardSpace[] row : game.getBoard()) {
            for (BoardSpace s : row) {
                humanPlayer.getPlayerOwnedSpacesSpaces().add(s);
            }
        }
        customPlayer.getPlayerOwnedSpacesSpaces().clear();

        Map<BoardSpace, ?> moves = game.getAvailableMoves(customPlayer);
        assertTrue(moves.isEmpty(), "There should be no available moves");

        // And chooseMove must return null in that case
        assertNull(strat.chooseMove(game, customPlayer),
                "chooseMove should return null when no legal moves exist");
    }
}


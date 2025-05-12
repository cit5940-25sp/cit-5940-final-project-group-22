package othello.util;

import othello.gamelogic.BoardSpace;
import othello.gamelogic.HumanPlayer;
import othello.gamelogic.OthelloGame;
import othello.gamelogic.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SaveLoadUtil saveGame and loadGame methods.
 */
public class SaveLoadUtilTest {

    private OthelloGame game;
    private Player black;
    private Player white;

    @BeforeEach
    void setUp() {
        black = new HumanPlayer();
        white = new HumanPlayer();
        black.setColor(BoardSpace.SpaceType.BLACK);
        white.setColor(BoardSpace.SpaceType.WHITE);
        game = new OthelloGame(black, white);
    }

    /**
     * Saving and loading without moves should preserve initial board and player.
     */
    @Test
    void testSaveLoadInitial() throws IOException {
        File temp = File.createTempFile("othello", ".ots");
        temp.deleteOnExit();

        // save initial state
        SaveLoadUtil.saveGame(game, temp);

        // mutate the game to ensure load actually changes state
        game.takeSpace(black, white, 0, 0);
        assertEquals(BoardSpace.SpaceType.BLACK, game.getBoard()[0][0].getType());

        // load back
        SaveLoadUtil.loadGame(game, temp);

        // verify restored to initial (0,0) is empty again
        assertEquals(BoardSpace.SpaceType.EMPTY,
                game.getBoard()[0][0].getType(),
                "Cell (0,0) should be reset after load");
        // initial four discs present
        int count = 0;
        for (BoardSpace[] row : game.getBoard()) {
            for (BoardSpace s : row) {
                if (s.getType() != BoardSpace.SpaceType.EMPTY) count++;
            }
        }
        assertEquals(4, count, "Initial disc count should be restored");
        // current player reset to black
        assertEquals(BoardSpace.SpaceType.BLACK,
                game.getCurrentPlayer().getColor(),
                "Current player should return to black after load");
    }

    /**
     * Saving and loading after a move should preserve that move.
     */
    @Test
    void testSaveLoadAfterMove() throws IOException {
        // perform one valid move
        var moves = game.getAvailableMoves(black);
        var move = moves.keySet().iterator().next();
        game.takeSpaces(black, white, moves, move);

        File temp = File.createTempFile("othello_move", ".ots");
        temp.deleteOnExit();
        SaveLoadUtil.saveGame(game, temp);

        // clear game to blank state
        game.initBoard();
        game = new OthelloGame(black, white); // reset entirely

        // load saved
        SaveLoadUtil.loadGame(game, temp);

        // the saved move must now exist
        assertEquals(BoardSpace.SpaceType.BLACK,
                game.getBoard()[move.getX()][move.getY()].getType(),
                "The saved move cell must be black after load");
        // and owned spaces include the move
        assertTrue(game.getPlayerOne().getPlayerOwnedSpacesSpaces()
                        .stream()
                        .anyMatch(bs -> bs.getX()==move.getX() && bs.getY()==move.getY()),
                "Owned spaces should include the saved move");
    }
}

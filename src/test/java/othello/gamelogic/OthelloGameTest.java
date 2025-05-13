package othello.gamelogic;

import othello.gamelogic.state.GameMemento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for OthelloGame methods: takeSpace, takeSpaces, memento functionality, and turn management.
 */
public class OthelloGameTest {
    private HumanPlayer black;
    private HumanPlayer white;
    private OthelloGame game;

    @BeforeEach
    void setUp() {
        black = new HumanPlayer();
        white = new HumanPlayer();
        black.setColor(BoardSpace.SpaceType.BLACK);
        white.setColor(BoardSpace.SpaceType.WHITE);
        game = new OthelloGame(black, white);
    }

    @Test
    void testInitialSetup() {
        BoardSpace[][] board = game.getBoard();
        // Initial four discs are placed correctly
        assertEquals(BoardSpace.SpaceType.WHITE, board[3][3].getType());
        assertEquals(BoardSpace.SpaceType.BLACK, board[3][4].getType());
        assertEquals(BoardSpace.SpaceType.BLACK, board[4][3].getType());
        assertEquals(BoardSpace.SpaceType.WHITE, board[4][4].getType());
        // Player owned spaces recorded
        assertTrue(black.getPlayerOwnedSpacesSpaces().contains(board[3][4]));
        assertTrue(black.getPlayerOwnedSpacesSpaces().contains(board[4][3]));
        assertTrue(white.getPlayerOwnedSpacesSpaces().contains(board[3][3]));
        assertTrue(white.getPlayerOwnedSpacesSpaces().contains(board[4][4]));
        // Black goes first hand
        assertSame(black, game.getCurrentPlayer());
    }

    @Test
    void testTakeSpaceOnEmpty() {
        // Take an empty corner
        game.takeSpace(black, white, 0, 0);
        BoardSpace corner = game.getBoard()[0][0];
        assertEquals(BoardSpace.SpaceType.BLACK, corner.getType());
        assertTrue(black.getPlayerOwnedSpacesSpaces().contains(corner));
    }

    @Test
    void testTakeSpaceOnOpponent() {
        // Manually place white at (1,1)
        game.takeSpace(white, black, 1, 1);
        // Black captures that space
        game.takeSpace(black, white, 1, 1);
        BoardSpace target = game.getBoard()[1][1];
        assertEquals(BoardSpace.SpaceType.BLACK, target.getType());
        // Confirm removed from white
        assertFalse(white.getPlayerOwnedSpacesSpaces().contains(target));
        // Confirm added to black
        assertTrue(black.getPlayerOwnedSpacesSpaces().contains(target));
    }

    @Test
    void testTakeSpaceOnOwn() {
        // Black takes (2,2)
        game.takeSpace(black, white, 2, 2);
        int before = black.getPlayerOwnedSpacesSpaces().size();
        // Taking again should do nothing
        game.takeSpace(black, white, 2, 2);
        assertEquals(before, black.getPlayerOwnedSpacesSpaces().size());
    }

    @Test
    void testTakeSpacesSingleDirection() {
        // Setup a simple flip scenario: place B at (4,4), W at (4,5)
        game.setBoard(new BoardSpace[8][8]);
        // rebuild empty board and players
        game.initBoard();
        // clear pieces and set only three spaces: black at (4,3), white at (4,4)
        BoardSpace[][] b = game.getBoard();
        for (int i=0; i<8; i++) for(int j=0; j<8; j++) b[i][j].setType(BoardSpace.SpaceType.EMPTY);
        b[4][3].setType(BoardSpace.SpaceType.BLACK);
        b[4][4].setType(BoardSpace.SpaceType.WHITE);
        black.getPlayerOwnedSpacesSpaces().clear();
        black.getPlayerOwnedSpacesSpaces().add(b[4][3]);
        white.getPlayerOwnedSpacesSpaces().clear();
        white.getPlayerOwnedSpacesSpaces().add(b[4][4]);

        // Black moves to (4,5) flipping one white
        Map<BoardSpace, List<BoardSpace>> moves = game.getAvailableMoves(black);
        BoardSpace dest = b[4][5];
        assertTrue(moves.containsKey(dest));
        game.takeSpaces(black, white, moves, dest);
        // Confirm flip of (4,4)
        assertEquals(BoardSpace.SpaceType.BLACK, b[4][4].getType());
        assertTrue(black.getPlayerOwnedSpacesSpaces().contains(b[4][4]));
    }

    @Test
    void testTurnSwitchAfterTakeSpaces() {
        Player first = game.getCurrentPlayer();
        Map<BoardSpace, List<BoardSpace>> moves = game.getAvailableMoves(first);
        BoardSpace anyDest = moves.keySet().iterator().next();
        game.takeSpaces(first, game.getPlayerTwo(), moves, anyDest);
        // currentPlayer should switch
        assertNotSame(first, game.getCurrentPlayer());
    }

    @Test
    void testMementoRestore() {
        // Make a move and take a memento
        GameMemento m = game.createMemento();
        // Change state: black takes corner
        game.takeSpace(black, white, 0, 0);
        assertEquals(BoardSpace.SpaceType.BLACK, game.getBoard()[0][0].getType());
        // Restore
        game.restoreFromMemento(m);
        assertEquals(BoardSpace.SpaceType.EMPTY, game.getBoard()[0][0].getType());
        // Confirm ownedSpaces lists reset
        assertFalse(black.getPlayerOwnedSpacesSpaces().stream()
                .anyMatch(bs -> bs.getX()==0 && bs.getY()==0));
        // Confirm current player reset to black
        assertSame(black, game.getCurrentPlayer());
    }
}


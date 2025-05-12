package othello.gamelogic.state;

import othello.gamelogic.BoardSpace;
import othello.gamelogic.HumanPlayer;
import othello.gamelogic.OthelloGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for GameMemento behavior:
 * ensures that snapshots capture board state, players, and owned-spaces
 * correctly, and that returned data is protected (deep-copied or immutable).
 */
public class GameMementoTest {
    private OthelloGame game;
    private HumanPlayer p1, p2;

    @BeforeEach
    void setUp() {
        p1 = new HumanPlayer();
        p2 = new HumanPlayer();
        game = new OthelloGame(p1, p2);
    }

    @Test
    void testSnapshotImmutability() {
        // 1. Take a snapshot of the fresh game
        GameMemento snap = game.createMemento();

        // 2. Mutate the original game: flip one of the initial positions
        game.getBoard()[3][3].setType(BoardSpace.SpaceType.BLACK);
        game.getPlayerOne().getPlayerOwnedSpacesSpaces().add(game.getBoard()[0][0]);

        // 3. The snapshot’s board must remain at the pre-mutation state
        BoardSpace[][] boardCopy = snap.getBoardSnapshot();
        assertEquals(BoardSpace.SpaceType.WHITE,
                boardCopy[3][3].getType(),
                "Snapshot cell should not reflect later changes");

        // 4. The snapshot’s list must not include any new additions
        List<BoardSpace> p1List = snap.getP1Spaces();
        boolean addedLater = p1List.stream()
                .anyMatch(bs -> bs.getX()==0 && bs.getY()==0);
        assertFalse(addedLater,
                "Snapshot p1Spaces should not include spaces added after snapshot");

        // 5. The snapshot lists must be unmodifiable
        assertThrows(UnsupportedOperationException.class,
                () -> p1List.add(boardCopy[0][0]),
                "getP1Spaces must return an unmodifiable copy");
    }

    @Test
    void testSnapshotPlayersAndCurrentPlayer() {
        // initial snapshot
        GameMemento m1 = game.createMemento();
        assertSame(p1, m1.getFirstPlayer(),
                "firstPlayer must match constructor argument");
        assertSame(p2, m1.getSecondPlayer(),
                "secondPlayer must match constructor argument");
        assertSame(p1, m1.getCurrentPlayer(),
                "at start, currentPlayer should be p1 (black)");

        // perform one valid move so turn toggles
        BoardSpace dest = game.getAvailableMoves(game.getCurrentPlayer())
                .keySet().iterator().next();
        game.takeSpaces(game.getCurrentPlayer(),
                game.getPlayerTwo(),
                game.getAvailableMoves(game.getCurrentPlayer()),
                dest);

        // new snapshot should record updated currentPlayer
        GameMemento m2 = game.createMemento();
        assertSame(game.getCurrentPlayer(),
                m2.getCurrentPlayer(),
                "snapshot must record new currentPlayer after move");
    }
}

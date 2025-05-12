package othello.gamelogic.state;

import othello.gamelogic.HumanPlayer;
import othello.gamelogic.OthelloGame;
import othello.gamelogic.BoardSpace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for GameHistory:
 * verifies save(), undo() and clear() manage mementos in LIFO order.
 */
public class GameHistoryTest {
    private GameHistory history;
    private OthelloGame game;
    private HumanPlayer p1, p2;

    @BeforeEach
    void setUp() {
        history = new GameHistory();
        p1 = new HumanPlayer();
        p2 = new HumanPlayer();
        game = new OthelloGame(p1, p2);
    }

    @Test
    void testSaveAndUndoOrder() {
        // push first snapshot
        GameMemento first = game.createMemento();
        history.save(first);

        // make a move, push second snapshot
        Map<BoardSpace, List<BoardSpace>> moves = game.getAvailableMoves(game.getCurrentPlayer());
        BoardSpace m = moves.keySet().iterator().next();
        game.takeSpaces(game.getCurrentPlayer(),
                game.getPlayerTwo(), moves, m);
        GameMemento second = game.createMemento();
        history.save(second);

        // undo returns second then first
        assertSame(second, history.undo(),
                "undo() should pop the last saved snapshot first");
        assertSame(first, history.undo(),
                "undo() should then pop the earlier snapshot");
        assertNull(history.undo(),
                "undo() on empty history should return null");
    }

    @Test
    void testClearDropsAllSnapshots() {
        history.save(game.createMemento());
        history.save(game.createMemento());
        history.clear();
        assertNull(history.undo(),
                "after clear(), undo() should return null");
    }
}


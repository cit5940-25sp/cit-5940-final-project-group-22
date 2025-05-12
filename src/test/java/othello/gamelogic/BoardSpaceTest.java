package othello.gamelogic;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BoardSpace class:
 * verifies getters, setters, copy constructor, and enum fill colors.
 */
public class BoardSpaceTest {

    private BoardSpace space;

    @BeforeEach
    public void setUp() {
        // start every test with a fresh EMPTY space at (1,2)
        space = new BoardSpace(1, 2, BoardSpace.SpaceType.EMPTY);
    }

    @Test
    public void testGetCoordinates() {
        // the constructor parameters should be returned by getX()/getY()
        assertEquals(1, space.getX(), "X coordinate should match constructor argument");
        assertEquals(2, space.getY(), "Y coordinate should match constructor argument");
    }

    @Test
    public void testGetTypeAndSetType() {
        // initial type is EMPTY
        assertEquals(BoardSpace.SpaceType.EMPTY, space.getType(),
                "Initial type should be EMPTY");

        // setting to BLACK and then WHITE should update getType()
        space.setType(BoardSpace.SpaceType.BLACK);
        assertEquals(BoardSpace.SpaceType.BLACK, space.getType(),
                "Type should update to BLACK");

        space.setType(BoardSpace.SpaceType.WHITE);
        assertEquals(BoardSpace.SpaceType.WHITE, space.getType(),
                "Type should update to WHITE");
    }

    @Test
    public void testCopyConstructor() {
        // change the original's type, then copy
        space.setType(BoardSpace.SpaceType.BLACK);
        BoardSpace clone = new BoardSpace(space);

        // clone must have the same fields
        assertEquals(space.getX(), clone.getX(),
                "Clone X should equal original X");
        assertEquals(space.getY(), clone.getY(),
                "Clone Y should equal original Y");
        assertEquals(space.getType(), clone.getType(),
                "Clone type should equal original type");

        // but it must not be the same object
        assertNotSame(space, clone, "Clone should be a different instance");
    }

    @Test
    public void testEnumFillColors() {
        // each enum value carries the correct JavaFX Color
        assertEquals(Color.GRAY,  BoardSpace.SpaceType.EMPTY.fill(),
                "EMPTY fill color should be GRAY");
        assertEquals(Color.BLACK, BoardSpace.SpaceType.BLACK.fill(),
                "BLACK fill color should be BLACK");
        assertEquals(Color.WHITE, BoardSpace.SpaceType.WHITE.fill(),
                "WHITE fill color should be WHITE");
    }
}


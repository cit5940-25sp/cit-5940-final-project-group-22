package othello.gamelogic.strategy;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the StrategyFactory.
 */
public class StrategyFactoryTest {

    @Test
    public void testValidStrategies() {
        Strategy s1 = StrategyFactory.create("minimax");
        assertNotNull(s1);
        assertTrue(s1 instanceof MinimaxStrategy);

        Strategy s2 = StrategyFactory.create("expectimax");
        assertNotNull(s2);
        assertTrue(s2 instanceof ExpectimaxStrategy);

        Strategy s3 = StrategyFactory.create("mcts");
        assertNotNull(s3);
        assertTrue(s3 instanceof MCTSStrategy);

        Strategy s4 = StrategyFactory.create("custom");
        assertNotNull(s4);
        assertTrue(s4 instanceof CustomStrategy);
    }

    @Test
    public void testUnknownStrategyThrows() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> StrategyFactory.create("nonexistent")
        );
        assertTrue(ex.getMessage().contains("Unknown strategy"));
    }
}


package othello.gamelogic.strategy;

public class StrategyFactory {

    public static Strategy create(String name) {
        return switch (name) {
            case "minimax" -> new MinimaxStrategy();
            case "expectimax" -> new ExpectimaxStrategy();
            case "mcts" -> new MCTSStrategy();
            case "custom" -> new CustomStrategy();
            default -> throw new IllegalArgumentException("Unknown strategy: " + name);
        };
    }
}

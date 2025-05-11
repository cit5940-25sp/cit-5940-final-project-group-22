package othello.gamelogic.strategy;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import othello.gamelogic.*;
import othello.gamelogic.strategy.*;

import java.util.List;
import java.util.Map;

public class StrategyComparisonTest {

    public static void main(String[] args) {
        final int NUM_GAMES = 100;
        int customWins = 0;
        int minimaxWins = 0;
        int draws = 0;

        for (int i = 1; i <= NUM_GAMES; i++) {
            // Alternate first move: on even games Custom plays Black (first), on odd games Minimax plays Black
            boolean customIsBlack = (i % 2 == 0);

            ComputerPlayer customPlayer  = new ComputerPlayer("custom");
            ComputerPlayer minimaxPlayer = new ComputerPlayer("minimax");

            if (customIsBlack) {
                customPlayer.setColor(BoardSpace.SpaceType.BLACK);
                minimaxPlayer.setColor(BoardSpace.SpaceType.WHITE);
            } else {
                customPlayer.setColor(BoardSpace.SpaceType.WHITE);
                minimaxPlayer.setColor(BoardSpace.SpaceType.BLACK);
            }

            // Construct the game with the correct turn order
            OthelloGame game = customIsBlack
                    ? new OthelloGame(customPlayer, minimaxPlayer)
                    : new OthelloGame(minimaxPlayer, customPlayer);

            Player current  = game.getPlayerOne();
            Player opponent = game.getPlayerTwo();

            // Main gameplay loop
            while (true) {
                Map<BoardSpace, List<BoardSpace>> moves = game.getAvailableMoves(current);
                if (!moves.isEmpty()) {
                    // Select move based on current player’s strategy
                    BoardSpace choice;
                    if (current == customPlayer) {
                        choice = game.computerDecision(customPlayer);
                    } else {
                        choice = game.computerDecision(minimaxPlayer);
                    }
                    game.takeSpaces(current, opponent, moves, choice);
                } else {
                    // If both players have no available moves, end the game
                    if (game.getAvailableMoves(opponent).isEmpty()) {
                        break;
                    }
                    // Otherwise, skip this player's turn
                }
                // Swap turns
                Player tmp = current;
                current  = opponent;
                opponent = tmp;
            }

            // Count pieces to determine winner
            int customCount  = customPlayer.getPlayerOwnedSpacesSpaces().size();
            int minimaxCount = minimaxPlayer.getPlayerOwnedSpacesSpaces().size();

            if (customCount > minimaxCount) {
                customWins++;
            } else if (minimaxCount > customCount) {
                minimaxWins++;
            } else {
                draws++;
            }
        }

        // Print final results
        System.out.println("Played " + NUM_GAMES + " games:");
        System.out.printf("  Custom wins %d games (%.2f%%)%n", customWins,  customWins  * 100.0 / NUM_GAMES);
        System.out.printf("  Minimax wins %d games (%.2f%%)%n", minimaxWins, minimaxWins * 100.0 / NUM_GAMES);
        System.out.printf("  Draws %d games (%.2f%%)%n", draws,      draws      * 100.0 / NUM_GAMES);
    }
}

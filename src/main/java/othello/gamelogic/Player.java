package othello.gamelogic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract Player class for representing a player within the game.
 * All types of Players have a color and a set of owned spaces on the game board.
 */
public abstract class Player {
    private final List<BoardSpace> playerOwnedSpaces = new ArrayList<>();
    public List<BoardSpace> getPlayerOwnedSpacesSpaces() {
        return playerOwnedSpaces;
    }

    private BoardSpace.SpaceType color;
    public void setColor(BoardSpace.SpaceType color) {
        this.color = color;
    }
    public BoardSpace.SpaceType getColor() {
        return color;
    }

    /**
     * PART 1
     * TODO: Implement this method
     * Gets the available moves for this player given a certain board state.
     * This method will find destinations, empty spaces that are valid moves,
     * and map them to a list of origins that can traverse to those destinations.
     * @param board the board that will be evaluated for possible moves for this player
     * @return a map with a destination BoardSpace mapped to a List of origin BoardSpaces.
     */
    public Map<BoardSpace, List<BoardSpace>> getAvailableMoves(BoardSpace[][] board) {
        Map<BoardSpace, List<BoardSpace>> moves = new HashMap<>();

        BoardSpace.SpaceType me       = getColor();
        BoardSpace.SpaceType opponent = (me == BoardSpace.SpaceType.BLACK)
                ? BoardSpace.SpaceType.WHITE
                : BoardSpace.SpaceType.BLACK;

        // Directions: N, NE, E, SE, S, SW, W, NW.
        int[] dx = {-1,-1, 0, 1, 1, 1, 0,-1};
        int[] dy = { 0, 1, 1, 1, 0,-1,-1,-1};
        int size = board.length;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j].getType() != BoardSpace.SpaceType.EMPTY) {
                    continue;
                }

                List<BoardSpace> origins = new ArrayList<>();
                // Check each direction for flippable opponent pieces.
                for (int d = 0; d < dx.length; d++) {
                    int x = i + dx[d], y = j + dy[d];
                    boolean seenOpponent = false;

                    // Traverse while encountering opponent pieces.
                    while (x >= 0 && x < size
                            && y >= 0 && y < size
                            && board[x][y].getType() == opponent) {
                        seenOpponent = true;
                        x += dx[d];
                        y += dy[d];
                    }
                    // If at least one opponent piece was seen and we end at a friendly piece,
                    // record that friend as an origin for flipping.
                    if (seenOpponent
                            && x >= 0 && x < size
                            && y >= 0 && y < size
                            && board[x][y].getType() == me) {
                        origins.add(board[x][y]);
                    }
                }

                if (!origins.isEmpty()) {
                    moves.put(board[i][j], origins);
                }
            }
        }
        return moves;
    }
}
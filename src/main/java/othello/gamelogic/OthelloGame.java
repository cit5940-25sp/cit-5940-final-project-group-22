package othello.gamelogic;

import java.util.*;

/**
 * Models a board of Othello.
 * Includes methods to get available moves and take spaces.
 */
public class OthelloGame {
    public static final int GAME_BOARD_SIZE = 8;

    private BoardSpace[][] board;
    private final Player playerOne;
    private final Player playerTwo;

    public OthelloGame(Player playerOne, Player playerTwo) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        initBoard();
    }

    public BoardSpace[][] getBoard() {
        return board;
    }

    public Player getPlayerOne() {
        return playerOne;
    }

    public Player getPlayerTwo() {
        return  playerTwo;
    }

    /**
     * Returns the available moves for a player.
     * Used by the GUI to get available moves each turn.
     * @param player player to get moves for
     * @return the map of available moves,that maps destination to list of origins
     */
    public Map<BoardSpace, List<BoardSpace>> getAvailableMoves(Player player) {
        return player.getAvailableMoves(board);
    }

    /**
     * Initializes the board at the start of the game with all EMPTY spaces.
     */
    public void initBoard() {
        board = new BoardSpace[GAME_BOARD_SIZE][GAME_BOARD_SIZE];
        for (int i = 0; i < GAME_BOARD_SIZE; i++) {
            for (int j = 0; j < GAME_BOARD_SIZE; j++) {
                board[i][j] = new BoardSpace(i, j, BoardSpace.SpaceType.EMPTY);
            }
        }

        // 2) Place the 4 starting discs
        //    (row 3,col 3) WHITE    (row 3,col 4) BLACK
        //    (row 4,col 3) BLACK    (row 4,col 4) WHITE
        board[3][3].setType(BoardSpace.SpaceType.WHITE);
        board[3][4].setType(BoardSpace.SpaceType.BLACK);
        board[4][3].setType(BoardSpace.SpaceType.BLACK);
        board[4][4].setType(BoardSpace.SpaceType.WHITE);

        // 3) Record them in each player's ownedSpaces
        playerOne.getPlayerOwnedSpacesSpaces().add(board[3][4]);  // black
        playerOne.getPlayerOwnedSpacesSpaces().add(board[4][3]);  // black
        playerTwo.getPlayerOwnedSpacesSpaces().add(board[3][3]);  // white
        playerTwo.getPlayerOwnedSpacesSpaces().add(board[4][4]);  // white
    }

    /**
     * PART 1
     * TODO: Implement this method
     * Claims the specified space for the acting player.
     * Should also check if the space being taken is already owned by the acting player,
     * should not claim anything if acting player already owns space at (x,y)
     * @param actingPlayer the player that will claim the space at (x,y)
     * @param opponent the opposing player, will lose a space if their space is at (x,y)
     * @param x the x-coordinate of the space to claim
     * @param y the y-coordinate of the space to claim
     */

    public void takeSpace(Player actingPlayer, Player opponent, int x, int y) {
        BoardSpace space = board[x][y];
        // If it's already owned by actingPlayer, do nothing
        if (space.getType() == actingPlayer.getColor()) {
            return;
        }
        // If it was owned by opponent, remove it from their list
        if (space.getType() == opponent.getColor()) {
            opponent.getPlayerOwnedSpacesSpaces().remove(space);
        }
        // Claim the space for actingPlayer
        space.setType(actingPlayer.getColor());
        actingPlayer.getPlayerOwnedSpacesSpaces().add(space);
    }
    /**
     * PART 1
     * TODO: Implement this method
     * Claims spaces from all origins that lead to a specified destination.
     * This is called when a player, human or computer, selects a valid destination.
     * @param actingPlayer the player that will claim spaces
     * @param opponent the opposing player, that may lose spaces
     * @param availableMoves map of the available moves, that maps destination to list of origins
     * @param selectedDestination the specific destination that a HUMAN player selected
     */
    public void takeSpaces(
            Player actingPlayer,
            Player opponent,
            Map<BoardSpace, List<BoardSpace>> availableMoves,
            BoardSpace selectedDestination
    ) {
        List<BoardSpace> origins = availableMoves.get(selectedDestination);
        if (origins == null) {
            return;
        }
        // For each origin, step from destination toward it and flip pieces
        for (BoardSpace origin : origins) {
            int dx = Integer.signum(origin.getX() - selectedDestination.getX());
            int dy = Integer.signum(origin.getY() - selectedDestination.getY());
            int x = selectedDestination.getX() + dx;
            int y = selectedDestination.getY() + dy;
            // Flip until reaching the origin space
            while (x != origin.getX() || y != origin.getY()) {
                takeSpace(actingPlayer, opponent, x, y);
                x += dx;
                y += dy;
            }
        }
        // Finally claim the destination itself
        takeSpace(actingPlayer, opponent,
                selectedDestination.getX(),
                selectedDestination.getY());
    }
    /**
     * PART 2
     * TODO: Implement this method
     * Gets the computer decision for its turn.
     * Should call a method within the ComputerPlayer class that returns a BoardSpace using a specific strategy.
     * @param computer computer player that is deciding their move for their turn
     * @return the BoardSpace that was decided upon
     */
    public BoardSpace computerDecision(ComputerPlayer computer) {
        // Let the player’s strategy inspect the entire game and return its best move
        return computer.chooseMove(this);
    }

}

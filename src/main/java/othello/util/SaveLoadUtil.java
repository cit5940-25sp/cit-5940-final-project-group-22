package othello.util;

import othello.gamelogic.OthelloGame;
import othello.gamelogic.BoardSpace;
import othello.gamelogic.state.GameMemento;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for saving/loading game state to disk.
 */
public class SaveLoadUtil {

    /**
     * Save current game state to a binary file.
     * @param game the OthelloGame instance
     * @param file the target file to write
     * @throws IOException if any IO error occurs
     */
    public static void saveGame(OthelloGame game, File file) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(file)))) {
            // Write a flag indicating if it's playerOne’s turn
            boolean isPlayerOneTurn = game.getCurrentPlayer() == game.getPlayerOne();
            dos.writeBoolean(isPlayerOneTurn);

            // Write each cell's type ordinal (EMPTY/BLACK/WHITE)
            BoardSpace[][] board = game.getBoard();
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    dos.writeByte(board[i][j].getType().ordinal());
                }
            }
        }
    }

    /**
     * Load game state from a file and restore into the given game instance.
     * @param game the OthelloGame to restore into
     * @param file the source file to read
     * @throws IOException if any IO error occurs
     */
    public static void loadGame(OthelloGame game, File file) throws IOException {
        try (DataInputStream dis = new DataInputStream(
                new BufferedInputStream(new FileInputStream(file)))) {
            // 1) Read whose turn it is
            boolean isPlayerOneTurn = dis.readBoolean();

            // 2) Rebuild board snapshot
            int size = OthelloGame.GAME_BOARD_SIZE;        // :contentReference[oaicite:0]{index=0}:contentReference[oaicite:1]{index=1}
            BoardSpace[][] snapshot = new BoardSpace[size][size];
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    int ord = dis.readByte();
                    BoardSpace.SpaceType type = BoardSpace.SpaceType.values()[ord];
                    snapshot[i][j] = new BoardSpace(i, j, type);
                }
            }

            // 3) Reconstruct each player's owned-space list
            List<BoardSpace> p1Spaces = new ArrayList<>();
            List<BoardSpace> p2Spaces = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    BoardSpace bs = snapshot[i][j];
                    if (bs.getType() == game.getPlayerOne().getColor()) {
                        p1Spaces.add(bs);
                    } else if (bs.getType() == game.getPlayerTwo().getColor()) {
                        p2Spaces.add(bs);
                    }
                }
            }

            // 4) Build a memento and restore
            GameMemento m = new GameMemento(
                    snapshot,
                    game.getPlayerOne(),
                    game.getPlayerTwo(),
                    isPlayerOneTurn ? game.getPlayerOne() : game.getPlayerTwo(),
                    p1Spaces,
                    p2Spaces
            );
            game.restoreFromMemento(m);
        }
    }
}

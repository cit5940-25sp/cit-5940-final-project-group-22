package othello.gamelogic.state;

import othello.gamelogic.BoardSpace;
import othello.gamelogic.Player;

import java.util.List;

/**
 * Immutable snapshot of an OthelloGame state.
 */
public class GameMemento {
    private final BoardSpace[][] boardSnapshot;
    private final Player firstPlayer;
    private final Player secondPlayer;
    private final Player currentPlayer;
    private final List<BoardSpace> p1Spaces;
    private final List<BoardSpace> p2Spaces;

    public GameMemento(BoardSpace[][] board,
                       Player p1,
                       Player p2,
                       Player current,
                       List<BoardSpace> p1List,
                       List<BoardSpace> p2List) {
        // deep copy board
        int n = board.length, m = board[0].length;
        this.boardSnapshot = new BoardSpace[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                this.boardSnapshot[i][j] = new BoardSpace(board[i][j]);
            }
        }
        this.firstPlayer  = p1;
        this.secondPlayer = p2;
        this.currentPlayer = current;
        // copy lists to make them immutable
        this.p1Spaces = List.copyOf(p1List);
        this.p2Spaces = List.copyOf(p2List);
    }

    public BoardSpace[][] getBoardSnapshot() { return boardSnapshot; }
    public Player getFirstPlayer()        { return firstPlayer; }
    public Player getSecondPlayer()       { return secondPlayer; }
    public Player getCurrentPlayer()      { return currentPlayer; }
    public List<BoardSpace> getP1Spaces() { return p1Spaces; }
    public List<BoardSpace> getP2Spaces() { return p2Spaces; }
}

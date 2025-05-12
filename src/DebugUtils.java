package othello.gamelogic;

import java.util.Arrays;

/**
 * test only
 */
public class DebugUtils {


    public static String boardToString(BoardSpace[][] board) {
        StringBuilder sb = new StringBuilder();
        int n = board.length;
        sb.append("   ");
        for (int j = 0; j < n; j++) {
            sb.append(String.format("%2d ", j));
        }
        sb.append("\n");
        for (int i = 0; i < n; i++) {
            sb.append(String.format("%2d ", i));  
            for (int j = 0; j < n; j++) {
                BoardSpace space = board[i][j];
                char c;
                switch (space.getOwner()) {
                    case BLACK: c = 'B'; break;
                    case WHITE: c = 'W'; break;
                    default:    c = '.'; break;
                }
                sb.append(" ").append(c).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}

package othello.gamelogic.strategy;

import othello.gamelogic.BoardSpace;
import othello.gamelogic.HumanPlayer;
import othello.gamelogic.OthelloGame;
import othello.gamelogic.Player;

import java.util.*;

/**
 * 自定义策略：一层对手极小化响应 + 启发式评估
 */
public class CustomStrategy implements Strategy {
    // 位置权重表
    private static final int[][] WEIGHTS = {
            { 100, -20,  10,   5,   5,  10, -20, 100},
            { -20, -50,  -2,  -2,  -2,  -2, -50, -20},
            {  10,  -2,   2,   2,   2,   2,  -2,  10},
            {   5,  -2,   2,   0,   0,   2,  -2,   5},
            {   5,  -2,   2,   0,   0,   2,  -2,   5},
            {  10,  -2,   2,   2,   2,   2,  -2,  10},
            { -20, -50,  -2,  -2,  -2,  -2, -50, -20},
            { 100, -20,  10,   5,   5,  10, -20, 100}
    };

    @Override
    public BoardSpace chooseMove(OthelloGame game, Player me) {
        Map<BoardSpace, List<BoardSpace>> moves = game.getAvailableMoves(me);
        if (moves.isEmpty()) return null;

        Player opp = (game.getPlayerOne() == me)
                ? game.getPlayerTwo() : game.getPlayerOne();

        BoardSpace bestMove = null;
        double     bestScore = Double.NEGATIVE_INFINITY;

        for (BoardSpace move : moves.keySet()) {
            // 1) 克隆局面并落子
            OthelloGame sim1 = cloneGame(game);
            Player simMe  = sim1.getPlayerOne().getColor() == me.getColor()
                    ? sim1.getPlayerOne()
                    : sim1.getPlayerTwo();
            Player simOpp = (simMe == sim1.getPlayerOne())
                    ? sim1.getPlayerTwo()
                    : sim1.getPlayerOne();
            sim1.takeSpaces(simMe, simOpp,
                    sim1.getAvailableMoves(simMe),
                    move);

            // 2) 对手所有回应中取最差（对我最不利）得分
            Map<BoardSpace,List<BoardSpace>> oppMoves =
                    sim1.getAvailableMoves(simOpp);
            double worst = Double.POSITIVE_INFINITY;
            if (oppMoves.isEmpty()) {
                worst = evaluate(sim1, simMe, simOpp);
            } else {
                for (BoardSpace r : oppMoves.keySet()) {
                    OthelloGame sim2 = cloneGame(sim1);
                    Player m2 = sim2.getPlayerOne().getColor() == me.getColor()
                            ? sim2.getPlayerOne()
                            : sim2.getPlayerTwo();
                    Player o2 = (m2 == sim2.getPlayerOne())
                            ? sim2.getPlayerTwo()
                            : sim2.getPlayerOne();
                    sim2.takeSpaces(o2, m2,
                            sim2.getAvailableMoves(o2),
                            r);
                    worst = Math.min(worst, evaluate(sim2, m2, o2));
                }
            }

            // 3) 选最大
            if (worst > bestScore) {
                bestScore = worst;
                bestMove  = move;
            }
        }

        // 返回原棋盘对象
        return game.getBoard()[bestMove.getX()][bestMove.getY()];
    }

    /** 启发式评估：位置权重 + 棋子差 + 行动力 + 边界惩罚 */
    private double evaluate(OthelloGame g, Player me, Player opp) {
        BoardSpace[][] b = g.getBoard();
        double score = 0;

        // 位置权重
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (b[i][j].getType() == me.getColor())      score += WEIGHTS[i][j];
                else if (b[i][j].getType() == opp.getColor()) score -= WEIGHTS[i][j];
            }
        }

        // 棋子数差
        score += 2 * (me.getPlayerOwnedSpacesSpaces().size()
                - opp.getPlayerOwnedSpacesSpaces().size());

        // 行动力差
        score += 5 * (g.getAvailableMoves(me).size()
                - g.getAvailableMoves(opp).size());

        // 边界棋子惩罚
        score -= 3 * (frontierCount(g, me) - frontierCount(g, opp));

        return score;
    }

    /** 计算边界棋子数 */
    private int frontierCount(OthelloGame g, Player p) {
        int cnt = 0;
        BoardSpace[][] b = g.getBoard();
        for (BoardSpace bs : p.getPlayerOwnedSpacesSpaces()) {
            int x = bs.getX(), y = bs.getY();
            boolean front = false;
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    int nx = x + dx, ny = y + dy;
                    if (nx >= 0 && ny >= 0 && nx < 8 && ny < 8
                            && b[nx][ny].getType() == BoardSpace.SpaceType.EMPTY) {
                        front = true;
                    }
                }
            }
            if (front) cnt++;
        }
        return cnt;
    }

    /** 克隆棋局（包括 ownedSpaces） */
    private OthelloGame cloneGame(OthelloGame orig) {
        // 1. 新玩家
        Player p1 = new HumanPlayer();
        Player p2 = new HumanPlayer();
        p1.setColor(orig.getPlayerOne().getColor());
        p2.setColor(orig.getPlayerTwo().getColor());

        // 2. 新游戏 + 复制棋盘
        OthelloGame copy = new OthelloGame(p1, p2);
        BoardSpace[][] ob = orig.getBoard();
        BoardSpace[][] nb = new BoardSpace[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                nb[i][j] = new BoardSpace(ob[i][j]);
            }
        }
        copy.setBoard(nb);

        // 3. 重建 ownedSpaces
        for (BoardSpace[] row : nb) {
            for (BoardSpace bs : row) {
                if (bs.getType() == p1.getColor()) p1.getPlayerOwnedSpacesSpaces().add(bs);
                else if (bs.getType() == p2.getColor()) p2.getPlayerOwnedSpacesSpaces().add(bs);
            }
        }
        return copy;
    }
}

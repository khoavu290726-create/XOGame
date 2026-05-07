package ai;

import model.Board;
import model.CellState;
import model.Move;

import java.util.List;
import java.util.Random;

/**
 * Triển khai AI chơi ngẫu nhiên (Easy difficulty).
 * Dùng để cho người mới chơi có thể thắng được AI.
 *
 * @author SinhVienCNTT
 */
public class RandomStrategy implements AIStrategy {

    private final Random random = new Random();

    @Override
    public Move findBestMove(Board board, CellState aiSymbol) {
        List<Move> moves = board.getAvailableMoves(aiSymbol);
        if (moves.isEmpty()) return null;
        return moves.get(random.nextInt(moves.size()));
    }

    @Override
    public String getStrategyName() { return "Random (Easy)"; }
}
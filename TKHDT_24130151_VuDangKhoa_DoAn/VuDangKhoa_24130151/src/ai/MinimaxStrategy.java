package ai;

import exception.InvalidMoveException;
import model.Board;
import model.CellState;
import model.GameResult;
import model.Move;

import java.util.List;

/**
 * Triển khai AI sử dụng thuật toán Minimax + Alpha-Beta Pruning.
 *
 * === NGUYÊN LÝ MINIMAX ===
 * - AI (Maximizer): luôn chọn nước đi có điểm CAO NHẤT
 * - Đối thủ (Minimizer): luôn chọn nước đi có điểm THẤP NHẤT
 * - AI giả định đối thủ cũng chơi tối ưu
 *
 * === ALPHA-BETA PRUNING ===
 * - alpha: điểm tốt nhất Maximizer tìm được
 * - beta:  điểm tốt nhất Minimizer tìm được
 * - Cắt tỉa nhánh vô dụng → giảm số nút cần duyệt
 *
 * === ĐÁNH GIÁ ===
 * - AI thắng sớm (depth nhỏ): điểm cao hơn
 * - AI thắng muộn: điểm thấp hơn
 * → AI ưu tiên thắng nhanh, tránh thua muộn
 *
 * @author SinhVienCNTT
 */
public class MinimaxStrategy implements AIStrategy {

    private static final int SCORE_WIN  =  10;
    private static final int SCORE_LOSE = -10;
    private static final int SCORE_DRAW =   0;

    private CellState aiSymbol;
    private CellState humanSymbol;
    private int nodesEvaluated; // Đếm số node đã duyệt (debug/thống kê)

    @Override
    public Move findBestMove(Board board, CellState aiSymbol) {
        this.aiSymbol    = aiSymbol;
        this.humanSymbol = aiSymbol.opponent();
        this.nodesEvaluated = 0;

        List<Move> available = board.getAvailableMoves(aiSymbol);
        if (available.isEmpty()) return null;

        Move bestMove  = null;
        int  bestScore = Integer.MIN_VALUE;

        for (Move move : available) {
            try {
                board.makeMove(move);
                int score = minimax(board, 0, false,
                                    Integer.MIN_VALUE, Integer.MAX_VALUE);
                board.undoMove();

                // Nếu điểm tốt hơn → cập nhật bestMove
                if (score > bestScore) {
                    bestScore = score;
                    bestMove  = move;
                }
            } catch (InvalidMoveException e) {
                // Không xảy ra vì đã lọc từ getAvailableMoves
                e.printStackTrace();
            }
        }

        System.out.println("[AI] Đã đánh giá " + nodesEvaluated
                         + " node, chọn: " + bestMove + " (score=" + bestScore + ")");
        return bestMove;
    }

    /**
     * Hàm đệ quy Minimax với Alpha-Beta Pruning.
     *
     * @param board         Bàn cờ hiện tại
     * @param depth         Độ sâu đệ quy (số nước đã đi trong nhánh này)
     * @param isMaximizing  true nếu đến lượt AI (Maximizer), false nếu đến lượt người (Minimizer)
     * @param alpha         Giá trị alpha (best score của Maximizer)
     * @param beta          Giá trị beta  (best score của Minimizer)
     * @return Điểm đánh giá của trạng thái này
     */
    private int minimax(Board board, int depth, boolean isMaximizing,
                        int alpha, int beta) {
        nodesEvaluated++;

        // === BASE CASE: Kiểm tra trạng thái kết thúc ===
        GameResult result = board.checkResult();
        if (result == GameResult.X_WIN) {
            int raw = aiSymbol == CellState.X ? SCORE_WIN : SCORE_LOSE;
            return raw - depth * (raw > 0 ? 1 : -1); // Thắng sớm → điểm cao hơn
        }
        if (result == GameResult.O_WIN) {
            int raw = aiSymbol == CellState.O ? SCORE_WIN : SCORE_LOSE;
            return raw - depth * (raw > 0 ? 1 : -1);
        }
        if (result == GameResult.DRAW || board.isFull()) {
            return SCORE_DRAW;
        }

        // === RECURSIVE CASE ===
        if (isMaximizing) {
            // Lượt AI: tìm điểm CAO NHẤT
            int bestScore = Integer.MIN_VALUE;
            for (Move move : board.getAvailableMoves(aiSymbol)) {
                try {
                    board.makeMove(move);
                    int score = minimax(board, depth + 1, false, alpha, beta);
                    board.undoMove();

                    bestScore = Math.max(bestScore, score);
                    alpha     = Math.max(alpha, bestScore);

                    // === CẮT TỈA BETA ===
                    if (beta <= alpha) break; // Nhánh này không cần duyệt tiếp
                } catch (InvalidMoveException e) {
                    e.printStackTrace();
                }
            }
            return bestScore;

        } else {
            // Lượt người: tìm điểm THẤP NHẤT
            int bestScore = Integer.MAX_VALUE;
            for (Move move : board.getAvailableMoves(humanSymbol)) {
                try {
                    board.makeMove(move);
                    int score = minimax(board, depth + 1, true, alpha, beta);
                    board.undoMove();

                    bestScore = Math.min(bestScore, score);
                    beta      = Math.min(beta, bestScore);

                    // === CẮT TỈA ALPHA ===
                    if (beta <= alpha) break;
                } catch (InvalidMoveException e) {
                    e.printStackTrace();
                }
            }
            return bestScore;
        }
    }

    public int getNodesEvaluated() { return nodesEvaluated; }

    @Override
    public String getStrategyName() { return "Minimax + Alpha-Beta Pruning"; }
}
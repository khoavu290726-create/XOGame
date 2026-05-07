package ai;

import model.Board;
import model.CellState;
import model.Move;

/**
 * Interface định nghĩa chiến lược AI.
 * Áp dụng Strategy Pattern: dễ dàng thay đổi thuật toán AI
 * mà không ảnh hưởng đến phần còn lại của ứng dụng.
 *
 * @author SinhVienCNTT
 */
public interface AIStrategy {

    /**
     * Tính toán nước đi tốt nhất cho AI.
     *
     * @param board     Bàn cờ hiện tại
     * @param aiSymbol  Ký hiệu của AI (X hoặc O)
     * @return Nước đi tốt nhất, hoặc null nếu không có ô trống
     */
    Move findBestMove(Board board, CellState aiSymbol);

    /**
     * Tên của chiến lược (hiển thị trong UI).
     */
    String getStrategyName();
}
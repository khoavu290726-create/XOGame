package model;

import config.AppConfig;
import exception.InvalidMoveException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Lớp đại diện cho bàn cờ 3x3.
 *
 * Trách nhiệm (Single Responsibility):
 * - Lưu trạng thái các ô
 * - Kiểm tra tính hợp lệ của nước đi
 * - Kiểm tra điều kiện thắng/thua/hòa
 * - Cung cấp thông tin bàn cờ cho AI
 *
 * @author SinhVienCNTT
 */
public class Board {

    private final int size;
    private CellState[][] grid;
    private int moveCount;
    private int[] winningLine; // [r1,c1, r2,c2, r3,c3] của đường thắng
    private List<Move> moveHistory;

    public Board() {
        this.size = AppConfig.BOARD_SIZE;
        this.grid = new CellState[size][size];
        this.moveHistory = new ArrayList<>();
        reset();
    }

    // ==================== THAO TÁC BÀN CỜ ====================

    /**
     * Thực hiện nước đi. Ném exception nếu không hợp lệ.
     *
     * @throws InvalidMoveException nếu ô không hợp lệ hoặc đã có quân
     */
    public void makeMove(Move move) throws InvalidMoveException {
        int r = move.getRow(), c = move.getCol();

        if (!isInBounds(r, c)) {
            throw new InvalidMoveException(
                "Ô (" + r + "," + c + ") nằm ngoài bàn cờ!", r, c);
        }
        if (!grid[r][c].isEmpty()) {
            throw new InvalidMoveException(
                "Ô (" + r + "," + c + ") đã có quân " + grid[r][c] + "!", r, c);
        }

        grid[r][c] = move.getPlayer();
        moveCount++;
        moveHistory.add(move);
    }

    /**
     * Hoàn tác nước đi cuối cùng (Undo).
     * Dùng trong thuật toán Minimax của AI.
     */
    public void undoMove() {
        if (moveHistory.isEmpty()) return;
        Move last = moveHistory.remove(moveHistory.size() - 1);
        grid[last.getRow()][last.getCol()] = CellState.EMPTY;
        moveCount--;
        winningLine = null; // reset cờ thắng
    }

    /**
     * Reset bàn cờ về trạng thái ban đầu.
     */
    public void reset() {
        for (CellState[] row : grid)
            Arrays.fill(row, CellState.EMPTY);
        moveCount   = 0;
        winningLine = null;
        moveHistory.clear();
    }

    // ==================== KIỂM TRA KẾT QUẢ ====================

    /**
     * Kiểm tra và trả về kết quả hiện tại của bàn cờ.
     */
    public GameResult checkResult() {
        // Kiểm tra 3 hàng ngang
        for (int r = 0; r < size; r++) {
            if (checkLine(r, 0, r, 1, r, 2))
                return toResult(grid[r][0]);
        }
        // Kiểm tra 3 hàng dọc
        for (int c = 0; c < size; c++) {
            if (checkLine(0, c, 1, c, 2, c))
                return toResult(grid[0][c]);
        }
        // Đường chéo chính (↘)
        if (checkLine(0, 0, 1, 1, 2, 2))
            return toResult(grid[0][0]);
        // Đường chéo phụ (↙)
        if (checkLine(0, 2, 1, 1, 2, 0))
            return toResult(grid[0][2]);

        // Hòa hoặc đang chơi
        return (moveCount == size * size) ? GameResult.DRAW : GameResult.IN_PROGRESS;
    }

    /**
     * Kiểm tra 3 ô có cùng ký hiệu không (và không EMPTY).
     * Nếu có → lưu winningLine và trả về true.
     */
    private boolean checkLine(int r1, int c1, int r2, int c2, int r3, int c3) {
        CellState a = grid[r1][c1], b = grid[r2][c2], c = grid[r3][c3];
        if (!a.isEmpty() && a == b && b == c) {
            winningLine = new int[]{r1, c1, r2, c2, r3, c3};
            return true;
        }
        return false;
    }

    private GameResult toResult(CellState winner) {
        return winner == CellState.X ? GameResult.X_WIN : GameResult.O_WIN;
    }

    // ==================== THÔNG TIN BÀN CỜ ====================

    public CellState getCell(int row, int col) {
        return grid[row][col];
    }

    public boolean isEmpty(int row, int col) {
        return grid[row][col].isEmpty();
    }

    public boolean isInBounds(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }

    public int getMoveCount()  { return moveCount;   }
    public int getSize()       { return size;        }
    public int[] getWinningLine() { return winningLine; }

    public boolean isFull() { return moveCount == size * size; }

    /**
     * Lấy danh sách tất cả ô trống (dùng cho AI).
     */
    public List<Move> getAvailableMoves(CellState forPlayer) {
        List<Move> moves = new ArrayList<>();
        for (int r = 0; r < size; r++)
            for (int c = 0; c < size; c++)
                if (grid[r][c].isEmpty())
                    moves.add(new Move(r, c, forPlayer));
        return moves;
    }

    /**
     * In bàn cờ ra console (debug).
     */
    public void printToConsole() {
        System.out.println("  0 1 2");
        for (int r = 0; r < size; r++) {
            System.out.print(r + " ");
            for (int c = 0; c < size; c++) {
                System.out.print(grid[r][c].getSymbol());
                if (c < size - 1) System.out.print("|");
            }
            System.out.println();
            if (r < size - 1) System.out.println("  -----");
        }
    }
}
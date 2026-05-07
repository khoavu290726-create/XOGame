package model;

/**
 * Lớp Value Object đại diện cho một nước đi.
 * Lưu thông tin: vị trí (row, col) và ai đánh (CellState).
 *
 * Dùng làm lịch sử nước đi và tham số truyền vào các hàm AI.
 *
 * @author SinhVienCNTT
 */
public class Move {

    private final int row;
    private final int col;
    private final CellState player;

    public Move(int row, int col, CellState player) {
        this.row    = row;
        this.col    = col;
        this.player = player;
    }

    // Constructor không cần player (dùng trong AI tìm nước đi tốt nhất)
    public Move(int row, int col) {
        this(row, col, CellState.EMPTY);
    }

    public int getRow()        { return row;    }
    public int getCol()        { return col;    }
    public CellState getPlayer() { return player; }

    /**
     * Kiểm tra 2 Move có cùng vị trí không.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Move)) return false;
        Move m = (Move) o;
        return row == m.row && col == m.col;
    }

    @Override
    public int hashCode() {
        return 31 * row + col;
    }

    @Override
    public String toString() {
        return String.format("Move[%s tại (%d,%d)]", player, row, col);
    }
}
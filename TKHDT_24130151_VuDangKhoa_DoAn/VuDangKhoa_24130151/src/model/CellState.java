package model;

/**
 * Enum biểu diễn trạng thái của một ô trong bàn cờ.
 *
 * Dùng enum thay vì int/String để:
 * - Đảm bảo type safety (compile-time check)
 * - Code dễ đọc hơn
 * - Tránh magic number/string
 *
 * @author SinhVienCNTT
 */
public enum CellState {

    EMPTY("  ", " "),
    X("X", "X"),
    O("O", "O");

    /** Ký hiệu hiển thị trên bảng text */
    private final String display;

    /** Ký hiệu ngắn gọn */
    private final String symbol;

    CellState(String display, String symbol) {
        this.display = display;
        this.symbol  = symbol;
    }

    public String getDisplay() { return display; }
    public String getSymbol()  { return symbol;  }

    /**
     * Trả về đối thủ của ký hiệu hiện tại.
     * X → O, O → X, EMPTY → EMPTY
     */
    public CellState opponent() {
        if (this == X) return O;
        if (this == O) return X;
        return EMPTY;
    }

    public boolean isEmpty() { return this == EMPTY; }

    @Override
    public String toString() { return symbol; }
}
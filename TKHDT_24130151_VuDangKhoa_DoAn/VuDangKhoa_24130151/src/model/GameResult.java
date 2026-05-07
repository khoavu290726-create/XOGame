package model;

/**
 * Enum biểu diễn kết quả của ván chơi.
 *
 * @author SinhVienCNTT
 */
public enum GameResult {

    IN_PROGRESS("Đang chơi"),
    X_WIN("X thắng!"),
    O_WIN("O thắng!"),
    DRAW("Hòa!");

    private final String description;

    GameResult(String description) {
        this.description = description;
    }

    public String getDescription() { return description; }

    /** Kiểm tra game đã kết thúc chưa */
    public boolean isFinished() {
        return this != IN_PROGRESS;
    }

    /** Kiểm tra có người thắng không (không phải hòa) */
    public boolean hasWinner() {
        return this == X_WIN || this == O_WIN;
    }

    @Override
    public String toString() { return description; }
}
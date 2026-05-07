package exception;

/**
 * Exception tùy chỉnh khi người chơi thực hiện nước đi không hợp lệ.
 * Ví dụ: đánh vào ô đã có ký hiệu, hoặc ô ngoài phạm vi bàn cờ.
 *
 * @author SinhVienCNTT
 */
public class InvalidMoveException extends Exception {

    private final int row;
    private final int col;

    public InvalidMoveException(String message, int row, int col) {
        super(message);
        this.row = row;
        this.col = col;
    }

    public InvalidMoveException(String message) {
        super(message);
        this.row = -1;
        this.col = -1;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }

    @Override
    public String toString() {
        if (row >= 0) {
            return "InvalidMoveException tại ô [" + row + "][" + col + "]: " + getMessage();
        }
        return "InvalidMoveException: " + getMessage();
    }
}
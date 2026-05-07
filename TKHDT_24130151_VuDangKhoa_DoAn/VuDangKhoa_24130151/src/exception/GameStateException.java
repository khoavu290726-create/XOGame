package exception;

/**
 * Exception khi game ở trạng thái không hợp lệ.
 * Ví dụ: thực hiện nước đi khi game đã kết thúc.
 *
 * @author SinhVienCNTT
 */
public class GameStateException extends RuntimeException {

    public GameStateException(String message) {
        super(message);
    }

    public GameStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
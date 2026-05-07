package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Lớp lưu trữ thông tin của một phiên chơi (một ván game).
 * Dùng để ghi log hoặc hiển thị lịch sử.
 *
 * @author SinhVienCNTT
 */
public class GameSession {

    private final int sessionId;
    private final String playerXName;
    private final String playerOName;
    private final GameResult result;
    private final int totalMoves;
    private final LocalDateTime playedAt;
    private final GameMode mode;

    public GameSession(int sessionId, String playerXName, String playerOName,
                       GameResult result, int totalMoves, GameMode mode) {
        this.sessionId   = sessionId;
        this.playerXName = playerXName;
        this.playerOName = playerOName;
        this.result      = result;
        this.totalMoves  = totalMoves;
        this.mode        = mode;
        this.playedAt    = LocalDateTime.now();
    }

    public int getSessionId()      { return sessionId;   }
    public String getPlayerXName() { return playerXName; }
    public String getPlayerOName() { return playerOName; }
    public GameResult getResult()  { return result;      }
    public int getTotalMoves()     { return totalMoves;  }
    public GameMode getMode()      { return mode;        }
    public LocalDateTime getPlayedAt() { return playedAt; }

    public String getWinnerName() {
        if (result == GameResult.X_WIN) return playerXName;
        if (result == GameResult.O_WIN) return playerOName;
        return "Hòa";
    }

    @Override
    public String toString() {
        String time = playedAt.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        return String.format("[Ván #%d | %s | %s vs %s | Kết quả: %s | %d nước | %s]",
                sessionId, time, playerXName, playerOName, result.getDescription(),
                totalMoves, mode.getLabel());
    }
}